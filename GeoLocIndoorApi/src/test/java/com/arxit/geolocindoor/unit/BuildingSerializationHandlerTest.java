package com.arxit.geolocindoor.unit;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.common.utils.BuildingSerializationHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BuildingSerializationHandlerTest {

    private BuildingSerializationHandler handler = new BuildingSerializationHandler();

    @Test
    public void shouldSerializeAndDeserializeBuildingCorrectly() {
        //given
        Building copernic = new Building(101, "Copernic", new ArrayList<>(), new ArrayList<>());

        //when
        Building result = handler.deserializeBuilding(handler.serializeBuilding(copernic));

        //then
        assertEquals(copernic, result);
    }

    @Test
    public void shouldDeserializeAndSerializeBuildingCorrectly() {
        //given
        Building copernic = new Building(101, "Copernic", new ArrayList<>(), new ArrayList<>());
        String serialized = handler.serializeBuilding(copernic);

        //when
        String result = handler.serializeBuilding(handler.deserializeBuilding(serialized));

        //then
        assertEquals(serialized, result);
    }

    @Test
    public void shouldSerializeAndDeserializeEventsCorrectly(){
        //given
        String json = "[{\"id\":666,\"title\":\"Foire à la so6\",\"description\":\"Vendez vos saucisses en toute simplicité\"," +
                "\"start\":\"2020-01-01T00:00:00+01:00\",\"end\":\"2020-02-20T00:00:00+01:00\"," +
                "\"location\":{\"class\":\"com.arxit.geolocindoor.common.entities.VisiblePOI\",\"id\":200,\"wkt\":\"POINT(2.58696070051265 48.8390722878262)\",\"title\":\"Stand à so6\",\"type\":\"food\"}}]";

        VisiblePOI location = new VisiblePOI();
        location.setId(200);
        location.setWkt("POINT(2.58696070051265 48.8390722878262)");
        location.setTitle("Stand à so6");
        location.setType("food");

        ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("GMT+01:00"));
        ZonedDateTime end = ZonedDateTime.of(2020, 2, 20, 0, 0, 0, 0, ZoneId.of("GMT+01:00"));
        Event event = new Event(666, "Foire à la so6", "Vendez vos saucisses en toute simplicité", start, end, location);

        //when
        List<Event> deserialized = handler.deserializeEvents(json);

        //then
        assertEquals(1, deserialized.size());
        assertEquals(event, deserialized.get(0));
    }

    @Test
    public void shouldDeserializeAndSerializeEventCorrectly() {
        //given
        VisiblePOI location = new VisiblePOI();
        location.setId(200);
        location.setWkt("POINT(2.58696070051265 48.8390722878262)");
        location.setTitle("Stand à so6");
        location.setType("food");
        Event event = new Event(666, "TITLE", "DESCRIPTION", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1), location);
        String serialized = handler.serializeEvents(List.of(event));

        //when
        String result = handler.serializeEvents(handler.deserializeEvents(serialized));

        //then
        assertEquals(serialized, result);
    }



}
