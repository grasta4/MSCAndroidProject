package com.example.msc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedPosition = null;
    private final int locationPermission = 14;
    private Location currentUserPosition;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    protected ArrayList<Geofence> geofenceArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkPermission();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        geofenceArrayList = new ArrayList<Geofence>();

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
            // locations from TaskLocations are added into a list that handles all the geofences
            geofenceArrayList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            TaskLocations.GEOFENCE_RADIUS
                    )
                    .setExpirationDuration(TaskLocations.GEOFENCE_EXPIRATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            // marker that shows the location
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(entry.getValue().latitude, entry.getValue().longitude))
                    .title(entry.getKey()));
            // circle that visualizes the Geofence
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(entry.getValue().latitude, entry.getValue().longitude))
                    .radius(200)
                    .strokeColor(Color.parseColor("#2271cce7"))
                    .fillColor(Color.parseColor("#2271cce7")));
        }

        checkPermission();

        LocationListener userLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                // TODO: to be revised
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        // Checks if a location can be fetched.
                        if (locationResult.getLastLocation() == null) {
                            return;
                        }
                        currentUserPosition = locationResult.getLastLocation();
                        LatLng latLng = new LatLng(currentUserPosition.getLatitude(), currentUserPosition.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                        mMap.addMarker(new MarkerOptions().position(latLng).title("testtstt"));
                        }
                };

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // TODO: to be revised
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000); // 3 seconds interval
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        // marker of the user's position
        mMap.addMarker(new MarkerOptions().position(
                (new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())))
                .title("User's Location"));



    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: to be revised
        if (checkForGooglePlayService()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            currentLocationUpdater();
        }
    }

    // TODO: to be revised
    private void currentLocationUpdater() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // check for location every 4 sec
        locationRequest.setInterval(4000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
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

    // TODO: to be revised
    private boolean checkForGooglePlayService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

}

// TODO: Check if the location updates and moves the marker
