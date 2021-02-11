package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.Beacon;
import com.arxit.geolocindoor.common.entities.Floor;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BeaconTest {
    Beacon beacon = new Beacon();

    @Test
    public void shouldGetAndSetCorrectId() {
        beacon.setId(2);
        assertEquals(beacon.getId(), 2);
    }

    @Test
    public void shouldGetAndSetCorrectFloor() {
        beacon.setFloor(new Floor("André"));
        assertEquals(beacon.getFloor().getName(), "André");
    }

    @Test
    public void shouldGetAndSetCorrectName() throws ParseException {
        beacon.setGeom((Point)(new WKTReader().read("POINT(2.2 4.4)")));
        assertEquals(beacon.getGeom(), new WKTReader().read("POINT(2.2 4.4)"));
    }

    @Test
    public void shouldGetAndSetCorrectUuid() {
        beacon.setUuid("Uuid");
        assertEquals(beacon.getUuid(), "Uuid");
    }

    @Test
    public void shouldGetAndSetCorrectWkt() {
        beacon.setWkt("Wkt");
        assertEquals(beacon.getWkt(), "Wkt");
    }

    @Test
    public void shouldGetAndSetCorrectHeight() {
        beacon.setHeight(1.0);
        assertEquals(beacon.getHeight(), 1.0);
    }

    @Test
    public void shouldEquals(){
        beacon.setId(1); beacon.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0,0 0))"); beacon.setUuid("uuid"); beacon.setHeight(2.0);
        Beacon b2 = new Beacon();
        assertFalse(beacon.equals(b2));
        b2.setId(1); b2.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0,0 0))"); b2.setUuid("uuid"); b2.setHeight(2.0);
        assertTrue(beacon.equals(b2));
    }

    @Test
    public void shouldHashCode(){
        beacon.setId(1); beacon.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0,0 0))"); beacon.setUuid("uuid"); beacon.setHeight(2.0);
        Beacon b2 = new Beacon();
        assertNotEquals(beacon.hashCode(), b2.hashCode());
        b2.setId(1); b2.setWkt("MULTIPOLYGON((0 0,0 1,1 1,1 0,0 0))"); b2.setUuid("uuid"); b2.setHeight(2.0);
        assertEquals(beacon.hashCode(), b2.hashCode());
    }

}
