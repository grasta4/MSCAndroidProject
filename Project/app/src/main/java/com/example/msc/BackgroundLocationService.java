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
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

import static android.arch.lifecycle.Lifecycle.State.RESUMED;

public class BackgroundLocationService extends Service {

    public static String stopForeground = "false";
    public static String startForeground = "true";

    private LocationManager locationManager;
    private LocationListener userLocationListener;

    // TODO: implement geofence


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(startForeground)) {

            Log.d("myApp", "onStartCommand: invoked");

            Intent notificationIntent = new Intent(this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);


            // todo: rename
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("My Awesome App")
                    .setContentText("Doing some work...")
                    .setContentIntent(pendingIntent).build();

            startForeground(1337, notification);

            userLocationListener = new LocationListener() {
                public void onLocationChanged(Location location) {

                    Log.d("myApp", "onLocationChangedSERVICE: " + location);


                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };


            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, userLocationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("myApp", "onHandleIntent: lastKnownlocation" + lastKnownLocation);
        }

        else if (intent.getAction().equals(stopForeground)) {
            Log.d("myApp", "foreground stopped");
            //your end servce code
            stopForeground(true);
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
