package com.example.geolocindoor.managers;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Building;
import com.example.geolocindoor.utils.PreferenceUtils;

import java.util.List;

import timber.log.Timber;

public final class BuildingManager {

    private final Context context;
    private final ApiClient apiClient;
    private final OfflineManager offlineManager;

    private BuildingManager(@NonNull Context context){
        this.context = context;
        this.apiClient = ApiClient.create(context);
        this.offlineManager = OfflineManager.create(context);
    }

    @SuppressLint("StaticFieldLeak")
    private static BuildingManager instance;

    public static BuildingManager create(Context context){
        if (instance == null){
            instance = new BuildingManager(context.getApplicationContext());
        }
        return instance;
    }

    List<BuildingSimple> getBuildings(){
        return this.apiClient.getBuildings();
    }

    Building getBuilding(){

        long buildingId = PreferenceUtils.getSelectedBuildingId(this.context);
        Building result;
        //Check if building is in cache and up-to-date
        if (this.offlineManager.hasCachedBuilding(buildingId)
            && this.offlineManager.computeBuildingChecksum(buildingId) == this.apiClient.getBuildingChecksum(buildingId)){

            Timber.i("Retrieving building " + buildingId + " from cache ...");
            result = this.offlineManager.getCachedBuilding(buildingId);
        } else {
            //Here, the building has to be (re)downloaded from API and (re)stored in cache
            Timber.i("Downloading building " + buildingId + " from API");
            result = this.apiClient.getBuilding(buildingId);
            this.offlineManager.saveBuildingToCache(result);
        }
        return result;
    }

    public OfflineManager offlineManager(){
        return this.offlineManager;
    }

}
