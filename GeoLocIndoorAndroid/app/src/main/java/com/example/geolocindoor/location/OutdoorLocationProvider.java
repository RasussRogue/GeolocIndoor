package com.example.geolocindoor.location;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import timber.log.Timber;

public final class OutdoorLocationProvider implements LocationProvider {

    private final List<BiConsumer<Location, Integer>> observers;
    private FusedLocationProviderClient fusedLocationClient;
    private final LocationRequest locationRequest;
    private final LocationCallback locationCallback;

    private static final int REQUEST_INTERVAL = 2000;
    private static final int REQUEST_FASTEST_INTERVAL = 1000;
    private static final int DEFAULT_FLOOR_LEVEL = 0;


    OutdoorLocationProvider(@NonNull Context context) {

        this.observers = new ArrayList<>();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        this.locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(REQUEST_INTERVAL)
                .setFastestInterval(REQUEST_FASTEST_INTERVAL);

        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Timber.i("Outdoor position received ...");
                observers.forEach(c -> c.accept(locationResult.getLastLocation(), DEFAULT_FLOOR_LEVEL));
            }
        };
    }

    @Override
    public void start() {
        Timber.i("Starting OutdoorLocationProvider ...");
        this.fusedLocationClient.requestLocationUpdates(this.locationRequest, this.locationCallback, null);
    }

    @Override
    public void stop() {
        this.fusedLocationClient.removeLocationUpdates(this.locationCallback);
        Timber.i("Stopped OutdoorLocationProvider");
    }

    @Override
    public OutdoorLocationProvider addObserver(@NonNull BiConsumer<Location, Integer> observer){
        this.observers.add(observer);
        return this;
    }

    @Override
    public OutdoorLocationProvider removeObserver(@NonNull BiConsumer<Location, Integer> observer){
        this.observers.remove(observer);
        return this;
    }

}
