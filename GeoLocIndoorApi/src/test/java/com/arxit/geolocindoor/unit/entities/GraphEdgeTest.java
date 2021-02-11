package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.GraphEdge;
import com.arxit.geolocindoor.common.entities.POI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GraphEdgeTest {
    GraphEdge graphEdge = new GraphEdge();

    @Test
    public void shouldGetAndSetCorrectId() {
        graphEdge.setId(2);
        assertEquals(graphEdge.getId(), 2);
    }

    @Test
    public void shouldGetAndSetCorrectValue() {
        graphEdge.setValue(1);
        assertEquals(graphEdge.getValue(), 1);
    }

    @Test
    public void shouldGetAndSetCorrectBuilding() {
        Building building = new Building();
        building.setName("Name");
        graphEdge.setBuilding(building);
        assertEquals(graphEdge.getBuilding().getName(), "Name");
    }

    @Test
    public void shouldGetAndSetCorrectStart() {
        POI poi = new POI();
        poi.setId(101);
        graphEdge.setStart(poi);
        assertEquals(graphEdge.getStart().getId(), 101);
    }

    @Test
    public void shouldGetAndSetCorrectEnd() {
        POI poi = new POI();
        poi.setId(101);
        graphEdge.setEnd(poi);
        assertEquals(graphEdge.getEnd().getId(), 101);
    }

    @Test
    public void shouldEquals(){

        graphEdge.setId(1); graphEdge.setValue(1.0); graphEdge.setStart(new POI()); graphEdge.setEnd(new POI());
        GraphEdge ge2 = new GraphEdge();
        assertFalse(graphEdge.equals(ge2));
        ge2.setId(1); ge2.setValue(1.0); ge2.setStart(new POI()); ge2.setEnd(new POI());
        assertTrue(graphEdge.equals(ge2));
    }

    @Test
    public void shouldHashCode(){
        POI p1 = new POI(); p1.setId(1);
        POI p2 = new POI(); p2.setId(2);
        graphEdge.setId(1); graphEdge.setValue(1.0); graphEdge.setStart(p1); graphEdge.setEnd(p2);
        GraphEdge ge2 = new GraphEdge();
        assertThrows(NullPointerException.class, () -> ge2.hashCode());
        ge2.setId(1); ge2.setValue(1.0); ge2.setStart(p1); ge2.setEnd(p2);
        assertEquals(graphEdge.hashCode(), ge2.hashCode());
    }

}
