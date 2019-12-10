package com.example.msc;

import android.Manifest;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class BackgroundLocationService extends Service {

    public static String stopForeground = "false";
    public static String startForeground = "true";

    private FusedLocationProviderClient mFusedLocationClient; // client that enables position updates
    private Location latestUserLocation; // updated current user location
    private LocationCallback locationCallback; // location updates
    private LocationRequest mLocationRequest; // background

    // TODO: implement geofence

    public static boolean isRunning = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(startForeground)) {
            isRunning = true;
            Log.d("myApp", "onStartCommand: invoked");

            Intent notificationIntent = new Intent(this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);


            // todo: rename

            Notification notification =
                    new Notification.Builder(this, "11")
                            .setContentTitle("notification title")
                            .setContentText("notification text")
                            .setSmallIcon(R.drawable.icon)
                            .setContentIntent(pendingIntent)
                            .setTicker(getText(R.string.app_name))
                            .build();

            startForeground(11, notification);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            // TODO: FOR BACKGROUND
            // mandatory to start location updates
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000); // 10 seconds interval
            mLocationRequest.setFastestInterval(10000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


            // creates a location callback that allows the tracking of the user location
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        Log.d("myApp", "SERVICELOCATION CHANGED "+location);
                    }
                };
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    locationCallback,
                    Looper.getMainLooper());


        }

        else if (intent.getAction().equals(stopForeground)) {
            isRunning = false;
            Log.d("myApp", "foreground stopped");
            //your end servce code
            //stopForeground(true);
            stopSelf();
        }

        return START_STICKY; //super.onStartCommand(intent, flags, startId);


    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
