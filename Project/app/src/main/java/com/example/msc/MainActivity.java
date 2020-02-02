package com.example.msc;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.msc.persistence.MyDatabaseAccessor;
import com.example.msc.persistence.dao.TaskDao;
import com.example.msc.persistence.entities.Task;
import com.example.msc.ui.login.LoginActivity;
import com.example.msc.ui.settings.SettingsActivity;
import com.example.msc.util.BackgroundTask;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private final int locationPermission = 14;
    public static Intent foregroundService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d("myApp", "onCreate: "+BackgroundLocationService.isRunning);

        if (BackgroundLocationService.isRunning) {
          // foregroundService = new Intent(MainActivity.this, BackgroundLocationService.class);
          //  foregroundService.setAction(BackgroundLocationService.stopForeground);
           // stopService(foregroundService);
        } else {
            createNotificationChannel();
            foregroundService = new Intent(MainActivity.this, BackgroundLocationService.class);
            startForegroundService(foregroundService.setAction(BackgroundLocationService.startForeground));
        }

        checkPermission();

        try {
            new BackgroundTask<Void>(() -> {
                final TaskDao taskDao = MyDatabaseAccessor.getInstance(this.getApplicationContext()).getTaskDao();

                final List<Task> taskList = taskDao.getTasksByUser(SettingsActivity.U_NAME);

                for (Task task : taskList) {
                    TaskLocations.taskLocations.put(task.getName(), new LatLng(task.getLatitude(), task.getLongitude()));
                }

                return null;

            }).execute().get();
        } catch (final ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
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

    public void logout(View v) {
        TaskLocations.taskLocations = new HashMap<String, LatLng>();
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        LoginManager.getInstance().logOut();
        startActivity(logoutIntent);
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

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermission);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void settings(View v) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

}
