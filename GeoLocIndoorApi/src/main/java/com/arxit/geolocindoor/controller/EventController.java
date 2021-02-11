package com.arxit.geolocindoor.controller;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.service.EventService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "${server.api.path}")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping(value = "/event/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Event getEventById(@PathVariable("id") long id) {
        return eventService.getEventById(id);
    }

//    @ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer {jwt_token}")
    @PostMapping(value = "/event/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public Event addEvent(@RequestBody Event event) {
       return eventService.postEvent(event);
    }

//    @ApiImplicitParam(name = "Authorization", value = "Bearer ", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer {jwt_token}")
    @DeleteMapping(value = "/event/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteEvent(@PathVariable("id") long id) {
        eventService.deleteEvent(id);
    }

    @GetMapping(value = "/events/{buildingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getEventByBuilding(@PathVariable("buildingId") long id) {
        return eventService.getEventsFromBuilding(id);
    }

    @GetMapping(value = "/events/{buildingId}/interval", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getEventByInterval(@PathVariable("buildingId") long id,
                                          @RequestParam("minStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime minStart,
                                          @RequestParam("maxEnd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime maxEnd) {
        return eventService.findByDateInterval(id, minStart, maxEnd);
    }
}
