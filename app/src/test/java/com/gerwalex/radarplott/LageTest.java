package com.gerwalex.radarplott;

import static org.junit.Assert.assertEquals;

import com.gerwalex.radarplott.main.OpponentVessel;
import com.gerwalex.radarplott.main.Vessel;
import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

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
    public void lageNeu() {
        Vessel me = new Vessel(80, 8);
        OpponentVessel other = new OpponentVessel(0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        other.getRelPosition(me);
        assertEquals(141.5, other.getHeadingAbsolut(), 0.1);
        assertEquals(15.3, other.getSpeedAbsolut(), 0.1);
        assertEquals(173.1, other.getHeading(), 0.1);
        assertEquals(13.4, other.getSpeed(), 0.1);
        Punkt2D cpa = me.getCPA(other);
        assertEquals(2.0, me.getAbstand(cpa), 0.1);
        assertEquals(83.1, me.getPeilungRechtweisend(cpa), 0.1);
        Punkt2D bcr = me.getBCR(other);
        assertEquals(2.0, me.getAbstand(bcr), 0.1);
        assertEquals(17.9, other.getRelativTimeTo(me.getCPA(other)), 0.1);
        assertEquals(17.4, other.getRelativTimeTo(me.getBCR(other)), 0.1);
    }

    @Test
    public void manoever() {
        Vessel me = new Vessel(80, 8);
        Vessel manoever = new Vessel(180, 8);
        OpponentVessel other = new OpponentVessel(0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Punkt2D relPosition = other.getRelPosition(me);
        Punkt2D manoeverPosition = other.getPosition(6);
        Punkt2D meManoeverPosition = manoever.getPosition(other.getMinutes());
        Punkt2D relPos = relPosition.add(meManoeverPosition);
        Gerade2D kurslinie = new Gerade2D(relPos, other.getSecondPosition());
        kurslinie.verschiebeParallell(manoeverPosition);
        float heading = kurslinie.getYAxisAngle();
        float speed = relPos.getAbstand(other.getSecondPosition()) * 60 / other.getMinutes();
        Vessel manoeverVessel = new Vessel(manoeverPosition, heading, speed);
        Punkt2D cpa = manoever.getCPA(manoeverVessel);
        Punkt2D bcr = manoever.getBCR(manoeverVessel);
        assertEquals(112.5, manoeverVessel.getHeading(), 0.1); // heading
        assertEquals(10.3, manoeverVessel.getSpeed(), 0.1); // speed
        assertEquals(3.3, manoever.getAbstand(cpa), 0.1); // cpa
        assertEquals(-2.7, manoeverVessel.getTimeTo(cpa), 0.1);
        assertEquals(-3.6, manoever.getAbstand(bcr), 0.1);
        assertEquals(-10.7, manoeverVessel.getTimeTo(bcr), 0.1);
    }

    @Test
    public void manoeverSimplified() {
        Vessel me = new Vessel(80, 8);
        Vessel manoever = new Vessel(180, 8);
        OpponentVessel other = new OpponentVessel(0, 'B', 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        other.getRelPosition(me);
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