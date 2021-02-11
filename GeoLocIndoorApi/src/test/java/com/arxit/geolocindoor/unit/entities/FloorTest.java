package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FloorTest {
    Floor floor = new Floor();

    @Test
    public void shouldGetAndSetCorrectId() {
        floor.setId(2);
        assertEquals(floor.getId(), 2);
    }

    @Test
    public void shouldGetAndSetCorrectName() {
        floor.setName("Name");
        assertEquals(floor.getName(), "Name");
    }

    @Test
    public void shouldGetAndSetCorrectBuilding() {
        Building building = new Building();
        building.setName("Name");
        floor.setBuilding(building);
        assertEquals(floor.getBuilding().getName(), "Name");
    }

    @Test
    public void shouldGetAndSetCorrectLevel() {
        floor.setLevel(1);
        assertEquals(floor.getLevel(), 1);
    }

    @Test
    public void shouldGetAndSetCorrectPOI(){
        floor.setPois(new ArrayList<>());
        assertEquals(floor.getPois(), new ArrayList<POI>());
    }

    @Test
    public void shouldGetAndSetCorrectBeacons(){
        floor.setBeacons(new ArrayList<>());
        assertEquals(floor.getBeacons(), new ArrayList<Beacon>());
    }

    @Test
    public void shouldGetAndSetCorrectBounds(){
        floor.setBounds(new ArrayList<>());
        assertEquals(floor.getBounds(), new ArrayList<FloorBound>());
    }

    @Test
    public void shouldGetAndSetCorrectWalls(){
        floor.setWalls(new ArrayList<>());
        assertEquals(floor.getWalls(), new ArrayList<FloorWalls>());
    }

    @Test
    public void shouldEquals(){
        floor.setId(1); floor.setLevel(2); floor.setName("floor");
        Floor floor2 = new Floor();
        assertNotEquals(floor, floor2);
        floor2.setId(1); floor2.setLevel(2); floor2.setName("floor");
        assertEquals(floor, floor2);
    }

    @Test
    public void shouldHashCode(){
        floor.setId(1); floor.setLevel(2); floor.setName("floor");
        Floor floor2 = new Floor();
        assertNotEquals(floor.hashCode(), floor2.hashCode());
        floor2.setId(1); floor2.setLevel(2); floor2.setName("floor");
        assertEquals(floor.hashCode(), floor2.hashCode());
    }

}
