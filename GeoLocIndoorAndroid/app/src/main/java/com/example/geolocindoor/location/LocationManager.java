package com.example.geolocindoor.location;

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Building;
import com.example.geolocindoor.R;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import timber.log.Timber;

public final class LocationManager implements LocationProvider {

    private final Handler handler;
    private final List<BiConsumer<Location, Integer>> observers;
    private final IndoorLocationProvider indoorProvider;
    private final OutdoorLocationProvider outdoorProvider;
    private final ProximityManager proximityManager;

    private boolean isIndoor;
    private LocationProvider currentProvider;

    private final static int TRANSITION_INDOOR_TO_OUTDOOR = 6000;
    private final Runnable swapProviderRunnable = this::swapLocationProvider;


    private static LocationManager instance;
    public static LocationManager create(@NonNull Context context){
        if (instance == null){
            instance = new LocationManager(context.getApplicationContext());
        }
        return instance;
    }

    private LocationManager(@NonNull Context context){
        this.handler = new Handler();
        this.observers = new ArrayList<>();
        this.indoorProvider = new IndoorLocationProvider(context).addObserver(this::notifyObservers);
        this.outdoorProvider = new OutdoorLocationProvider(context).addObserver(this::notifyObservers);

        KontaktSDK.initialize(context.getString(R.string.kontaktio_api_key));
        this.proximityManager = ProximityManagerFactory.create(context);
        this.proximityManager.configuration().deviceUpdateCallbackInterval(100);

        this.proximityManager.setIBeaconListener(new SimpleIBeaconListener() {

            @Override
            public void onIBeaconDiscovered(IBeaconDevice iBeacon, IBeaconRegion region) {
                Timber.i("IBeacon discovered : %s", iBeacon);
                indoorProvider.addKnownBeacon(iBeacon);
                handler.removeCallbacks(swapProviderRunnable);
                //Check if should swap to indoor mode
                if (!isIndoor && indoorProvider.canComputeIndoorLocation()){
                    swapLocationProvider();
                }
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region) {
                indoorProvider.updateKnownBeacons(iBeacons);
                handler.removeCallbacks(swapProviderRunnable);
            }

            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
                Timber.i("IBeacon lost : %s", iBeacon);
                indoorProvider.removeKnownBeacon(iBeacon);
                //Check if should swap to outdoor mode
                if (isIndoor && !indoorProvider.canComputeIndoorLocation()){
                    //swapLocationProvider();
                    handler.postDelayed(swapProviderRunnable, TRANSITION_INDOOR_TO_OUTDOOR);
                }
            }
        });

        this.isIndoor = false;
        this.currentProvider = this.outdoorProvider;
    }

    public void setBuilding(@NonNull Building building){
        this.indoorProvider.setBuilding(building);
    }

    private void swapLocationProvider(){
        this.currentProvider.stop();
        if (this.isIndoor){
            this.currentProvider = this.outdoorProvider;
        } else {
            this.currentProvider = this.indoorProvider;
        }
        this.isIndoor = !this.isIndoor;
        Timber.i("Swapping location provider to %s", (this.isIndoor ? "INDOOR" : "OUTDOOR"));
        this.currentProvider.start();
    }

    private boolean running = false;

    public boolean isRunning(){
        return this.running;
    }

    @Override
    public void start() {
        Timber.i("Starting LocationManager ...");
        this.currentProvider.start();
        this.proximityManager.connect(this.proximityManager::startScanning);
        this.running = true;
    }

    @Override
    public void stop() {
        this.currentProvider.stop();
        this.proximityManager.stopScanning();
        if (this.proximityManager.isConnected()){
            this.proximityManager.disconnect();
        }
        Timber.i("Stopped LocationManager");
        this.running = false;
    }

    @Override
    public LocationManager addObserver(@NonNull BiConsumer<Location, Integer> observer) {
        this.observers.add(observer);
        return this;
    }

    @Override
    public LocationManager removeObserver(@NonNull BiConsumer<Location, Integer> observer) {
        this.observers.remove(observer);
        return this;
    }

    private void notifyObservers(@NonNull Location location, int floorLevel){
        Timber.i("LOCATION RECEIVED (%s) : [ lon:%.4f ; lat:%.4f ; acc:%.2f ; floor:%d ]", (this.isIndoor ? "indoor" : "outdoor"),
                location.getLongitude(), location.getLatitude(), location.getAccuracy(), floorLevel);
        this.observers.forEach(c -> c.accept(location, floorLevel));
    }

    public boolean isIndoor() {
        return isIndoor;
    }

    public void setPositioningMode(boolean positioningMode) {
        this.indoorProvider.setPositioningMode(positioningMode);
    }
}