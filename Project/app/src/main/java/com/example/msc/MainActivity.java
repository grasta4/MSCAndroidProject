package com.example.msc;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import static android.support.v4.app.ServiceCompat.stopForeground;

public class MainActivity extends AppCompatActivity {

    private final int locationPermission = 14;
    private GeofencingClient geofencingClient; // geofence
    public static Intent foregroundService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isMyServiceRunning(BackgroundLocationService.class)) {
            foregroundService = new Intent(MainActivity.this, BackgroundLocationService.class);
            stopService(foregroundService);
        } else {
            foregroundService = new Intent(MainActivity.this, BackgroundLocationService.class);
            startForegroundService(foregroundService.setAction(BackgroundLocationService.startForeground));
        }

       // foregroundService = new Intent(this, BackgroundLocationService.class);
       // foregroundService.setAction(BackgroundLocationService.startForeground);
       // startForegroundService(foregroundService);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermission);
        }
    }

    public void viewMap(View v) {
        Intent mapsIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsIntent);

    }

    public void newTask(View v) {
        Intent newTaskIntent = new Intent(this, AddTaskActivity.class);
        startActivity(newTaskIntent);
    }

    public void endTask(View v) {
        Intent endTaskIntent = new Intent(this, EndTaskActivity.class);
        startActivity(endTaskIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // checks if permissions are granted and gives a response accordingly
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case locationPermission: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("myApp", "location permission granted");

                } else {
                    Log.d("myApp", "location permission denied");
                }
                return;
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
