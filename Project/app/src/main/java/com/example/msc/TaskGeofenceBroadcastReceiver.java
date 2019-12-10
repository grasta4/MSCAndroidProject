package com.example.msc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class TaskGeofenceBroadcastReceiver extends BroadcastReceiver {
    
    private String TAG = "myApp";


    @Override
    public void onReceive(Context context, Intent intent) {

        
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                Log.d(TAG, "onReceive: monitored transition");
            }

        }
    
}
