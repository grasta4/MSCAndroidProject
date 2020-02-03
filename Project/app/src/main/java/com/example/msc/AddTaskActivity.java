package com.example.msc;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.msc.persistence.MyDatabaseAccessor;
import com.example.msc.persistence.dao.TaskDao;
import com.example.msc.ui.settings.SettingsActivity;
import com.example.msc.util.BackgroundTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.msc.persistence.entities.Task;

import java.util.concurrent.ExecutionException;


public class AddTaskActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final int locationPermission = 15; // 15 is the id given to this permission
    private LatLng selectedLocation;
    private String taskDescription = null;
    private Marker locationMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkPermission();

        // enables real-time change of the taskDescription variable
        EditText taskDescriptionParse = findViewById(R.id.eTinsertTask);
        taskDescriptionParse.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable e) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskDescription = s.toString();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        checkPermission();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkPermission();

        // this activity does not update the user location. It always takes the last location.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                selectedLocation = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                Log.d("myApp", "marker moved to" + selectedLocation);
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });

        // marker on map for the position of the task
        locationMarker = mMap.addMarker(new MarkerOptions().position(
                (new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())))
                .title("Selected Location").draggable(true));

        // default position selected for intent if marker is not moved
        selectedLocation = new LatLng(locationMarker.getPosition().latitude, locationMarker.getPosition().longitude);

        // camera moves to the user's location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 16.0f));


    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermission);
        }
    }


    public void onClickAddTask(View v) {
        if (taskDescription == null || taskDescription == "") {
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle("Missing task description")
                    .setMessage("Please provide a task description before continuing.");
            alert.setPositiveButton("OK", null);
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        } else {
            Intent addTaskIntent = new Intent(this, MapsActivity.class);

            Bundle locationBundle = new Bundle();
            // puts location to the intent to move camera when creating a new task
            locationBundle.putParcelable("SelectedLocation", selectedLocation);
            addTaskIntent.putExtras(locationBundle);
            Log.d("myApp", taskDescription);

            // stores task location and description
            TaskLocations.taskLocations.put(taskDescription, selectedLocation);
            TaskLocations.locationID.add(taskDescription);
            startActivity(addTaskIntent);

            createNotificationChannel(taskDescription);

            String addtaskQuery = "";

            try {
                addtaskQuery = new BackgroundTask<>(() -> {
                    final TaskDao taskDao = MyDatabaseAccessor.getInstance(this.getApplicationContext()).getTaskDao();

                    final Task task = taskDao.getTaskByName(taskDescription);
                    if (task != null) {
                        return "Task already created";
                    }

                    taskDao.AddTask(new Task(taskDescription, selectedLocation.latitude, selectedLocation.longitude, SettingsActivity.U_NAME));
                    return "Task added";


                }).execute().get();
            } catch (final ExecutionException | InterruptedException exception) {
                exception.printStackTrace();
            }
            Toast.makeText(this, addtaskQuery, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void createNotificationChannel(String channelName) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelName, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
