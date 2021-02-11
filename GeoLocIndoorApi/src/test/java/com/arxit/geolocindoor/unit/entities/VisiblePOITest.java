package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class VisiblePOITest {
    VisiblePOI visiblePOI = new VisiblePOI();

    @Test
    public void shouldGetAndSetCorrectId() {
        visiblePOI.setId(2);
        assertEquals(visiblePOI.getId(), 2);
    }

    @Test
    public void shouldGetAndSetCorrectTitle() {
        visiblePOI.setTitle("Title");
        assertEquals(visiblePOI.getTitle(), "Title");
    }

    @Test
    public void shouldGetAndSetCorrectType() {
        visiblePOI.setType("Type");
        assertEquals(visiblePOI.getType(), "Type");
    }

    @Test
    public void shouldGetAndSetCorrectWkt() {
        visiblePOI.setWkt("Wkt");
        assertEquals(visiblePOI.getWkt(), "Wkt");
    }

    @Test
    public void shouldGetAndSetCorrectGeom() throws ParseException {
        visiblePOI.setGeom((Point)(new WKTReader().read("POINT(2.2 4.4)")));
        assertEquals(visiblePOI.getGeom(), (Point)(new WKTReader().read("POINT(2.2 4.4)")));
    }

    @Test
    public void shouldGetAndSetCorrectFloor() {
        Floor floor = new Floor();
        floor.setName("Floor");
        visiblePOI.setFloor(floor);
        assertEquals(visiblePOI.getFloor().getName(), "Floor");
    }

    @Test
    public void shouldEquals(){
        visiblePOI.setId(1); visiblePOI.setTitle("title"); visiblePOI.setType("type");
        VisiblePOI vpoi2 = new VisiblePOI();
        assertFalse(visiblePOI.equals(vpoi2));
        vpoi2.setId(1); vpoi2.setTitle("title"); vpoi2.setType("type");
        assertTrue(visiblePOI.equals(vpoi2));
    }

    @Test
    public void shouldHashCode(){
        visiblePOI.setId(1); visiblePOI.setTitle("title"); visiblePOI.setType("type");
        VisiblePOI vpoi2 = new VisiblePOI();
        assertNotEquals(visiblePOI.hashCode(), vpoi2.hashCode());
        vpoi2.setId(1); vpoi2.setTitle("title"); vpoi2.setType("type");
        assertEquals(visiblePOI.hashCode(), vpoi2.hashCode());
    }

    @Test
    public void shouldMatches(){
        visiblePOI.setTitle("title"); visiblePOI.setType("type");
        assertTrue(visiblePOI.matches("t"));
    }

    @Test
    public void shouldGetAndSetCorrectEvents(){
        List<Event> events = List.of(new Event(), new Event());
        visiblePOI.setEvents(events);
        assertEquals(events, visiblePOI.getEvents());
    }

    @Test
    public void shouldAddEventOnEvents(){
        List<Event> events = List.of();
        Event e1 = new Event();

        assertEquals(0, events.size());
        visiblePOI.addEvent(e1);

        assertEquals(1, visiblePOI.getEvents().size());
        System.out.println(visiblePOI.getEvents().get(0));
        assertEquals(e1, visiblePOI.getEvents().get(0));
    }


}
