package com.gerwalex.radarplott.math;

public class Kurslinie extends Gerade2D {
    public Kurslinie(Punkt2D first, Punkt2D second) {
        super(first, second);
    }

    public Kurslinie(Punkt2D p, Vektor2D richtungsvektor) {
        super(p, richtungsvektor);
    }

    @Override
    public Kurslinie verschiebeParallell(Punkt2D p) {
        return new Kurslinie(p, getRichtungsvektor());
    }
}
