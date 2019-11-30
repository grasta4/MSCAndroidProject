package com.example.msc;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class TaskLocations {
    public static final long GEOFENCE_EXPIRATION = 100 * 24 * 60 * 60 * 1000; // geofence persists for 100 days
    public static final float GEOFENCE_RADIUS = 200;

    public static HashMap<String, LatLng> taskLocations = new HashMap<String, LatLng>();

}
