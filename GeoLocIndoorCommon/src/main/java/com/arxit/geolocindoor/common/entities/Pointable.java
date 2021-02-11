package com.arxit.geolocindoor.common.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Point;

public interface Pointable extends Displayable {

    @JsonIgnore
    default double getLongitude(){
        return this.getGeometry().getX();
    }

    @JsonIgnore
    default double getLatitude(){
        return this.getGeometry().getY();
    }

    @JsonIgnore
    @Override
    Point getGeometry();
}
