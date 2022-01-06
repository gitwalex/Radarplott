package com.gerwalex.radarplott;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;

import com.gerwalex.radarplott.main.OpponentVessel;
import com.gerwalex.radarplott.main.Vessel;
import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Kreis2D;
import com.gerwalex.radarplott.math.Punkt2D;
import com.gerwalex.radarplott.math.Vektor2D;

import org.junit.Before;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LageTest {

    private float getHeading(@NonNull OpponentVessel other, int minutes, float distance) {
        Vessel me = new Vessel(80, 8);
        Punkt2D cpa = other.getCPA(me);
        Float h = null;
        if (minutes >= 0 && other.getTimeTo(cpa) > minutes) {
            Punkt2D otherFuturePosition = other.getPosition(minutes);
            Kreis2D k = new Kreis2D(new Punkt2D(), distance);
            Punkt2D[] bp = k.getBeruehrpunkte(otherFuturePosition);
            if (bp != null) {
                Vektor2D v = new Vektor2D(other.getSecondPosition(), bp[0]);
                Punkt2D relPos = other.getRelPosition(me).add(v);
                h = new Gerade2D(relPos, otherFuturePosition).getYAxisAngle();
                System.out.printf("heading1: %03.1f%n", h);
                v = new Vektor2D(other.getSecondPosition(), bp[1]);
                relPos = other.getRelPosition(me).add(v);
                h = new Gerade2D(relPos, otherFuturePosition).getYAxisAngle();
                System.out.printf("heading1: %03.1f%n", h);
            }
        }
        return h;
    }

    private void getOpponentVessel(@NonNull OpponentVessel other, int minutes, float distance) {
        Vessel me = new Vessel(80, 8);
        if (minutes >= 0) {
        }
    }

    @Before
    public void initialize() {
    }

    @Test
    public void lageNeu() {
        Vessel me = new Vessel(80, 8);
        OpponentVessel otherVessel = new OpponentVessel(0, 'B', 10, 7.0);
        otherVessel.setSecondSeitenpeilung(12, 20, 4.5);
        Punkt2D relPos = otherVessel.getRelPosition(me);
        assertEquals(141.5, otherVessel.getHeadingRelativ(), 0.1);
        assertEquals(15.3, otherVessel.getSpeedRelativ(), 0.1);
        assertEquals(173.1, otherVessel.getHeading(), 0.1);
        assertEquals(13.4, otherVessel.getSpeed(), 0.1);
        assertEquals(2.0, otherVessel.getAbstandCPA(me), 0.1);
        assertEquals(83.1, otherVessel.getPeilungRechtweisendCPA(me), 0.1);
        assertEquals(2.0, otherVessel.getAbstandBCR(me), 0.1);
        assertEquals(17.9, otherVessel.getRelativTimeTo(otherVessel.getCPA(me)), 0.1);
        assertEquals(17.4, otherVessel.getRelativTimeTo(otherVessel.getBCR(me)), 0.1);
    }

    @Test
    public void manoever() {
        OpponentVessel otherVessel = new OpponentVessel(0, 'B', 10, 7.0);
        otherVessel.setSecondSeitenpeilung(12, 20, 4.5);
        getHeading(otherVessel, 6, 2.1f);
        getHeading(otherVessel, 6, 2.5f);
    }
}