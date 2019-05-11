package com.example.containerfullnesstracker.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.containerfullnesstracker.R;
import com.example.containerfullnesstracker.model.Container;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


    private View window;
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    private void randomWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView textView = window.findViewById(R.id.occupancyRate);

        if (title.equals("")) {
            textView.setText(title);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.custom_info_window, null);
        String title = marker.getTitle();
        TextView info = view.findViewById(R.id.occupancyRate);
        Button changeLocation = view.findViewById(R.id.changeLocation);

        LinearLayout linearLayout = view.findViewById(R.id.customInfoLL);
        linearLayout.setAlpha((float) 0.8);

        info.setText(title);

        return view;
    }
}
