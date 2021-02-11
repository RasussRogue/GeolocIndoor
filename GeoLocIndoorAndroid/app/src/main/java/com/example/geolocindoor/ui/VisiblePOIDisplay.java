package com.example.geolocindoor.ui;

import com.arxit.geolocindoor.common.entities.VisiblePOI;

public class VisiblePOIDisplay {

    private final VisiblePOI poi;
    private long distance;

    public VisiblePOIDisplay(VisiblePOI poi) {
        this.poi = poi;
        this.distance = distance;
    }

    public VisiblePOI getPoi() {
        return poi;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }
}
