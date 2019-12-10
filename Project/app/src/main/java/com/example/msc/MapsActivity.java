package com.example.msc;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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

import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedPosition = null; // maybe not necessary
    private final int locationPermission = 14;
    private final int backgroundPermission = 13;
    private LocationRequest mLocationRequest; // background
    private Marker userMarker;

    private GeofencingClient geofencingClient; // geofence
    private PendingIntent geofencePendingIntent; //geofence
    private BroadcastReceiver broadcastReceiver;

  //  boolean permissionAccessCoarseLocationApproved =
    //        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
      //              == PackageManager.PERMISSION_GRANTED; // permissions

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

        //todo: geofences

        geofencingClient = LocationServices.getGeofencingClient(this);
        if (! TaskLocations.geofenceArrayList.isEmpty()) {
            addGeofences(geofencingClient);
        }

        // todo: ask for background permission
        /*
        if (permissionAccessCoarseLocationApproved) {
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;

            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
            } else {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        13);
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    14);
        }

         */

        // for geofencing
        broadcastReceiver = new TaskGeofenceBroadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter());


        // todo: for background
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latestUserLocation = location;
                            Log.d("myApp", "onSuccess: fused locationprov" + latestUserLocation.getLatitude());
                        }
                    }
                });



        // TODO: FOR BACKGROUND
        // mandatory to start location updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000); // 4 seconds interval
        mLocationRequest.setFastestInterval(4000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // creates a location callback that allows the tracking of the user location
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (userMarker != null) {
                        userMarker.remove();
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

                    if (! TaskLocations.geofenceArrayList.isEmpty()) {
                        addGeofences(geofencingClient);
                    }
                }
            };
        };


      //  checkPermission();
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
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
        Intent intent = new Intent(this, TaskGeofenceBroadcastReceiver.class);
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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))); // takes custom icon

            // circle that visualizes the Geofence
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(entry.getValue().latitude, entry.getValue().longitude))
                    .radius(200)
                    .strokeColor(Color.parseColor("#2271cce7"))
                    .fillColor(Color.parseColor("#2271cce7")));


        }

      //  checkPermission();


        // todo: deprecated location
        /*
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


                //if (! TaskLocations.geofenceArrayList.isEmpty()) {
                //    addGeofences(geofencingClient);
               // }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

         */


        // todo: deprecated location
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 10, userLocationListener);

        //Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);




        /*
         * Moves camera to the newly created task if activity is called through intent. If activity
         * is called naturally, camera moves to the user's location.
         */


        // todo: maybe remove
        Bundle locationBundle = getIntent().getExtras();
        if (locationBundle != null) {
            selectedPosition = getIntent().getExtras().getParcelable("SelectedLocation");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPosition, 16.0f));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // stop foreground service
        startLocationUpdates();

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


    // todo: geofences
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
