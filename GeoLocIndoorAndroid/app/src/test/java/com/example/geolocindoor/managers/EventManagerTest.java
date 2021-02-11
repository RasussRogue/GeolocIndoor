package com.example.geolocindoor.managers;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class EventManagerTest {

    @Test
    public void allEventsTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BuildingManager buildingManager = BuildingManager.create(appContext);
        EventManager eventManager = EventManager.create(appContext);

        /* Building copernic = buildingManager.getBuilding();
        eventManager.getEvents(copernic)
                .forEach(e -> System.out.println(e.getTitle() + " - " + e.getDescription() + " - " + e.getStart() + " * " + e.getEnd() + " - " + e.getLocation().getFloor().getLevel())); */
    }

    @Test
    public void matchingEventsTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BuildingManager buildingManager = BuildingManager.create(appContext);
        EventManager eventManager = EventManager.create(appContext);

        /* Building copernic = buildingManager.getBuilding();
        List<Event> allEvents = eventManager.getEvents(copernic);
        System.out.println(eventManager.getEventsMatching(allEvents, "ôktòBéR").size()); */
    }
}
