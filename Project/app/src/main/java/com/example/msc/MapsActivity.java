package com.example.msc;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedPosition = null; // maybe not necessary
    private final int locationPermission = 14;
    private LocationRequest mLocationRequest; // background
    private Marker userMarker;

    public static boolean isRunning;

    private FusedLocationProviderClient mFusedLocationClient; // client that enables position updates
    private Location latestUserLocation; // updated current user location
    private LocationCallback locationCallback; // location updates


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        isRunning = true;

        // todo: for background
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latestUserLocation = location; //sets the user location to the last fetched user location
                        }
                    }
                });



        // TODO: FOR BACKGROUND
        // mandatory to start location updates
        mLocationRequest = new LocationRequest(); // initiates a location request
        mLocationRequest.setInterval(4000); // 4 seconds interval
        mLocationRequest.setFastestInterval(4000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // THIS WILL ALWAYS BE CALLED WHEN A NEW GEOFENCE IS CREATED SO IT MAY AS WELL STAY IN HERE
        //requests to change the location settings to high accuracy
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> requestTask = client.checkLocationSettings(settingsBuilder.build());

        requestTask.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        requestTask.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                100);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


        // creates a location callback that allows the tracking of the user location
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) { //stops callback if location cant be fetched
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (userMarker != null) {
                        userMarker.remove(); //removes the marker of the previous location
                    }
                    // sets marker to the updated location
                    userMarker = mMap.addMarker(new MarkerOptions().position(
                            (new LatLng(location.getLatitude(), location.getLongitude())))
                            .title("User's Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.obesity)));

                    // animates camera to the updated location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(),
                                    location.getLongitude()), 16.0f));
                    Log.d("myApp", "updatd fused location changed to"+ location.getLatitude());

                }
            };
        };


        checkPermission();
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        /*
         * This calls the TaskLocations.class which handles all the added tasks.
         *
         * The locations stored in the HashMap get called and receive a marker and their Geofence
         * as a circle.
         *
         * Every time something changes, the locations get updated. Although the runtime is not
         * optimal, I decided to implement it like this because I expect there only to be a
         * comparably small amount of tasks so that it does not affect performance.
         */

        for (Map.Entry<String, LatLng> entry : TaskLocations.taskLocations.entrySet()) {
            // marker that shows the location
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(entry.getValue().latitude, entry.getValue().longitude))
                    .title(entry.getKey())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))); // takes custom icon

            // circle that visualizes the Geofence
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(entry.getValue().latitude, entry.getValue().longitude))
                    .radius(200)
                    .strokeColor(Color.parseColor("#2271cce7"))
                    .fillColor(Color.parseColor("#2271cce7")));


        }


        checkPermission();


        //todo: maybe remove
        /*
         * Moves camera to the newly created task if activity is called through intent. If activity
         * is called naturally, camera moves to the user's location.
         */
        Bundle locationBundle = getIntent().getExtras();
        if (locationBundle != null) {
            selectedPosition = getIntent().getExtras().getParcelable("SelectedLocation");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPosition, 16.0f));
        }

    }


    /*
     * Checks if fine location permission is granted. If not, fine location permission is requested.
     */
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermission);
        }
    }

    /*
     * If permissions are granted, they are stored into an array that saves the state of the granted
     * permissions.
     */
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


    public void returnHome(View v) {
        Intent returnIntent = new Intent(this, MainActivity.class);
        startActivity(returnIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        isRunning = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
