package com.example.containerfullnesstracker;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.containerfullnesstracker.adapter.CustomInfoWindowAdapter;
import com.example.containerfullnesstracker.model.Container;
import com.example.containerfullnesstracker.viewmodel.ContainerViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocaton;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    double latitude,longitude;
    ContainerViewModel containerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        containerViewModel= ViewModelProviders.of(this).get(ContainerViewModel.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }
        checkUserLocationPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
    }


    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            setGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    protected synchronized void setGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        lastLocaton = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(TrackingActivity.this));
        containerViewModel.getContainers().observe(this, new Observer<List<Container>>() {
            @Override
            public void onChanged(@Nullable List<Container> containers) {
                for (int i=0;i<containers.size();i++) {

                    Container container = containers.get(i);
                    LatLng latLng1=new LatLng(container.getLatitude(),container.getLongitude());

                    Marker marker=mMap.addMarker(new MarkerOptions()
                            .position(latLng1)
                            .title("Fullness rate: "+container.getOccupancyRate()+" Temp: "+container.getTemperature())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    marker.setTag(container.getContainerId());
                    marker.showInfoWindow();


                }
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                Toast.makeText(TrackingActivity.this, "window clicked "+marker.getTag(), Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(TrackingActivity.this);
                dialog.setContentView(R.layout.change_occupancy_rate);

                final EditText lat = dialog.findViewById(R.id.lat);
                final EditText lon=dialog.findViewById(R.id.lon);
                Button apply=dialog.findViewById(R.id.apply);

                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String containerId=marker.getTag().toString();
                        containerViewModel.updateLocation(containerId,Double.parseDouble(lat.getText().toString()),Double.parseDouble(lon.getText().toString()));

                        Toast.makeText(TrackingActivity.this, "New occupancyRate is "
                                + lat.getText().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        mMap.clear();

                    }
                });
                dialog.show();
            }
        });


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(TrackingActivity.this,""+
                location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(15));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1100);
        mLocationRequest.setFastestInterval(1100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
