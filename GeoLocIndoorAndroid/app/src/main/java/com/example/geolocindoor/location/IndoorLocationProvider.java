package com.example.geolocindoor.location;

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Beacon;
import com.arxit.geolocindoor.common.entities.Building;
import com.example.geolocindoor.trilateration.TrilaterationHandler;
import com.example.geolocindoor.utils.PreferenceUtils;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import timber.log.Timber;

public final class IndoorLocationProvider implements LocationProvider {

    private final List<BiConsumer<Location, Integer>> observers;
    private final Map<String, IBeaconDevice> knownBeacons;
    private Map<String, Beacon> buildingBeacons;
    private Beacon closestBeacon;
    private TrilaterationHandler trilat;
    private Location location = new Location("");
    private Location lastNotified = new Location("");
    private List<Location> lastLocations = new ArrayList<>();
    private int counter = 0;
    private Location newLocation = new Location("");
    private Handler handler = new Handler();
    private float biggestDistance = 0;
    private boolean positioningMode;

    private long lastIndoorTime;
    private static final int WAIT_BEFORE_QUIT_INDOOR_MODE_MILLIS = 6000;

    IndoorLocationProvider(@NonNull Context context) {
        this.observers = new ArrayList<>();
        this.knownBeacons = new HashMap<>();
        this.trilat = TrilaterationHandler.create();
        this.positioningMode = PreferenceUtils.isTrilaterationMode(context);
        this.buildingBeacons = new HashMap<>();
    }

    void setBuilding(@NonNull Building building) {
        this.buildingBeacons = building.getBeacons();
    }

    void addKnownBeacon(@NonNull IBeaconDevice b) {
        if (!this.buildingBeacons.containsKey(b.getUniqueId())) {
            return;
        }
        this.knownBeacons.put(b.getUniqueId(), b);
        this.updateLocation();
        this.refreshLastIndoorTime();
    }

    void removeKnownBeacon(@NonNull IBeaconDevice b) {
        this.knownBeacons.remove(b.getUniqueId());
        this.updateLocation();
    }

    void updateKnownBeacons(@NonNull List<IBeaconDevice> beacons) {
        beacons.forEach(b -> this.knownBeacons.replace(b.getUniqueId(), b));
        this.updateLocation();
        this.refreshLastIndoorTime();
    }

    private void refreshLastIndoorTime(){
        this.lastIndoorTime = System.currentTimeMillis();
    }

    private void updateLocation(){

        if (!this.canComputeIndoorLocation() || !this.positioningMode){
            return;
        }

        double[] result = trilat.solve(this.knownBeacons, this.buildingBeacons);
        location.setLatitude(result[1]);
        location.setLongitude(result[0]);
        lastLocations.add(location);
    }

    @Override
    public void start() {
        Timber.i("Starting IndoorLocationProvider ...");
        this.running = true;
        this.refreshLastIndoorTime();
        refreshPosition();
    }

    @Override
    public void stop() {
        this.running = false;
        Timber.i("Stopped IndoorLocationProvider");
    }

    @Override
    public IndoorLocationProvider addObserver(@NonNull BiConsumer<Location, Integer> observer) {
        this.observers.add(observer);
        return this;
    }

    @Override
    public IndoorLocationProvider removeObserver(@NonNull BiConsumer<Location, Integer> observer) {
        this.observers.remove(observer);
        return this;
    }

    boolean canComputeIndoorLocation() {
        return this.knownBeacons.size() >= 3;
    }

    boolean isTimeoutFinished(){
        return this.lastNotified != null && System.currentTimeMillis() - this.lastIndoorTime > WAIT_BEFORE_QUIT_INDOOR_MODE_MILLIS;
    }

    private boolean running = false;

    private void refreshPosition() {
        handler.postDelayed(() -> {
            if (lastNotified == null) {
                notifyPosition();
            }
            lastLocations.add(location);
            counter++;
            if (counter == 30) {
                notifyPosition();
                counter = 0;
            }
            if (this.running){
                refreshPosition();
            }
        }, 50);
    }

    private void notifyPosition() {
        Optional<IBeaconDevice> opt = this.knownBeacons.values().stream().min(Comparator.comparing(RemoteBluetoothDevice::getDistance));
        if (opt.isPresent()) {
            this.refreshLastIndoorTime();
            IBeaconDevice closestDevice = opt.get();
            closestBeacon = this.buildingBeacons.get(closestDevice.getUniqueId());

            if (!this.positioningMode){
                location.setLatitude(Objects.requireNonNull(closestBeacon).getLatitude());
                location.setLongitude(closestBeacon.getLongitude());
                observers.forEach(c -> c.accept(location, closestBeacon.getFloor().getLevel()));
                return;
            }

            if (lastLocations.size() < 1) return;
            Timber.i("%s positions used to compute current location", lastLocations.size());
            double averageDistance = 0;

            List<Location> staticCopy = lastLocations;
            lastLocations = new ArrayList<>();

            double longitude = 0;
            double latitude = 0;
            for (Location l : staticCopy) {
                averageDistance += lastNotified.distanceTo(l);
            }
            averageDistance /= staticCopy.size();
            if (averageDistance < 2) {
                lastNotified.setAccuracy((float) averageDistance);
                observers.forEach(c -> c.accept(lastNotified, closestBeacon.getFloor().getLevel()));
                counter += 20;
                return;
            }

            for (Location l : staticCopy) {
                if (l.distanceTo(lastNotified) > averageDistance * 1.1 || l.distanceTo(lastNotified) < averageDistance * 0.9) {
                    staticCopy.remove(l);
                    continue;
                }
                longitude += l.getLongitude();
                latitude += l.getLatitude();
            }

            longitude /= staticCopy.size();
            latitude /= staticCopy.size();

            newLocation.setLongitude(longitude);
            newLocation.setLatitude(latitude);

            for (Location l : staticCopy) {
                double d = newLocation.distanceTo(l);
                if (d > biggestDistance) {
                    biggestDistance = (float) d;
                }
            }
            newLocation.setAccuracy(biggestDistance);
            biggestDistance = 0;
            observers.forEach(c -> c.accept(newLocation, closestBeacon.getFloor().getLevel()));
            lastNotified = newLocation;
        }
    }

    void setPositioningMode(boolean positioningMode) {
        this.positioningMode = positioningMode;
    }
}
