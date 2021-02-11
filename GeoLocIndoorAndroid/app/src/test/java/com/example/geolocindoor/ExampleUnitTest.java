package com.example.geolocindoor;

import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.common.utils.StringUtils;

import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertTrue;

/**
 * Example local unit ico_teaching, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void matchesTest(){
        VisiblePOI p = new VisiblePOI();
        p.setType("floor");
        p.setTitle("Escaliers SUD");
        assertTrue(p.matches(StringUtils.unaccent("esc")));

        ZonedDateTime now = ZonedDateTime.now();
        System.out.println(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now));
        System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(now));
        System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
    }
}