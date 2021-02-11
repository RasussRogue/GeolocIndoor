package com.arxit.geolocindoor.service;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.repository.EventRepository;
import com.arxit.geolocindoor.repository.VisiblePOIRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class POIServiceTest {

    @Mock
    private VisiblePOIRepository poiRepository;

    @InjectMocks
    private VisiblePOIService poiService;

    VisiblePOI vis1 = new VisiblePOI();
    VisiblePOI vis2 = new VisiblePOI();
    VisiblePOI vis3 = new VisiblePOI();

    @Test
    public void whenGetVisibles_thenReturnAllVisibles(){
        Floor floor = new Floor();
        floor.setId(1);

        vis1.setFloor(floor);
        vis2.setFloor(floor);


        when(poiRepository.findAllByFloorId(1))
                .thenReturn(List.of(vis1, vis2));

        assertEquals(List.of(vis1, vis2), poiService.getVisibles(1));
    }

    @Test
    public void whenGetTypes_thenReturnAllTypes(){

        vis1.setType("Truck");
        vis2.setType("Alouette");
        vis3.setType("Comete");


        when(poiRepository.findAll())
                .thenReturn(List.of(vis1, vis2, vis3));

        assertEquals(Set.of("Truck", "Alouette", "Comete"), poiService.getTypes());
    }


}
