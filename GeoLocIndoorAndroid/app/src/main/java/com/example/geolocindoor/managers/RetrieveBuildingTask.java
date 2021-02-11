package com.example.geolocindoor.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Building;
import com.example.geolocindoor.utils.PreferenceUtils;

import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.function.Consumer;

public class RetrieveBuildingTask extends AsyncTask<Void, Void, Optional<Building>> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final Handler handler;
    private final BuildingManager buildingManager;
    private final EventManager eventManager;
    private final Consumer<Building> onPostExecute;
    private final Consumer<Building> onNoNetworkButBuildingInCache;
    private final Runnable onNetworkError;

    public RetrieveBuildingTask(@NonNull Context context, @NonNull Consumer<Building> onPostExecute,
                                @NonNull Consumer<Building> onNoNetworkButBuildingInCache, @NonNull Runnable onNetworkError){
        this.handler = new Handler();
        this.context = context;
        this.buildingManager = BuildingManager.create(context);
        this.eventManager = EventManager.create(context);
        this.onPostExecute = onPostExecute;
        this.onNoNetworkButBuildingInCache = onNoNetworkButBuildingInCache;
        this.onNetworkError = onNetworkError;
    }

    @Override
    protected Optional<Building> doInBackground(Void... voids) {

        long buildingId = PreferenceUtils.getSelectedBuildingId(this.context);
        try {
            Building retrieved = this.buildingManager.getBuilding();
            this.eventManager.bindEventsToPois(retrieved, this.eventManager.getEvents(retrieved));
            return Optional.of(retrieved);
        } catch (UncheckedIOException e){

            OfflineManager offlineManager = this.buildingManager.offlineManager();
            if (offlineManager.hasCachedBuilding(buildingId)){
                Building cached = offlineManager.getCachedBuilding(buildingId);
                this.eventManager.bindEventsToPois(cached, this.eventManager.getEvents(cached));
                this.handler.post(() -> this.onNoNetworkButBuildingInCache.accept(cached));
                return Optional.empty();
            }
            this.handler.post(this.onNetworkError);
            return Optional.empty();
        }
    }

    @Override
    protected void onPostExecute(Optional<Building> building) {
        building.ifPresent(this.onPostExecute);
    }
}
