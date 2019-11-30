package com.example.msc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;


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

        LocationListener userLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                10,
                userLocationListener);

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

        locationManager.removeUpdates(userLocationListener);

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

            TaskLocations.taskLocations.put(taskDescription, selectedLocation);

            startActivity(addTaskIntent);
        }

    }

}

//TODO: Kill activity after onClick
