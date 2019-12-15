package com.example.msc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class TaskGeofenceBroadcastReceiver extends BroadcastReceiver {
    
    private String TAG = "myApp";


    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            String group = "groupedNotifications";
            Integer id;


            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){ //||
             //       geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                List<Geofence> geofenceTransitions = geofencingEvent.getTriggeringGeofences();

                for (Geofence geofence : geofenceTransitions) {
                    id = TaskLocations.locationID.indexOf(geofence.getRequestId());
                    Log.d(TAG, "onReceive: "+id);
                    Log.d(TAG, "triggered "+geofence.getRequestId());
                    sendNotification(context, geofence, geofence.getRequestId(), id, group, notificationManagerCompat);
                }

            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                List<Geofence> geofenceTransitions = geofencingEvent.getTriggeringGeofences();

                for (Geofence geofence : geofenceTransitions) {
                    id = TaskLocations.locationID.indexOf(geofence.getRequestId());
                    Log.d(TAG, "onReceive: "+id);
                    Log.d(TAG, "triggered "+geofence.getRequestId());
                    deleteNotification(id, notificationManagerCompat);
                }

            }


    }


    private void sendNotification(Context context, Geofence geofence, String channel, Integer id, String group, NotificationManagerCompat notificationManagerCompat) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "channel");

        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, openAppIntent, 0);

        Intent endTaskIntent = new Intent(context, EndTaskService.class);
        endTaskIntent.putExtra("toDelete", geofence.getRequestId());
        PendingIntent deleteIntent = PendingIntent.getService(context, id, endTaskIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentTitle("Task Location Entered!" )
                .setContentText("You are near " + geofence.getRequestId())
                .setSmallIcon(R.drawable.julius)
                .setChannelId(channel)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setGroup(group)
                .addAction(R.drawable.julius, "End Task", deleteIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManagerCompat.notify(id, notificationBuilder.build());

        Log.d(TAG, "sendNotification: " + TaskLocations.taskLocations );

        Notification summaryNotification =
                new NotificationCompat.Builder(context, "channel")
                        .setContentTitle("Multiple Task Locations entered.")
                        //set content text to support devices running API level < 24
                        .setContentText("")
                        .setSmallIcon(R.drawable.julius)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .setSummaryText("Multiple Task Locations nearby."))
                        //specify which group this notification belongs to
                        .setGroup("groupedNotifications")
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();
        notificationManagerCompat.notify(1, summaryNotification);
    }

    private void deleteNotification(Integer id, NotificationManagerCompat notificationManagerCompat) {
        notificationManagerCompat.cancel(id);
    }


}
