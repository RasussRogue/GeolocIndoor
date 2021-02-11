package com.example.geolocindoor.managers;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Building;

public class BuildingSimple {

    private final long id;
    private final String name;
    private final int floorCount;

    private BuildingSimple(long id, String name, int floorCount) {
        this.id = id;
        this.name = name;
        this.floorCount = floorCount;
    }

    static BuildingSimple fromBuilding(@NonNull Building building){
        return new BuildingSimple(building.getId(), building.getName(), building.getFloors().size());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFloorCount() {
        return floorCount;
    }
}
