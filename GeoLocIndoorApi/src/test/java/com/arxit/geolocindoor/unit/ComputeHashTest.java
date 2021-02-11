package com.arxit.geolocindoor.unit;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.utils.ChecksumHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class ComputeHashTest {

    @Test
    public void shouldFindEquivalenceBetweenTwoEventHashes() {
        //given
        Building a = new Building(0, "Copernic", new ArrayList<>(), new ArrayList<>());
        Building b = new Building(1, "Lavoisier", new ArrayList<>(), new ArrayList<>());
        ChecksumHandler ch = new ChecksumHandler();

        //when
        long ares = ch.computeBuildingChecksum(a);
        long bres = ch.computeBuildingChecksum(b);

        //thengetChecksumById
        assertEquals(ares, ares);
        assertNotEquals(ares, bres);
    }
}
