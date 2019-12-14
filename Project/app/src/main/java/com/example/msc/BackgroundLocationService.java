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
import android.content.IntentSender;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executor;


public class BackgroundLocationService extends Service {

    public static String stopForeground = "false";
    public static String startForeground = "true";
    private final int locationPermission = 14;

    private FusedLocationProviderClient mFusedLocationClient; // client that enables position updates
    private Location latestUserLocation; // updated current user location
    private LocationCallback locationCallback; // location updates
    private LocationRequest mLocationRequest; // background

    public GeofencingClient geofencingClient; // geofence
    private PendingIntent geofencePendingIntent;
    public static ArrayList<Geofence> geofenceArrayList = new ArrayList<Geofence>();

    // TODO: implement geofence

    public static boolean isRunning = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) { // fixes the issue that app stops if location permission is revoked after service has been started
             if ((ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                isRunning = false;
                Log.d("myApp", "foreground stopped");
                stopForeground(true);
                stopSelf();
            }
        } else {

        if (intent.getAction().equals(startForeground)) {
            isRunning = true;
            Log.d("myApp", "onStartCommand: invoked");


            Log.d("myApp", ""+geofenceArrayList);

            Intent notificationIntent = new Intent(this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);


            Notification foregroundNotification =
                    new Notification.Builder(this, "38")
                            .setContentTitle("Background Location")
                            .setContentText("MSC uses background location.")
                            .setSmallIcon(R.drawable.icon)
                            .setContentIntent(pendingIntent)
                            .setTicker(getText(R.string.app_name))
                            .build();

            startForeground(11, foregroundNotification);

            geofencingClient = LocationServices.getGeofencingClient(this);

            if (!geofenceArrayList.isEmpty()) {
                geofenceArrayList = new ArrayList<Geofence>();
                populateGeofenceList();
                removeGeofences(geofencingClient);
                addGeofences(geofencingClient);
                Log.d("myApp", ""+geofenceArrayList);
                Log.d("myApp", ""+TaskLocations.taskLocations);
            } else if (!(TaskLocations.taskLocations.size()==0)) {
                populateGeofenceList();
                removeGeofences(geofencingClient);
                addGeofences(geofencingClient);
                Log.d("myApp", ""+geofenceArrayList);
                Log.d("myApp", ""+TaskLocations.taskLocations);
            }



            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

                        if (!geofenceArrayList.isEmpty()) {
                            geofenceArrayList = new ArrayList<Geofence>();
                            if (!(TaskLocations.taskLocations.size()==0)){
                            populateGeofenceList();
                            removeGeofences(geofencingClient);
                            addGeofences(geofencingClient);
                            Log.d("myApp", ""+geofenceArrayList);
                            Log.d("myApp", ""+TaskLocations.taskLocations);}
                        } else if (TaskLocations.taskLocations.size()==0) {
                            Log.d("myApp", ""+TaskLocations.taskLocations);
                            Log.d("myApp", ""+geofenceArrayList);
                            return;
                        } else {
                            populateGeofenceList();
                            removeGeofences(geofencingClient);
                            addGeofences(geofencingClient);
                            Log.d("myApp", ""+geofenceArrayList);
                            Log.d("myApp", ""+TaskLocations.taskLocations);

                        }

                    }
                };
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    locationCallback,
                    Looper.getMainLooper());


        }

        else if (intent.getAction().equals(stopForeground) || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            isRunning = false;
            Log.d("myApp", "foreground stopped");
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY; //super.onStartCommand(intent, flags, startId);

        } return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // TODO: Geofences
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);//ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofenceArrayList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent geoIntent = new Intent(this, TaskGeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, geoIntent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void addGeofences(GeofencingClient geofencingClient) {
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent());

    }

    public void removeGeofences(GeofencingClient geofencingClient) {
        geofencingClient.removeGeofences(getGeofencePendingIntent());

    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : TaskLocations.taskLocations.entrySet()) {
            geofenceArrayList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            TaskLocations.GEOFENCE_RADIUS)
                    .setExpirationDuration(TaskLocations.GEOFENCE_EXPIRATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }


}
