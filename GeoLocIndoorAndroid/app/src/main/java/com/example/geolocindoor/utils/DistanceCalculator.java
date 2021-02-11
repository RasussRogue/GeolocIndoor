package com.example.geolocindoor.utils;

import android.location.Location;

import com.arxit.geolocindoor.common.entities.Pointable;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class DistanceCalculator {

    private static final LatLng a = new LatLng(0, 0);
    private static final LatLng b = new LatLng(0, 0);

    public static double distance(Pointable p1, Pointable p2){
        a.setLatitude(p1.getLatitude());
        a.setLongitude(p1.getLongitude());
        b.setLatitude(p2.getLatitude());
        b.setLongitude(p2.getLongitude());
        return a.distanceTo(b);
    }

    public static double distance(Pointable p1, Location p2){
        a.setLatitude(p1.getLatitude());
        a.setLongitude(p1.getLongitude());
        b.setLatitude(p2.getLatitude());
        b.setLongitude(p2.getLongitude());
        return a.distanceTo(b);
    }
}
