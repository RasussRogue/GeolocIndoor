package com.arxit.geolocindoor.unit;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Test
    public void shouldRefuseEventIfDatesAreIllegal() {
        //given
        Event event = new Event(20, "Event", "Description",
                ZonedDateTime.of(LocalDate.parse("2010-12-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
                ZonedDateTime.of(LocalDate.parse("2010-11-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), null);

        //when/then
        assertThrows(IllegalArgumentException.class, () -> eventService.postEvent(event));
    }


}
