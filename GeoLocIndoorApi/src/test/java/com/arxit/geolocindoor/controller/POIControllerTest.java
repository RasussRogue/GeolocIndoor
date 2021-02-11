package com.arxit.geolocindoor.controller;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.service.EventService;
import com.arxit.geolocindoor.service.VisiblePOIService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = VisiblePOIController.class)
public class POIControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private VisiblePOIService poiService;

    VisiblePOI vis1 = new VisiblePOI();
    VisiblePOI vis2 = new VisiblePOI();
    VisiblePOI vis3 = new VisiblePOI();

    @Test
    public void whenHttpGetVisiblesByFloorId_thenReturnCorrectVisibles() throws Exception {
        Floor floor = new Floor();
        floor.setId(1);

        vis1.setFloor(floor);
        vis2.setFloor(floor);
        when(poiService.getVisibles(1))
                .thenReturn(List.of(vis1, vis2));

        MvcResult result = mvc.perform(get("/api/visibles/{floorId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(om.readValue(result.getResponse().getContentAsString(), new TypeReference<List<VisiblePOI>>(){}), List.of(vis1, vis2));
    }

    @Test
    public void whenHttpGetTypes_thenReturnCorrectTypes() throws Exception {
        vis1.setType("Truck");
        vis2.setType("Alouette");
        vis3.setType("Comete");

        when(poiService.getTypes())
                .thenReturn(Set.of("Truck", "Alouette", "Comete"));

        MvcResult result = mvc.perform(get("/api/types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(om.readValue(result.getResponse().getContentAsString(), new TypeReference<Set<String>>(){}), Set.of("Truck", "Alouette", "Comete"));
    }

}
