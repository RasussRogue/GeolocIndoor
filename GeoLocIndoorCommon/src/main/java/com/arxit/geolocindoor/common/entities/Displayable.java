package com.arxit.geolocindoor.common.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Generated;
import org.locationtech.jts.geom.Geometry;

import java.io.Serializable;

@Generated
public interface Displayable extends Serializable {

    String getWkt();

    @JsonIgnore
    Geometry getGeometry();
}
