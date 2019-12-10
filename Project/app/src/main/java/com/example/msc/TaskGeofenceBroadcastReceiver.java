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


    @Override
    public void onReceive(Context context, Intent intent) {

       // GeofenceService.enqueueWork(context, intent);

        /*
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d("myApp", "onReceive: geofence");

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d("myApp", "onReceive: geofence entered");


            // sendNotification(geofenceTransitionDetails);

        }

         */
    }




   // @Override
    //public void onReceive(Context context, Intent intent) {
      //  Log.d("myApp", "onReceive: ");
       // }

}
