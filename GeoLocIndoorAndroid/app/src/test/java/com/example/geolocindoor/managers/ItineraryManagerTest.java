package com.example.geolocindoor.managers;

import android.content.Context;
import android.location.Location;

import androidx.test.platform.app.InstrumentationRegistry;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.POI;
import com.example.geolocindoor.itinerary.Itinerary;
import com.example.geolocindoor.itinerary.ItineraryManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ItineraryManagerTest {

    @Test
    public void itineraryTest(){

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BuildingManager buildingManager = BuildingManager.create(appContext);
        /* Building copernic = buildingManager.getBuilding();

        Location startLocation = new Location("");
        startLocation.setLongitude(2.58657);
        startLocation.setLatitude(48.83959);
        int floorLevel = 0;

        ItineraryManager iManager = ItineraryManager.getInstance();
        iManager.setBuilding(copernic, false);

        POI target = copernic.getEdges().get(copernic.getEdges().size() - 1).getEnd();
        Itinerary i = iManager.computeItinerary(startLocation, floorLevel, target);

        System.out.println("START LOCATION : " + startLocation);
        System.out.println("FIRST POI : " + i.getPoints().get(0));
        System.out.println("LAST POI : " + i.getPoints().get(i.getPoints().size() - 1));
        System.out.println("TARGET POI : " + target); */

    }
}
