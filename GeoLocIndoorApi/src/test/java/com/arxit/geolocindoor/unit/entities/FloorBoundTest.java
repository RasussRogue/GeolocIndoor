package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.FloorBound;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FloorBoundTest {
    FloorBound floorBound = new FloorBound();

    @Test
    public void shouldGetAndSetCorrectId() {
        floorBound.setId(2);
        assertEquals(floorBound.getId(), 2);
    }

    @Test
    public void shouldGetAndSetCorrectWkt() {
        floorBound.setWkt("Wkt");
        assertEquals(floorBound.getWkt(), "Wkt");
    }

    @Test
    public void shouldGetAndSetCorrectFloor() {
        floorBound.setFloor(new Floor("André"));
        assertEquals(floorBound.getFloor().getName(), "André");
    }

    @Test
    public void shouldGetAndSetCorrectGeom() throws ParseException {
        floorBound.setGeom((MultiPolygon) (new WKTReader().read("MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)),((6 3,9 2,9 4,6 3)))")));
        assertEquals(floorBound.getGeom(), (new WKTReader().read("MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)),((6 3,9 2,9 4,6 3)))")));
    }

    @Test
    public void shouldGetGeometry() throws ParseException {

        FloorBound floorBound2 = new FloorBound();
        floorBound2.setId(1);
        floorBound2.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0))");

        assertThrows(IllegalStateException.class, () -> floorBound2.getGeometry());

        floorBound2.setWkt("MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)),((6 3,9 2,9 4,6 3)))");

        MultiPolygon mp = (MultiPolygon) new WKTReader().read("MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)),((6 3,9 2,9 4,6 3)))");

        assertEquals(mp, floorBound2.getGeometry());
    }

    @Test
    public void shouldEquals(){
        floorBound.setId(1); floorBound.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0,0 0))");
        FloorBound floorBound2 = new FloorBound();
        assertFalse(floorBound.equals(floorBound2));
        floorBound2.setId(1); floorBound2.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0,0 0))");
        assertTrue(floorBound.equals(floorBound2));
    }

    @Test
    public void shouldHashCode(){
        floorBound.setId(1); floorBound.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0,0 0))");
        FloorBound floorBound2 = new FloorBound();
        assertNotEquals(floorBound.hashCode(), floorBound2.hashCode());
        floorBound2.setId(1); floorBound2.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0,0 0))");
        assertEquals(floorBound.hashCode(), floorBound2.hashCode());
    }

}
