package com.example.geolocindoor.location;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.function.BiConsumer;

public interface LocationProvider {

    void start();

    void stop();

    LocationProvider addObserver(@NonNull BiConsumer<Location, Integer> observer);

    LocationProvider removeObserver(@NonNull BiConsumer<Location, Integer> observer);

}
