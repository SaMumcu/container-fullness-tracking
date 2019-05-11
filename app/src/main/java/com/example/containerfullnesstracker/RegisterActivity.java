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

import com.example.containerfullnesstracker.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText userPasswordET, usernameET;
    TextView loginTV;
    Button registerB;
    RelativeLayout relativeLayout;
    private BroadcastReceiver MyReceiver = null;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        usernameET = findViewById(R.id.usernameET);
        userPasswordET = findViewById(R.id.userPasswordET);
        relativeLayout = findViewById(R.id.register);
        registerB = findViewById(R.id.registerB);
        loginTV = findViewById(R.id.loginTV);

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        MyReceiver = new MyReceiver() {
            @Override
            protected void onNetworkChange(String m) {
                Snackbar snackbar = Snackbar.make(relativeLayout, m, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        };
        broadcastIntent();

        registerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameET.getText().toString().trim();
                String password = userPasswordET.getText().toString().trim();

                String key = databaseReference.push().getKey();
                User user = new User(key, username, password);
                databaseReference.child(key).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar snackbar = Snackbar.make(relativeLayout, "User added.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
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
