package com.example.geolocindoor.managers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.utils.BuildingSerializationHandler;
import com.arxit.geolocindoor.common.utils.ChecksumHandler;

import java.util.List;

public final class OfflineManager {

    private final SharedPreferences prefs;
    private final BuildingSerializationHandler serializer;
    private final ChecksumHandler checksumHandler;

    private static final String SHARED_PREFS = "geoloc-indoor-shared-prefs";
    private static final String DEFAULT_STRING_VALUE = "DEFAULT";

    private OfflineManager(@NonNull Context context){
        this.prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        this.serializer = new BuildingSerializationHandler();
        this.checksumHandler = new ChecksumHandler();
    }

    public static OfflineManager create(Context context){
        return new OfflineManager(context);
    }

    private String buildingKey(long buildingId){
        return "BUILDING-" + buildingId;
    }

    private String eventsKey(long buildingId){
        return "EVENTS-" + buildingId;
    }

    boolean hasCachedBuilding(long buildingId){
        return this.prefs.contains(this.buildingKey(buildingId));
    }

    boolean hasCachedEvents(long buildingId){
        return this.prefs.contains(this.eventsKey(buildingId));
    }

    long computeBuildingChecksum(long buildingId){

        if (!this.hasCachedBuilding(buildingId)){
            throw new IllegalStateException("Building #" + buildingId + " is not stored in cache");
        }

        String json = this.prefs.getString(this.buildingKey(buildingId), DEFAULT_STRING_VALUE);
        Building cached = this.serializer.deserializeBuilding(json);
        return this.checksumHandler.computeBuildingChecksum(cached);
    }

    public Building getCachedBuilding(long buildingId){

        if (!this.hasCachedBuilding(buildingId)){
            throw new IllegalStateException("Building #" + buildingId + " is not stored in cache");
        }

        String json = this.prefs.getString(this.buildingKey(buildingId), DEFAULT_STRING_VALUE);
        return this.serializer.deserializeBuilding(json);
    }

    List<Event> getCachedEvents(long buildingId){
        if (!this.hasCachedEvents(buildingId)){
            throw new IllegalStateException("Events for building #" + buildingId + " are not stored in cache");
        }

        String json = this.prefs.getString(this.eventsKey(buildingId), DEFAULT_STRING_VALUE);
        return this.serializer.deserializeEvents(json);
    }

    void saveBuildingToCache(@NonNull Building building){
        String json = this.serializer.serializeBuilding(building);
        this.prefs.edit()
                .putString(this.buildingKey(building.getId()), json)
                .apply();
    }

    void saveEventsToCache(long buildingId, @NonNull List<Event> events){
        String json = this.serializer.serializeEvents(events);
        this.prefs.edit()
                .putString(this.eventsKey(buildingId), json)
                .apply();
    }

}
