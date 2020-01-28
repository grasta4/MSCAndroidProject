package com.example.msc;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class TaskGeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            Integer id;

            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){

                List<Geofence> geofenceTransitions = geofencingEvent.getTriggeringGeofences();

                for (Geofence geofence : geofenceTransitions) {
                    id = TaskLocations.locationID.indexOf(geofence.getRequestId());
                    sendNotification(context, geofence, geofence.getRequestId(), id, notificationManagerCompat);
                }

            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                List<Geofence> geofenceTransitions = geofencingEvent.getTriggeringGeofences();

                for (Geofence geofence : geofenceTransitions) {
                    id = TaskLocations.locationID.indexOf(geofence.getRequestId());
                    deleteNotification(id, notificationManagerCompat);
                }
            }
    }


    private void sendNotification(Context context, Geofence geofence, String channel, Integer id, NotificationManagerCompat notificationManagerCompat) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel);

        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, openAppIntent, 0);

        Intent endTaskIntent = new Intent(context, EndTaskService.class);
        endTaskIntent.putExtra("toDelete", geofence.getRequestId());
        PendingIntent deleteIntent = PendingIntent.getService(context, id, endTaskIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentTitle("Task Location Entered!" )
                .setContentText("You are near " + geofence.getRequestId())
                .setSmallIcon(R.drawable.clipboard)
                .setChannelId(channel)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .addAction(R.drawable.clipboard, "End Task", deleteIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManagerCompat.notify(id, notificationBuilder.build());

    }

    private void deleteNotification(Integer id, NotificationManagerCompat notificationManagerCompat) {
        notificationManagerCompat.cancel(id);
    }

}
