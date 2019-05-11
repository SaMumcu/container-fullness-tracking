package com.example.containerfullnesstracker;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.containerfullnesstracker.model.Container;
import com.example.containerfullnesstracker.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //user screen
    //todo: login user
    //todo: warn the user about connectivity issues
    //todo: connectivity or user related issues must be pressented to the user properly

    EditText userPasswordET, usernameET;
    TextView registerTV;
    Button registerB;
    RelativeLayout relativeLayout;

    DatabaseReference databaseReference;

    private BroadcastReceiver MyReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = findViewById(R.id.login);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        MyReceiver = new MyReceiver() {
            @Override
            protected void onNetworkChange(String m) {
                Snackbar snackbar = Snackbar.make(relativeLayout, m, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        };
        usernameET = findViewById(R.id.usernameET);
        userPasswordET = findViewById(R.id.userPasswordET);
        registerTV = findViewById(R.id.registerTV);
        registerB = findViewById(R.id.registerB);

        broadcastIntent();
        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        registerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameET.getText().toString().trim();
                final String password = userPasswordET.getText().toString().trim();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot users : dataSnapshot.getChildren()) {

                            User user = users.getValue(User.class);
                            if (user.getUsername().toString().equals(username) &&
                                    user.getPassword().toString().equals(password)) {
                                Intent intent = new Intent(MainActivity.this, TrackingActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                databaseReference = FirebaseDatabase.getInstance().getReference("Containers");


//                                Container container = new Container("70", "15", "5-May-2019","45","45",38.481873,27.212738);
//                                databaseReference.child("45").setValue(container);
//
//                                Container container2 = new Container("60", "20", "6-May-2019","46","46",38.481505,27.212779);
//                                databaseReference.child("46").setValue(container2);
//
//                                Container container3 = new Container("40", "15", "5-May-2019","47","47",38.481354,27.214050);
//                                databaseReference.child("47").setValue(container3);
//
//                                Container container4 = new Container("15", "20", "6-May-2019","48","48",38.481062,27.214064);
//                                databaseReference.child("48").setValue(container4);
//
//                                Container container5 = new Container("10", "20", "6-May-2019","49","49",38.480762,27.214077);
//                                databaseReference.child("49").setValue(container5);
//
//                                Container container6 = new Container("30", "20", "6-May-2019","50","50",38.480878,27.213995);
//                                databaseReference.child("50").setValue(container6);


                                finish();


                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
