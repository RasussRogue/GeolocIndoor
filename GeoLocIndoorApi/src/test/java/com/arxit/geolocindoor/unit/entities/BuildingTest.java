package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BuildingTest {
    Building building = new Building();

    @Test
    public void shouldSetAndGetCorrectId() {
        building.setId(2);
        assertEquals(building.getId(), 2);
    }

    @Test
    public void shouldSetAndGetCorrectName() {
        building.setName("Lavoisier");
        assertEquals(building.getName(), "Lavoisier");
    }

    @Test
    public void shouldSetAndGetCorrectFloor() {
        building.setFloors(List.of(new Floor("semoule")));
        assertEquals(building.getFloors().get(0).getName(), "semoule");
    }

    @Test
    public void shouldEquals() {
        building.setId(1);
        building.setName("name");
        building.setFloors(List.of(new Floor()));
        building.setEdges(List.of(new GraphEdge()));
        Building b2 = new Building();
        assertFalse(building.equals(b2));
        b2.setId(1);
        b2.setName("name");
        b2.setFloors(List.of(new Floor()));
        b2.setEdges(List.of(new GraphEdge()));
        assertTrue(building.equals(b2));
    }

    @Test
    public void shouldHashCode() {

        POI p1 = new POI();
        p1.setId(1);
        POI p2 = new POI();
        p2.setId(2);
        GraphEdge ge1 = new GraphEdge();
        ge1.setId(1);
        ge1.setValue(1.0);
        ge1.setStart(p1);
        ge1.setEnd(p2);

        building.setId(1);
        building.setName("name");
        building.setFloors(List.of(new Floor()));
        building.setEdges(List.of(ge1));
        Building b2 = new Building();
        //assertThrows(NullPointerException.class, () -> b2.hashCode());
        b2.setId(1);
        b2.setName("name");
        b2.setFloors(List.of(new Floor()));
        b2.setEdges(List.of(ge1));
        assertEquals(building.hashCode(), b2.hashCode());
    }

    @Test
    public void shouldSetAndGetBeacons() {

        Beacon b1 = new Beacon();
        b1.setId(1);
        b1.setUuid("b1");
        Floor f1 = new Floor("f1");
        f1.setBeacons(List.of(b1));

        Beacon b2 = new Beacon();
        b2.setId(2);
        b2.setUuid("b2");
        Floor f2 = new Floor("f2");
        f2.setBeacons(List.of(b2));

        building.setFloors(List.of(f1, f2));

        assertEquals(Map.of("b1", b1, "b2", b2), building.getBeacons());
    }

    @Test
    public void shouldGetFloorByLevel() {

        Floor f1 = new Floor("f1");
        f1.setLevel(1);
        Floor f2 = new Floor("f2");
        f2.setLevel(2);

        building.setFloors(List.of(f1, f2));
        assertEquals(building.getFloorByLevel(1).get(), f1);

        assertFalse(building.getFloorByLevel(0).isPresent());

        Floor f3 = new Floor("f3");
        f3.setLevel(2);
        building.setFloors(List.of(f1, f3, f2));

        assertEquals(building.getFloorByLevel(2).get(), f3);

    }

    @Test
    public void shouldGetPOIById() {

        POI p1 = new POI();
        p1.setId(1);
        POI p2 = new POI();
        p2.setId(2);
        List<POI> pois1 = List.of(p1, p2);

        POI p3 = new POI();
        p3.setId(3);
        POI p4 = new POI();
        p4.setId(4);
        List<POI> pois2 = List.of(p3, p4);

        Floor f1 = new Floor("f1");
        f1.setLevel(1);
        f1.setPois(pois1);
        Floor f2 = new Floor("f2");
        f2.setLevel(2);
        f2.setPois(pois2);

        building.setFloors(List.of(f1, f2));

        assertEquals(building.getPoiById(1).get(), p1);
        assertFalse(building.getPoiById(0).isPresent());

        POI p5 = new POI();
        p5.setId(3);
        POI p6 = new POI();
        p6.setId(4);
        List<POI> pois3 = List.of(p3, p4);

        Floor f3 = new Floor("f3");
        f3.setLevel(3);
        building.setFloors(List.of(f1, f3, f2));
        f3.setPois(pois3);

        assertEquals(building.getPoiById(3).get(), p5);

    }

    /*@Test
    public void shouldGetGraph(){

        //POI
        POI p1 = new POI(); p1.setId(1);
        POI p2 = new POI(); p2.setId(2);
        List<POI> pois1 = List.of(p1,p2);

        POI p3 = new POI(); p3.setId(3);
        POI p4 = new POI(); p4.setId(4);
        List<POI> pois2 = List.of(p3,p4);

        //GraphEdge
        GraphEdge ge1 = new GraphEdge(); ge1.setValue(1.0); ge1.setStart(p1); ge1.setEnd(p1);
        GraphEdge ge2 = new GraphEdge(); ge2.setValue(2.0); ge2.setStart(p2); ge2.setEnd(p2);

        Floor f1 = new Floor("f1"); f1.setLevel(1); f1.setPois(pois1);
        Floor f2 = new Floor("f2"); f2.setLevel(2); f2.setPois(pois2);

        building.setFloors(List.of(f1, f2));
        building.setEdges(List.of(ge1, ge2));

        GraphBuilder<POI,Double> graphBuilder = GraphBuilder.create();
        graphBuilder.connect(ge1.getStart()).to(ge1.getEnd()).withEdge(ge1.getValue());
        graphBuilder.connect(ge2.getStart()).to(ge2.getEnd()).withEdge(ge2.getValue());

        assertEquals(building.getGraph(), graphBuilder.createDirectedGraph());

    }*/

    @Test
    public void shouldGetPlacesMatching() {

        VisiblePOI p1 = new VisiblePOI();
        p1.setId(1);
        p1.setType("zoro");
        VisiblePOI p2 = new VisiblePOI();
        p2.setId(2);
        p2.setType("zoro");
        List<POI> pois1 = List.of(p1, p2);

        POI p3 = new POI();
        p3.setId(3);
        POI p4 = new POI();
        p4.setId(4);
        List<POI> pois2 = List.of(p3, p4);

        Floor f1 = new Floor("f1");
        f1.setLevel(1);
        f1.setPois(pois1);
        Floor f2 = new Floor("f2");
        f2.setLevel(2);
        f2.setPois(pois2);

        building.setFloors(List.of(f1, f2));

        assertEquals(List.of(), building.getPlacesMatching("a"));
        assertEquals(pois1, building.getPlacesMatching("z"));
    }

}
