package com.example.msc;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.amitshekhar.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.support.v4.app.ServiceCompat.stopForeground;
import static com.example.msc.BackgroundLocationService.stopForeground;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedPosition = null;
    private final int locationPermission = 14;
    private LocationRequest mLocationRequest; // background
    private Marker userMarker;
    private LocationManager locationManager;
    private LocationListener userLocationListener;

    private GeofencingClient geofencingClient; // geofence
    private PendingIntent geofencePendingIntent; //geofence
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        broadcastReceiver = new TaskGeofenceBroadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter());

        stopService(MainActivity.foregroundService);




        // TODO: FOR BACKGROUND
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000); // 3 seconds interval
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        checkPermission();
    }


    // TODO: Geofences
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(TaskLocations.geofenceArrayList);
        return builder.build();
    }

    // TODO: geofences (pasted)
    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

            // circle that visualizes the Geofence
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(entry.getValue().latitude, entry.getValue().longitude))
                    .radius(200)
                    .strokeColor(Color.parseColor("#2271cce7"))
                    .fillColor(Color.parseColor("#2271cce7")));


        }

        checkPermission();

        userLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (userMarker != null) {
                    userMarker.remove();
                }
                userMarker = mMap.addMarker(new MarkerOptions().position(
                        (new LatLng(location.getLatitude(), location.getLongitude())))
                        .title("User's Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.obesity)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(),
                                location.getLongitude()), 16.0f));
                Log.d("myApp", "onLocationChanged: " + location);


                if (!TaskLocations.geofenceArrayList.isEmpty()) {
                    addGeofences(geofencingClient);
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 10, userLocationListener);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        /*
         * Moves camera to the newly created task if activity is called through intent. If activity
         * is called naturally, camera moves to the user's location.
         */

        Bundle locationBundle = getIntent().getExtras();
        if (locationBundle != null) {

            selectedPosition = getIntent().getExtras().getParcelable("SelectedLocation");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPosition, 16.0f));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude()), 16.0f));
        }

        userMarker = mMap.addMarker(new MarkerOptions().position(
                (new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())))
                .title("User's Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude()), 16.0f));


    }

    @Override
    protected void onResume() {
        super.onResume();
        // stop foreground service

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
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        // start foreground service

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates(userLocationListener);
    }

    private void addGeofences(GeofencingClient geofencingClient) {
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                        Log.d("myApp", "onSuccess: ");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                        Log.d("myApp", "onFailure: ");
                    }
                });
    }
}
