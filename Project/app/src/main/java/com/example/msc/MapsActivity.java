package com.example.msc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedPosition = null;
    private final int locationPermission = 14;
    private Location currentUserPosition;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker userMarker;
    private LocationCallback thisLocationCallback;

    protected ArrayList<Geofence> geofenceArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // TODO: FOR BACKGROUND
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000); // 3 seconds interval
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("testy"));
                        }
                    }
                });


        // TODO: FOR BACKGROUND
        thisLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    location = locationResult.getLastLocation();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                    if (userMarker == null)
                        userMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng).title("testy"));
                    else
                        AnimateMarker.animateMarkerToGB(userMarker, latLng, new LatLngInterpolator.Spherical());
                }
            }
        };

        checkPermission();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startLocationUpdates();
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


        LocationListener userLocationListener = new LocationListener() {
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
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        startLocationUpdates(); // TODO: FOR BACKGROUND
    }

    // TODO: FOR BACKGROUND
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                thisLocationCallback,
                Looper.getMainLooper());
    }


    // TODO: FOR BACKGROUND
    private void currentLocationUpdater() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // check for location every 4 sec
        locationRequest.setInterval(4000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, thisLocationCallback, Looper.myLooper());
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

    // TODO: FOR BACKGROUND
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

    public void returnHome(View v) {
        Intent returnIntent = new Intent(this, MainActivity.class);
        startActivity(returnIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(thisLocationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(thisLocationCallback);
    }

}

