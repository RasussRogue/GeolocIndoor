package com.example.geolocindoor.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Event;
import com.example.geolocindoor.utils.PreferenceUtils;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class RetrieveEventsTask extends AsyncTask<Void, Void, Optional<? extends List<Event>>> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final Handler handler;
    private final BuildingManager buildingManager;
    private final EventManager eventManager;
    private final Consumer<List<Event>> onPostExecute;
    private final Runnable onNetworkError;

    public RetrieveEventsTask(@NonNull Context context, @NonNull BuildingManager buildingManager, @NonNull EventManager eventManager,
                              @NonNull Consumer<List<Event>> onPostExecute, @NonNull Runnable onNetworkError){
        this.handler = new Handler();
        this.context = context;
        this.buildingManager = buildingManager;
        this.eventManager = eventManager;
        this.onPostExecute = onPostExecute;
        this.onNetworkError = onNetworkError;
    }

    @Override
    protected Optional<? extends List<Event>> doInBackground(Void... voids) {
        try {
            long buildingId = PreferenceUtils.getSelectedBuildingId(this.context);
            if (this.buildingManager.offlineManager().hasCachedBuilding(buildingId)){
                return Optional.of(this.eventManager.getEvents(this.buildingManager.offlineManager().getCachedBuilding(buildingId)));
            }
            return Optional.empty();
        } catch (UncheckedIOException e){
            this.handler.post(this.onNetworkError);
            return Optional.empty();
        }
    }

    @Override
    protected void onPostExecute(Optional<? extends List<Event>> events) {
        events.ifPresent(this.onPostExecute);
    }
}
