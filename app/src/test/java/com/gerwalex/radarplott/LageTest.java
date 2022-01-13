package com.gerwalex.radarplott;

import static org.junit.Assert.assertEquals;

import com.gerwalex.radarplott.math.Lage;
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
        OpponentVessel other = new OpponentVessel(0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Lage lage = new Lage(me, other.getRelativeVessel());
        assertEquals(141.5, lage.getHeadingAbsolut(), 0.1);
        assertEquals(15.3, lage.getSpeedAbsolut(), 0.1);
        assertEquals(173.1, lage.getHeadingRelativ(), 0.1);
        assertEquals(13.4, lage.getSpeedRelativ(), 0.1);
        assertEquals(2.0, lage.getAbstandCPA(), 0.1);
        assertEquals(17.9, lage.getTimeToCPA(), 0.1);
        assertEquals(83.1, lage.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(2.0, lage.getAbstandBCR(), 0.1);
        assertEquals(17.4, lage.getTimeToBCR(), 0.1);
    }

    @Test
    public void lage2() {
        Vessel me = new Vessel(80, 8);
        OpponentVessel other = new OpponentVessel(0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 12, 4.5);
        Lage lage = new Lage(me, other.getRelativeVessel());
        assertEquals(149.7, lage.getHeadingAbsolut(), 0.1);
        assertEquals(12.8, lage.getSpeedAbsolut(), 0.1);
        assertEquals(186.4, lage.getHeadingRelativ(), 0.1);
        assertEquals(12.5, lage.getSpeedRelativ(), 0.1);
        assertEquals(0.4, lage.getAbstandCPA(), 0.1);
        assertEquals(21.4, lage.getTimeToCPA(), 0.1);
        assertEquals(96.4, lage.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(0.5, lage.getAbstandBCR(), 0.1);
        assertEquals(20.8, lage.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverLage() {
        Vessel me = new Vessel(80, 8);
        OpponentVessel other = new OpponentVessel(0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Lage manoever = other.getManoever(me, 6, 180, 8);
        assertEquals(112.5, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(10.3, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(3.3, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(-2.7, manoever.getTimeToCPA(), 0.1);
        assertEquals(-3.6, manoever.getAbstandBCR(), 0.1);
        assertEquals(-10.7, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverLage1() {
        Vessel me = new Vessel(80, 8);
        OpponentVessel other = new OpponentVessel(0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Lage manoever = new Lage(me, other.getRelativeVessel(), 6, 180, 8);
        assertEquals(112.5, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(10.3, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(3.3, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(-2.7, manoever.getTimeToCPA(), 0.1);
        assertEquals(-3.6, manoever.getAbstandBCR(), 0.1);
        assertEquals(-10.7, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverSimplified() {
        Vessel me = new Vessel(80, 8);
        Vessel manoever = new Vessel(180, 8);
        OpponentVessel other = new OpponentVessel(0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Vessel manoeverVessel = other.createManoever(me, manoever, 6);
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