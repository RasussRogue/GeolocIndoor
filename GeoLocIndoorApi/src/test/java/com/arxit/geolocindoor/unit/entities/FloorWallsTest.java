package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.FloorWalls;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FloorWallsTest {
    FloorWalls floorWalls = new FloorWalls();

    @Test
    public void shouldGetAndSetCorrectId() {
        floorWalls.setId(2);
        assertEquals(floorWalls.getId(), 2);
    }

    @Test
    public void shouldGetAndSetCorrectWkt() {
        floorWalls.setWkt("Wkt");
        assertEquals(floorWalls.getWkt(), "Wkt");
    }

    @Test
    public void shouldGetAndSetCorrectFloor() {
        floorWalls.setFloor(new Floor("André"));
        assertEquals(floorWalls.getFloor().getName(), "André");
    }

    @Test
    public void shouldGetAndSetCorrectGeom() throws ParseException {
        floorWalls.setGeom((MultiLineString) (new WKTReader().read("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))")));
        assertEquals(floorWalls.getGeom(), new WKTReader().read("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))"));
    }

    @Test
    public void shouldEquals(){
        floorWalls.setId(1);
        floorWalls.setWkt("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))");
        FloorWalls fw2 = new FloorWalls();
        assertFalse(floorWalls.equals(fw2));
        fw2.setId(1);
        fw2.setWkt("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))");
        assertTrue(floorWalls.equals(fw2));
    }

    @Test
    public void shouldHashCode(){
        floorWalls.setId(1);
        floorWalls.setWkt("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))");
        FloorWalls fw2 = new FloorWalls();
        assertNotEquals(floorWalls.hashCode(), fw2.hashCode());
        fw2.setId(1); fw2.setWkt("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))");
        assertEquals(floorWalls.hashCode(), fw2.hashCode());
    }

    @Test
    public void shouldGetGeometry() throws ParseException {

        floorWalls.setWkt("MULTILINESTRING(1 2)");

        assertThrows(IllegalStateException.class, () -> floorWalls.getGeometry());

        floorWalls.setWkt("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))");

        MultiLineString mls = (MultiLineString) new WKTReader().read("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))");

        assertEquals(mls, floorWalls.getGeometry());
    }

}
