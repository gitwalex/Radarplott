package com.gerwalex.radarplott;

import static org.junit.Assert.assertEquals;

import com.gerwalex.radarplott.math.OpponentVessel;
import com.gerwalex.radarplott.math.Punkt2D;
import com.gerwalex.radarplott.math.Vessel;

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
    public void lage1() {
        Vessel me = new Vessel(80, 8);
        OpponentVessel other = new OpponentVessel(me, 0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        assertEquals(141.5, other.getHeadingAbsolut(), 0.1);
        assertEquals(15.3, other.getSpeedAbsolut(), 0.1);
        assertEquals(173.1, other.getHeadingRelativ(), 0.1);
        assertEquals(13.4, other.getSpeedRelativ(), 0.1);
        assertEquals(2.0, other.getAbstandCPA(), 0.1);
        assertEquals(17.9, other.getTimeToCPA(), 0.1);
        assertEquals(83.1, other.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(2.0, other.getAbstandBCR(), 0.1);
        assertEquals(17.4, other.getTimeToBCR(), 0.1);
    }

    @Test
    public void lage2() {
        Vessel me = new Vessel(80, 8);
        OpponentVessel other = new OpponentVessel(me, 0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 12, 4.5);
        assertEquals(149.7, other.getHeadingAbsolut(), 0.1);
        assertEquals(12.8, other.getSpeedAbsolut(), 0.1);
        assertEquals(186.4, other.getHeadingRelativ(), 0.1);
        assertEquals(12.5, other.getSpeedRelativ(), 0.1);
        assertEquals(0.4, other.getAbstandCPA(), 0.1);
        assertEquals(21.4, other.getTimeToCPA(), 0.1);
        assertEquals(96.4, other.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(0.5, other.getAbstandBCR(), 0.1);
        assertEquals(20.8, other.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverSimplified() {
        Vessel me = new Vessel(80, 8);
        Vessel manoever = new Vessel(180, 8);
        OpponentVessel other = new OpponentVessel(me, 0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Vessel manoeverVessel = other.createManoever(manoever, 6);
        Punkt2D cpa = manoever.getCPA(manoeverVessel);
        Punkt2D bcr = manoever.getBCR(manoeverVessel);
        assertEquals(112.5, manoeverVessel.getHeading(), 0.1); // heading
        assertEquals(10.3, manoeverVessel.getSpeed(), 0.1); // speed
        assertEquals(3.3, manoever.getAbstand(cpa), 0.1); // cpa
        assertEquals(-2.7, manoeverVessel.getTimeTo(cpa), 0.1);
        assertEquals(-3.6, manoever.getAbstand(bcr), 0.1);
        assertEquals(-10.7, manoeverVessel.getTimeTo(bcr), 0.1);
    }
}