package com.gerwalex.radarplott;

import static org.junit.Assert.assertEquals;

import com.gerwalex.radarplott.radar.Vessel;

import org.junit.Before;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LageTest {
    @Before
    public void initialize() {
    }

    @Test
    public void lage() {
        Vessel myVessel = new Vessel(80, 8);
        Vessel otherVessel = new Vessel('B', 10, 7.0);
        Vessel.Lage lage = otherVessel.setSecondSeitenpeilung(12, 20, 4.5, myVessel);
        assertEquals(141.5, otherVessel.getHeading(), 0.1);
        assertEquals(15.3, otherVessel.getSpeed(), 0.1);
        assertEquals(173.1, otherVessel.getHeadingRelativ(), 0.1);
        assertEquals(13.4, otherVessel.getSpeedRelativ(), 0.1);
        assertEquals(2.0, lage.cpaDistance, 0.1);
        assertEquals(17.9, lage.tCPA, 0.1);
        assertEquals(83.1, lage.pCPA, 0.1);
        assertEquals(2.0, lage.bcrDistance, 0.1);
        assertEquals(17.4, lage.tBCR, 0.1);
    }

    @Test
    public void manoever() {
        Vessel otherVessel = new Vessel('B', 70, 8.0);
        Vessel myVessel = new Vessel(20, 6);
        Vessel.Lage lage = otherVessel.setSecondSeitenpeilung(12, 71, 6, myVessel);
        assertEquals(283.5, otherVessel.getHeading(), 0.1);
        assertEquals(7.4, otherVessel.getSpeed(), 0.1);
        assertEquals(247.0, otherVessel.getHeadingRelativ(), 0.1);
        assertEquals(10.0, otherVessel.getSpeedRelativ(), 0.1);
        assertEquals(0.4, lage.cpaDistance, 0.1);
        assertEquals(35.8, lage.tCPA, 0.1);
        assertEquals(157.0, lage.pCPA, 0.1);
        assertEquals(0.6, lage.bcrDistance, 0.1);
        assertEquals(38.2, lage.tBCR, 0.1);
        myVessel = new Vessel(40, 6);
        lage = otherVessel.setSecondSeitenpeilung(12, 51, 6, myVessel);
        assertEquals(208.0, otherVessel.getHeading(), 0.1);
        assertEquals(2.9, otherVessel.getSpeed(), 0.1);
        assertEquals(247.0, otherVessel.getHeadingRelativ(), 0.1);
        assertEquals(10.0, otherVessel.getSpeedRelativ(), 0.1);
        assertEquals(0.4, lage.cpaDistance, 0.1);
        assertEquals(35.8, lage.tCPA, 0.1);
        assertEquals(157.0, lage.pCPA, 0.1);
        assertEquals(1.9, lage.bcrDistance, 0.1);
        assertEquals(25.0, lage.tBCR, 0.1);
    }
}