package com.example.msc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TaskLocations {
    public static final long GEOFENCE_EXPIRATION = 100 * 24 * 60 * 60 * 1000; // geofence persists for 100 days
    public static final float GEOFENCE_RADIUS = 200;


    public static HashMap<String, LatLng> taskLocations = new HashMap<String, LatLng>();

   // public static ArrayList<Geofence> geofenceArrayList = new ArrayList<Geofence>();


}
