package com.arxit.geolocindoor.service;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.stream.Location;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private final Event zero = new Event(0, "Zero", "Le zero",
            ZonedDateTime.of(LocalDate.parse("2010-11-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
            ZonedDateTime.of(LocalDate.parse("2010-12-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), null);

    private final Event un = new Event(1, "Un", "Le un",
            ZonedDateTime.of(LocalDate.parse("2020-11-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
            ZonedDateTime.of(LocalDate.parse("2020-12-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), null);

    @Test
    public void whenGetEventById_thenReturnEvent(){

        when(eventRepository.findById(101))
                .thenReturn(zero);

        assertEquals(zero, eventService.getEventById(101));
    }

    @Test
    public void whenGetEventsByBuildingId_thenReturnListEvents(){
        List<Event> events = List.of(zero, un);

        when(eventRepository.findByBuilding(202))
                .thenReturn(events);

        assertEquals(events, eventService.getEventsFromBuilding(202));
    }

    @Test
    public void whenFindByDateInterval_thenReturnListEvents(){
        List<Event> events = List.of(zero, un);

        when(eventRepository.findByDateInterval(101, ZonedDateTime.parse("2010-11-19T17:52:49+01:00"), ZonedDateTime.parse("2010-12-30T10:02:49+01:00")))
                .thenReturn(events);

        assertEquals(events, eventService.findByDateInterval(101, ZonedDateTime.parse("2010-11-19T17:52:49+01:00"), ZonedDateTime.parse("2010-12-30T10:02:49+01:00")));
    }

    /*@Test
    public void whenPostEvent_thenCreateEvent(){
        zero.setLocation(new VisiblePOI());
        zero.setId(0);
        when(eventRepository.save(Mockito.any(Event.class)))
                .thenReturn(zero);

        eventService.postEvent(zero);

        verify(eventRepository, times(1)).save(Mockito.any(Event.class));
    }*/

    @Test
    public void whenDeleteEventById_thenRemoveEvent(){

        eventService.deleteEvent(101);

        verify(eventRepository, times(1)).deleteById(101);
    }

}
