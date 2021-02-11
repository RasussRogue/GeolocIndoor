package com.example.geolocindoor.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class RetrieveBuildingsListTask extends AsyncTask<Void, Void, Optional<? extends List<BuildingSimple>>> {

    private final Handler handler;
    private final BuildingManager buildingManager;
    private final Consumer<List<BuildingSimple>> onPostExecute;
    private final Runnable onNetworkError;

    public RetrieveBuildingsListTask(@NonNull Context context, @NonNull Consumer<List<BuildingSimple>> onPostExecute, @NonNull Runnable onNetworkError) {
        this.handler = new Handler();
        this.buildingManager = BuildingManager.create(context);
        this.onPostExecute = onPostExecute;
        this.onNetworkError = onNetworkError;
    }

    @Override
    protected Optional<? extends List<BuildingSimple>> doInBackground(Void... voids) {
        try {
            return Optional.of(this.buildingManager.getBuildings());
        } catch (UncheckedIOException e){
            this.handler.post(this.onNetworkError);
            return Optional.empty();
        }
    }

    @Override
    protected void onPostExecute(Optional<? extends List<BuildingSimple>> buildings) {
        buildings.ifPresent(this.onPostExecute);
    }
}
