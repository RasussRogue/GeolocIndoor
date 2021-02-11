package com.arxit.geolocindoor.unit.entities;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EventTest {
    private Event event = new Event();

    private Event zero = new Event(0, "Zero", "Le zero",
            ZonedDateTime.of(LocalDate.parse("2010-11-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
            ZonedDateTime.of(LocalDate.parse("2010-12-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), new VisiblePOI("zero", "zero"));

    @Test
    public void shouldGetAndSetCorrectId() {
        event.setId(2);
        assertEquals(event.getId(), 2);
    }

    @Test
    public void shouldGetAndSetCorrectLocation() {
        VisiblePOI poi = new VisiblePOI();
        poi.setTitle("Olé");

        event.setLocation(poi);
        assertEquals(event.getLocation().getTitle(), "Olé");
    }

    @Test
    public void shouldGetAndSetCorrectTitle() {
        event.setTitle("Title");
        assertEquals(event.getTitle(), "Title");
    }

    @Test
    public void shouldGetAndSetCorrectDesc() {
        event.setDescription("Desc");
        assertEquals(event.getDescription(), "Desc");
    }

    @Test
    public void shouldGetAndSetCorrectStart() {
        ZonedDateTime now = ZonedDateTime.now();
        event.setStart(now);
        assertEquals(event.getStart(), now);
    }

    @Test
    public void shouldGetAndSetCorrectEnd() {
        ZonedDateTime now = ZonedDateTime.now();
        event.setEnd(now);
        assertEquals(event.getEnd(), now);
    }

    @Test
    public void shouldEquals(){

        assertFalse(event.equals(zero));
        Event un = new Event(0, "Zero", "Le zero",
                ZonedDateTime.of(LocalDate.parse("2010-11-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
                ZonedDateTime.of(LocalDate.parse("2010-12-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), new VisiblePOI("zero", "zero"));
        assertTrue(un.equals(zero));
    }

    @Test
    public void shouldHashCode(){
        Event un = new Event(0, "Zero", "Le zero",
                ZonedDateTime.of(LocalDate.parse("2010-11-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
                ZonedDateTime.of(LocalDate.parse("2010-12-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), new VisiblePOI("zero", "zero"));
        assertNotEquals(event.hashCode(), zero.hashCode());
        assertEquals(un.hashCode(), zero.hashCode());
    }

    @Test
    public void shouldMatches(){
        assertTrue(zero.matches("z"));
    }
}
