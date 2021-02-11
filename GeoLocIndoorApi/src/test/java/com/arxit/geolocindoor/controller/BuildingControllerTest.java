package com.arxit.geolocindoor.controller;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.service.BuildingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BuildingController.class)
public class BuildingControllerTest {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BuildingService buildingService;

    private final Building building1 = new Building(101, "Copernic", new ArrayList<>(), new ArrayList<>());
    private final Building building2 = new Building(666, "Lavoisier", new ArrayList<>(), new ArrayList<>());
    private final List<Building> buildings = List.of(building1, building2);

    @Test
    public void whenHttpGetBuildings_thenReturnJson() throws Exception {
        when(buildingService.getBuildings())
                .thenReturn(buildings);

        MvcResult result = mvc.perform(get("/api/buildings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(om.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Building>>(){}), buildings);
    }

    @Test
    public void whenHttpGetBuildingById_thenReturnJson() throws Exception {
        when(buildingService.getBuildingById(101))
                .thenReturn(building1);
        MvcResult result = mvc.perform(get("/api/building/{id}", 101))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(om.readValue(result.getResponse().getContentAsString(), Building.class), building1);
    }

    @Test
    public void whenHttpGetChecksumById_thenReturnJson() throws Exception {
        when(buildingService.getChecksumById(0))
                .thenReturn((long) 1000);

        MvcResult result = mvc.perform(get("/api/checksum/{id}", 0))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(om.readValue(result.getResponse().getContentAsString(), Long.class), 1000);
    }
}
