package com.arxit.geolocindoor.service;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.repository.EventRepository;
import com.arxit.geolocindoor.repository.VisiblePOIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private VisiblePOIRepository poiRepo;

    public Event getEventById(long id) {
        return eventRepo.findById(id);
    }

    public List<Event> getEventsFromBuilding(long id) {
        return eventRepo.findByBuilding(id);
    }

    public List<Event> findByDateInterval(long id, ZonedDateTime minStart, ZonedDateTime maxEnd) {
        return eventRepo.findByDateInterval(id, minStart, maxEnd);
    }

    public Event postEvent(Event event) {
        if (event.getStart().isAfter(event.getEnd())) {
            System.out.println(event.getStart().toString());
            System.out.println(event.getEnd().toString());
            throw new IllegalArgumentException("Start date is after end date for event "+event.getTitle());
        }
        System.out.println(event);
        System.out.println(poiRepo);
        System.out.println(event.getLocation());
        event.setLocation(poiRepo.findById(event.getLocation().getId()));
        return eventRepo.save(event);
    }

    public void deleteEvent(long id) {
         eventRepo.deleteById(id);
    }
}
