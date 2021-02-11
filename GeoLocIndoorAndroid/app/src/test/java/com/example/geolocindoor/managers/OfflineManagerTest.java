package com.example.geolocindoor.managers;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.arxit.geolocindoor.common.entities.Building;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class OfflineManagerTest {

    @Test
    public void noBuildingInCacheTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        OfflineManager oManager = OfflineManager.create(appContext);
        assertFalse(oManager.hasCachedBuilding(-666));
        assertFalse(oManager.hasCachedBuilding(420));
    }

    @Test
    public void saveToCacheTest(){

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ApiClient client = ApiClient.create(appContext);
        OfflineManager oManager = OfflineManager.create(appContext);
        Building online = client.getBuilding(0);

        oManager.saveBuildingToCache(online);
        assertTrue(oManager.hasCachedBuilding(0));
    }

    @Test
    public void failBuildingNotCachedGetChecksumTest(){

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        OfflineManager oManager = OfflineManager.create(appContext);

        assertThrows(IllegalStateException.class, () -> oManager.computeBuildingChecksum(420));
    }

    @Test
    public void failBuildingNotCachedGetBuildingTest(){

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        OfflineManager oManager = OfflineManager.create(appContext);

        assertThrows(IllegalStateException.class, () -> oManager.getCachedBuilding(420));
    }

    @Test
    public void cachedChecksumTest(){

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ApiClient apiClient = ApiClient.create(appContext);
        OfflineManager oManager = OfflineManager.create(appContext);
        Building online = apiClient.getBuilding(0);

        oManager.saveBuildingToCache(online);
        assertEquals(oManager.computeBuildingChecksum(0), apiClient.getBuildingChecksum(0));
    }

    @Test
    public void cachedBuildingTest(){

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ApiClient apiClient = ApiClient.create(appContext);
        OfflineManager oManager = OfflineManager.create(appContext);
        Building online = apiClient.getBuilding(0);

        oManager.saveBuildingToCache(online);
        Building cached = oManager.getCachedBuilding(0);

        assertEquals(online.hashCode(), cached.hashCode());
    }
}
