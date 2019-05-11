package com.example.containerfullnesstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

public abstract class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        if(status.isEmpty()) {
            status="No Internet Connection";
        }

        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        onNetworkChange(status);
    }

    protected abstract void onNetworkChange(String message);
}
