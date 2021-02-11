package com.arxit.geolocindoor.controller;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.service.EventService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private EventService eventService;


    private final Event zero = new Event(0, "Zero", "Le zero",
            ZonedDateTime.of(LocalDate.parse("2010-11-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
            ZonedDateTime.of(LocalDate.parse("2010-12-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), null);

    private final Event un = new Event(1, "Un", "Le un",
            ZonedDateTime.of(LocalDate.parse("2020-11-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
            ZonedDateTime.of(LocalDate.parse("2020-12-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), null);

    private final Event deux = new Event(1, "Deux", "Le deux",
            ZonedDateTime.of(LocalDate.parse("2020-11-18"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
            ZonedDateTime.of(LocalDate.parse("2020-12-30"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), null);

    private final Event trois = new Event(1, "Trois", "Le trois",
            ZonedDateTime.of(LocalDate.parse("2020-11-19"), LocalTime.parse("17:52:49"), ZoneId.of("UTC")),
            ZonedDateTime.of(LocalDate.parse("2020-12-31"), LocalTime.parse("10:02:49"), ZoneId.of("UTC")), null);

    private final List<Event> listEvents = List.of(zero, un, deux, trois);

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void whenHttpGetEventById_thenReturnJson() throws Exception {
        when(eventService.getEventById(0))
                .thenReturn(zero);

        MvcResult result = mvc.perform(get("/api/event/{id}", 0))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(om.readValue(result.getResponse().getContentAsString(), Event.class), zero);
    }

    @Test
    public void whenHttpGetEventByBuildingId_thenReturnJson() throws Exception{
        when(eventService.getEventsFromBuilding(101))
                .thenReturn(listEvents);

        MvcResult result = mvc.perform(get("/api/events/{buildingId}", 101))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(om.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Event>>(){}), listEvents);
    }

    @Test
    public void whenHttpGetEventByBuildingIdBetweenMinAndMaxDate_thenReturnJson() throws Exception{

        when(eventService.getEventsFromBuilding(101))
                .thenReturn(listEvents);

        MvcResult result = mvc.perform(
                get("/api/events/{buildingId}", 101)
                        .param("minStart", "2010-11-19")
                        .param("maxEnd", "2010-12-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(om.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Event>>(){}), listEvents);

    }

}
