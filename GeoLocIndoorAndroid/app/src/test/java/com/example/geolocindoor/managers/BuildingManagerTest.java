package com.example.geolocindoor.managers;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.arxit.geolocindoor.common.entities.Building;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class BuildingManagerTest {

    @Test
    public void getOnlineBuildingTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BuildingManager buildingManager = BuildingManager.create(appContext);
        /* Building online = buildingManager.getBuilding();
        assertNotEquals(null, online);
        assertEquals(0, online.getId());
        assertEquals("Copernic", online.getName()); */
    }

    @Test
    public void notExistingBuildingFailTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BuildingManager buildingManager = BuildingManager.create(appContext);
        /* assertThrows(RuntimeException.class, () -> buildingManager.getBuilding(420));
        assertThrows(RuntimeException.class, () -> buildingManager.getBuilding(666)); */
    }

    @Test
    public void matchingPlacesTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BuildingManager buildingManager = BuildingManager.create(appContext);
        /* Building copernic = buildingManager.getBuilding();
        copernic.getPlacesMatching("éScalièrS")
                .forEach(p -> System.out.println(p.getTitle() + " [" + p.getType() + "]")); */
    }
}
