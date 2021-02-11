package com.example.geolocindoor.managers;

import com.arxit.geolocindoor.common.entities.Beacon;
import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.GraphEdge;
import com.arxit.geolocindoor.common.entities.POI;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class FeatureCollectionManagerTest {

    @Mock
    private final FeatureCollectionManager featureCollectionManager = FeatureCollectionManager.create();

    @Test
    public void whenBeacons_thenReturnFeatureCollection(){

        //Floor
        Floor rdc = new Floor("rdc");rdc.setLevel(0);
        Floor et1 = new Floor("etage 1");et1.setLevel(1);
        Floor et2 = new Floor("etage 2");et2.setLevel(2);
        Floor et3 = new Floor("etage 3");et3.setLevel(3);
        List<Floor> floors = Arrays.asList(rdc, et1, et2, et3);

        //GraphEdges
        GraphEdge ge1 = new GraphEdge();ge1.setId(1);ge1.setValue(1.0);ge1.setStart(new POI());ge1.setEnd(new POI());
        GraphEdge ge2 = new GraphEdge();ge2.setId(2);ge2.setValue(2.0);ge2.setStart(new POI());ge2.setEnd(new POI());
        List<GraphEdge> graphEdges = Arrays.asList(ge1, ge2);

        //Beacons
        Beacon b1_rdc = new Beacon();b1_rdc.setId(10);b1_rdc.setUuid("uuid10");b1_rdc.setFloor(rdc);b1_rdc.setWkt("POINT(1.0 1.0)");
        Beacon b1_et1 = new Beacon();b1_et1.setId(11);b1_et1.setUuid("uuid11");b1_et1.setFloor(et1);b1_et1.setWkt("POINT(1.1 1.1)");
        Beacon b2_et1 = new Beacon();b2_et1.setId(21);b2_et1.setUuid("uuid21");b2_et1.setFloor(et1);b2_et1.setWkt("POINT(2.1 2.1)");
        Beacon b1_et2 = new Beacon();b1_et2.setId(12);b1_et2.setUuid("uuid12");b1_et2.setFloor(et2);b1_et2.setWkt("POINT(1.2 1.2)");
        List<Beacon> beaconsRdc = Arrays.asList(b1_rdc);
        List<Beacon> beaconsEt1 = Arrays.asList(b1_et1, b2_et1);
        List<Beacon> beaconsEt2 = Arrays.asList(b1_et2);

        //Building
        Building building = new Building(0, "Batiment Copernic", floors, graphEdges);

        //On ajoute les beacons a des Floor
        rdc.setBeacons(beaconsRdc);
        et1.setBeacons(beaconsEt1);
        et2.setBeacons(beaconsEt2);

        List<Feature> featuresRdc = new ArrayList<>();
        rdc.getBeacons().forEach(beacon -> featuresRdc.add(Feature.fromGeometry(Point.fromLngLat(beacon.getLongitude(), beacon.getLatitude()))));

        List<Feature> featuresEt1 = new ArrayList<>();
        et1.getBeacons().forEach(beacon -> featuresEt1.add(Feature.fromGeometry(Point.fromLngLat(beacon.getLongitude(), beacon.getLatitude()))));

        List<Feature> featuresEt2 = new ArrayList<>();
        et2.getBeacons().forEach(beacon -> featuresEt2.add(Feature.fromGeometry(Point.fromLngLat(beacon.getLongitude(), beacon.getLatitude()))));

        List<Feature> featuresEt3 = new ArrayList<>();
        et3.getBeacons().forEach(beacon -> featuresEt3.add(Feature.fromGeometry(Point.fromLngLat(beacon.getLongitude(), beacon.getLatitude()))));

        assertEquals(FeatureCollection.fromFeatures(featuresRdc), featureCollectionManager.beacons(building, 0));
        assertEquals(FeatureCollection.fromFeatures(featuresEt1), featureCollectionManager.beacons(building, 1));
        assertEquals(FeatureCollection.fromFeatures(featuresEt2), featureCollectionManager.beacons(building, 2));
        assertEquals(FeatureCollection.fromFeatures(featuresEt3), featureCollectionManager.beacons(building, 3));
        assertThrows(IllegalArgumentException.class, () -> featureCollectionManager.beacons(building, 4));

    }

    @Test
    public void whenVisiblePois_thenReturnFeatureCollection(){

        //Floor
        Floor rdc = new Floor("rdc");rdc.setLevel(0);
        Floor et1 = new Floor("etage 1");et1.setLevel(1);
        Floor et2 = new Floor("etage 2");et2.setLevel(2);
        Floor et3 = new Floor("etage 3");et3.setLevel(3);
        List<Floor> floors = Arrays.asList(rdc, et1, et2, et3);

        //VisiblePOI
        VisiblePOI vpoi1Rdc = new VisiblePOI();vpoi1Rdc.setTitle("POI 1 RDC");vpoi1Rdc.setId(10);
        POI poi1Rdc = vpoi1Rdc ;poi1Rdc.setFloor(rdc);poi1Rdc.setWkt("POINT(1.0 1.0)");
        List<POI> poisRdc = Arrays.asList(poi1Rdc);

        VisiblePOI vpoi1Et1 = new VisiblePOI();vpoi1Et1.setTitle("POI 1 Et1");vpoi1Et1.setId(11);
        POI poi1Et1 = vpoi1Et1;poi1Et1.setFloor(rdc);poi1Et1.setWkt("POINT(1.1 1.1)");

        VisiblePOI vpoi2Et1 = new VisiblePOI();vpoi2Et1.setTitle("POI 2 Et1");vpoi2Et1.setId(21);
        POI poi2Et1 = vpoi2Et1;poi2Et1.setFloor(rdc);poi2Et1.setWkt("POINT(2.1 2.1)");
        List<POI> poisEt1 = Arrays.asList(poi1Et1, poi2Et1);

        //ON ajout les POI aux Floor
        rdc.setPois(poisRdc);
        et1.setPois(poisEt1);

        Building building = new Building(0, "Batiment Copernic", floors, new ArrayList<GraphEdge>());

        JsonObject props = new JsonObject();
        props.addProperty("title", "POI 1 RDC");
        props.addProperty("id", 10);
        List<Feature> featuresRdc = Arrays.asList(
                Feature.fromGeometry(Point.fromLngLat(vpoi1Rdc.getLongitude(), vpoi1Rdc.getLatitude()), props)
        );

        JsonObject props1 = new JsonObject();
        props1.addProperty("title", "POI 1 Et1");
        props1.addProperty("id", 11);
        JsonObject props2 = new JsonObject();
        props2.addProperty("title", "POI 2 Et1");
        props2.addProperty("id", 21);
        List<Feature> featuresEt1 = Arrays.asList(
                Feature.fromGeometry(Point.fromLngLat(vpoi1Et1.getLongitude(), vpoi1Et1.getLatitude()), props1),
                Feature.fromGeometry(Point.fromLngLat(vpoi2Et1.getLongitude(), vpoi2Et1.getLatitude()), props2)
        );

        assertEquals(FeatureCollection.fromFeatures(featuresRdc), featureCollectionManager.visiblePois(building, 0, true));
        assertEquals(FeatureCollection.fromFeatures(featuresEt1), featureCollectionManager.visiblePois(building, 1, true));
        assertThrows(IllegalArgumentException.class, () -> featureCollectionManager.beacons(building, 4));

    }

    @Test
    public void whenVisiblePoisWithEvents_thenReturnFeatureCollection(){

        //Floor
        Floor rdc = new Floor("rdc");rdc.setLevel(0);
        Floor et1 = new Floor("etage 1");et1.setLevel(1);
        Floor et2 = new Floor("etage 2");et2.setLevel(2);
        Floor et3 = new Floor("etage 3");et3.setLevel(3);
        List<Floor> floors = Arrays.asList(rdc, et1, et2, et3);

        //Events
        Event ev1_rdc = new Event();ev1_rdc.setId(0);ev1_rdc.setDescription("Event 1 au RDC");
        Event ev1_et1 = new Event();ev1_et1.setId(1);ev1_et1.setDescription("Event 1 a l'Etage 1");
        Event ev2_et1 = new Event();ev2_et1.setId(2);ev2_et1.setDescription("Event 2 a l'Etage 1");
        Event ev1_et2 = new Event();ev1_et2.setId(3);ev1_et2.setDescription("Event 1 a l'Etage 2");
        Event ev2_et2 = new Event();ev2_et2.setId(4);ev2_et2.setDescription("Event 2 a l'Etage 2");
        List<Event> eventsRdc = Arrays.asList(ev1_rdc);
        List<Event> eventsPoi1Et1 = Arrays.asList(ev1_et1);
        List<Event> eventsPoi2Et1 = Arrays.asList(ev2_et1);
        List<Event> eventsEt2 = Arrays.asList(ev1_et2, ev2_et2);

        //VisiblePOI
        VisiblePOI vpoi1Rdc = new VisiblePOI();vpoi1Rdc.setTitle("POI 1 RDC");vpoi1Rdc.setId(10);vpoi1Rdc.setEvents(eventsRdc);
        POI poi1Rdc = vpoi1Rdc ;poi1Rdc.setFloor(rdc);poi1Rdc.setWkt("POINT(1.0 1.0)");
        List<POI> poisRdc = Arrays.asList(poi1Rdc);

        VisiblePOI vpoi1Et1 = new VisiblePOI();vpoi1Et1.setTitle("POI 1 Et1");vpoi1Et1.setId(11);vpoi1Et1.setEvents(eventsPoi1Et1);
        POI poi1Et1 = vpoi1Et1;poi1Et1.setFloor(rdc);poi1Et1.setWkt("POINT(1.1 1.1)");

        VisiblePOI vpoi2Et1 = new VisiblePOI();vpoi2Et1.setTitle("POI 2 Et1");vpoi2Et1.setId(21);vpoi2Et1.setEvents(eventsPoi2Et1);
        POI poi2Et1 = vpoi2Et1;poi2Et1.setFloor(rdc);poi2Et1.setWkt("POINT(2.1 2.1)");
        List<POI> poisEt1 = Arrays.asList(poi1Et1, poi2Et1);

        VisiblePOI vpoi1Et2 = new VisiblePOI();vpoi1Et2.setTitle("POI 1 Et2");vpoi1Et2.setId(12);vpoi1Et2.setEvents(eventsEt2);
        POI poi1Et2 = vpoi1Et2;poi1Et2.setFloor(rdc);poi1Et2.setWkt("POINT(1.2 1.2)");

        VisiblePOI vpoi2Et2 = new VisiblePOI();vpoi2Et2.setTitle("POI 2 Et2");vpoi2Et2.setId(22);
        POI poi2Et2 = vpoi2Et2;poi2Et2.setFloor(rdc);poi2Et2.setWkt("POINT(2.2 2.2)");
        List<POI> poisEt2 = Arrays.asList(poi1Et2, poi2Et2);

        rdc.setPois(poisRdc);
        et1.setPois(poisEt1);
        et2.setPois(poisEt2);

        Building building = new Building(0, "Batiment Copernic", floors, new ArrayList<GraphEdge>());

        JsonObject props = new JsonObject();
        props.addProperty("title", "POI 1 RDC");
        props.addProperty("id", 10);
        List<Feature> featuresRdc = Arrays.asList(
                Feature.fromGeometry(Point.fromLngLat(vpoi1Rdc.getLongitude(), vpoi1Rdc.getLatitude()), props)
        );

        JsonObject props1 = new JsonObject();
        props1.addProperty("title", "POI 1 Et1");
        props1.addProperty("id", 11);
        JsonObject props2 = new JsonObject();
        props2.addProperty("title", "POI 2 Et1");
        props2.addProperty("id", 21);
        List<Feature> featuresEt1 = Arrays.asList(
                Feature.fromGeometry(Point.fromLngLat(vpoi1Et1.getLongitude(), vpoi1Et1.getLatitude()), props1),
                Feature.fromGeometry(Point.fromLngLat(vpoi2Et1.getLongitude(), vpoi2Et1.getLatitude()), props2)
        );

        JsonObject props3 = new JsonObject();
        props3.addProperty("title", "POI 1 Et2");
        props3.addProperty("id", 12);
        List<Feature> featuresEt2 = Arrays.asList(
                Feature.fromGeometry(Point.fromLngLat(vpoi1Et2.getLongitude(), vpoi1Et2.getLatitude()), props3)
        );

        assertEquals(FeatureCollection.fromFeatures(featuresRdc), featureCollectionManager.visiblePoisWithEvents(building, 0));
        assertEquals(FeatureCollection.fromFeatures(featuresEt1), featureCollectionManager.visiblePoisWithEvents(building, 1));
        assertEquals(FeatureCollection.fromFeatures(featuresEt2), featureCollectionManager.visiblePoisWithEvents(building, 2));
        assertThrows(IllegalArgumentException.class, () -> featureCollectionManager.beacons(building, 4));
    }

    @Test
    public void whenBounds_thenReturnFeatureCollection(){

        //POLYGON((0 0,0 1,1 1,1 0,0 0))

        //TODO
        //walls()
        //itinerary
    }
}
