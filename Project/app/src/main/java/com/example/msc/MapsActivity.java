package com.example.msc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedPosition;
    private final int locationPermission = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermission);
        }

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

        // marker of the user's position
        mMap.addMarker(new MarkerOptions().position(
                (new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())))
                .title("User's Location").draggable(true));

        // camera moves to the user's location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 16.0f));

        locationManager.removeUpdates(userLocationListener);

        // TODO: create a location mark that actually moves with the user


        selectedPosition = getIntent().getExtras().getParcelable("SelectedLocation");
        String taskDescription = getIntent().getExtras().getParcelable("TaskDescription");
        Log.d("myApp", ""+taskDescription);

        mMap.addMarker(new MarkerOptions().position(selectedPosition).title(taskDescription));

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

}
