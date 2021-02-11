package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.POI;
import com.arxit.geolocindoor.common.entities.Pointable;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//import org.locationtech.jts.geom.*;

@SpringBootTest
public class PointableTest {

    @Test
    public void shouldGetLongitude(){

        POI poi1 = new POI();
        poi1.setWkt("POINT(1.0 2.10)");
        Pointable p1 = poi1;
        assertEquals(1.0, p1.getLongitude());
    }

    @Test
    public void shouldGetLatitude(){

        POI poi1 = new POI();
        poi1.setWkt("POINT(1.0 2.10)");
        Pointable p1 = poi1;
        assertEquals(2.10, p1.getLatitude());
    }

    @Test
    public void shouldGetGeometry(){

        POI poi1 = new POI();
        poi1.setWkt("POINT(10)");
        final Pointable p1 = poi1;

        assertThrows(IllegalStateException.class, () -> p1.getGeometry());

        poi1.setWkt("POINT(1.0 2.10)");
        Pointable p2 = poi1;

        assertEquals(new GeometryFactory().createPoint(new Coordinate(1.0, 2.10)), p2.getGeometry());
    }

}
