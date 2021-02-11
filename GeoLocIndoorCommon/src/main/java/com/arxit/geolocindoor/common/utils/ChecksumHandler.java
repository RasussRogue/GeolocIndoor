package com.arxit.geolocindoor.common.utils;

import com.arxit.geolocindoor.common.entities.Building;

public class ChecksumHandler {

    public long computeBuildingChecksum(Building building){
        return building.hashCode();
    }
}
