package com.arxit.geolocindoor.service;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.utils.ChecksumHandler;
import com.arxit.geolocindoor.repository.BuildingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@SpringBootTest
public class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private ChecksumHandler checksumHandler;

    @InjectMocks
    private BuildingService buildingService;

    private final Building building1 = new Building(101, "Copernic", new ArrayList<>(), new ArrayList<>());
    private final Building building2 = new Building(666, "Lavoisier", new ArrayList<>(), new ArrayList<>());

    @Test
    public void whenGetBuildings_thenReturnListBuildings(){

        List<Building> buildings = List.of(building1, building2);

        when(buildingRepository.findAll())
                .thenReturn(buildings);

        assertEquals(buildings, buildingService.getBuildings());
    }

    @Test
    public void whenGetBuildingById_thenReturnBuilding(){

        when(buildingRepository.findById(101))
                .thenReturn(building1);

        assertEquals(building1, buildingService.getBuildingById(101));
    }

}
