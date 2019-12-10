package com.example.msc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofenceStatusCodes;

public class GeofenceService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d("myApp", "onReceive: geofence");

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d("myApp", "onReceive: geofence entered");
        }

            return super.onStartCommand(intent, flags, startId);
        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
