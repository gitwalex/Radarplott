package com.gerwalex.radarplott;

import static org.junit.Assert.assertEquals;

import com.gerwalex.radarplott.math.Lage;
import com.gerwalex.radarplott.math.Opponent;
import com.gerwalex.radarplott.math.Vessel;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LageTest {

    @Test
    public void boje1() {
        Vessel me = new Vessel(0, 6);
        Opponent other = new Opponent(me, 0, "B", 0, 7.0);
        other.setSecondSeitenpeilung(60, 0, 1.0);
        Lage lage = Objects.requireNonNull(other.getLage());
        assertEquals(180.0, lage.getHeadingRelativ(), 0.1); // heading
        assertEquals(6.0, lage.getSpeedRelativ(), 0.1); // speed
        assertEquals(0, lage.getAbstandCPA(), 0.1); // cpa
        assertEquals(10.0, lage.getTimeToCPA(), 0.1);
    }

    @Test
    public void cpa1() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        other.createManoeverLage(3.5f, 0);
        Lage manoever = Objects.requireNonNull(other.manoever.get());
        assertEquals(3.5, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(23.0, manoever.getTimeToCPA(), 0.1);
        assertEquals(148.9, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(7.4, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(58.9, manoever.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(14.2, manoever.getAbstandBCR(), 0.1);
        assertEquals(134.7, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void cpa2() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Lage manoever = other.getLage().getLage(3.5f, 0);
        assertEquals(148.9, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(7.4, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(3.5, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(23.0, manoever.getTimeToCPA(), 0.1);
        assertEquals(58.9, manoever.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(14.2, manoever.getAbstandBCR(), 0.1);
        assertEquals(134.7, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void cpa3() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Lage manoever = other.getLage().getLage(3.3f, 6);
        assertEquals(131.1, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(7.5, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(3.3, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(5.0, manoever.getTimeToCPA(), 0.1);
        assertEquals(41.1, manoever.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(-9.6, manoever.getAbstandBCR(), 0.1);
        assertEquals(-67.5, manoever.getTimeToBCR(), 0.1);
    }

    @Before
    public void initialize() {
    }

    @Test
    public void lage1() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
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
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
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
    public void lage3() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
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
    public void manoeverLage1() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Vessel v = new Vessel(180, 8);
        other.createManoeverLage(v, 6);
        Lage manoever = Objects.requireNonNull(other.manoever.get());
        assertEquals(112.5, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(10.3, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(3.3, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(-2.7, manoever.getTimeToCPA(), 0.1);
        assertEquals(-3.6, manoever.getAbstandBCR(), 0.1);
        assertEquals(-10.7, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverLage2() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Vessel v = new Vessel(110, 8);
        other.createManoeverLage(v, 6);
        Lage manoever = Objects.requireNonNull(other.manoever.get());
        assertEquals(167.9, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(9.4, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(2.3, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(15.8, manoever.getTimeToCPA(), 0.1);
        assertEquals(77.9, manoever.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(2.7, manoever.getAbstandBCR(), 0.1);
        assertEquals(24.9, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverLage3() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Vessel v = new Vessel(110, 8);
        other.createManoeverLage(v, 12);
        Lage manoever = Objects.requireNonNull(other.manoever.get());
        assertEquals(167.9, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(9.4, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(2.1, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(7.2, manoever.getTimeToCPA(), 0.1);
        assertEquals(77.9, manoever.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(2.5, manoever.getAbstandBCR(), 0.1);
        assertEquals(15.9, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverLage4() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Vessel v = new Vessel(180, 8);
        Lage manoever = other.getLage().getLage(v, 6);
        assertEquals(112.5, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(10.3, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(3.3, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(-2.7, manoever.getTimeToCPA(), 0.1);
        assertEquals(-3.6, manoever.getAbstandBCR(), 0.1);
        assertEquals(-10.7, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverLage5() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Vessel v = new Vessel(110, 8);
        Lage manoever = other.getLage().getLage(v, 6);
        assertEquals(167.9, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(9.4, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(2.3, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(15.8, manoever.getTimeToCPA(), 0.1);
        assertEquals(77.9, manoever.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(2.7, manoever.getAbstandBCR(), 0.1);
        assertEquals(24.9, manoever.getTimeToBCR(), 0.1);
    }

    @Test
    public void manoeverLage6() {
        Vessel me = new Vessel(80, 8);
        Opponent other = new Opponent(me, 0, "B", 10, 7.0);
        other.setSecondSeitenpeilung(12, 20, 4.5);
        Vessel v = new Vessel(110, 8);
        Lage manoever = other.getLage().getLage(v, 12);
        assertEquals(167.9, manoever.getHeadingRelativ(), 0.1); // heading
        assertEquals(9.4, manoever.getSpeedRelativ(), 0.1); // speed
        assertEquals(2.1, manoever.getAbstandCPA(), 0.1); // cpa
        assertEquals(7.2, manoever.getTimeToCPA(), 0.1);
        assertEquals(77.9, manoever.getPeilungRechtweisendCPA(), 0.1);
        assertEquals(2.5, manoever.getAbstandBCR(), 0.1);
        assertEquals(15.9, manoever.getTimeToBCR(), 0.1);
    }
}