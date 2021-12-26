package com.gerwalex.radarplott.math;

import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Die Klasse Gerade2D bietet Funktionen zur Berechnung von Werten im 2-dimensionalen Raum
 *
 * @author Alexander Winkler
 */
public class Gerade2D {
    // Gerade2D ax + by +c = 0
    private final double a, b, c;
    // Gerade2D Punkt2D_A + lambda * Richtungsvektor
    // Gerade2D y = mx + n
    private final double m, n;
    private final Punkt2D punkt1, punkt2;
    /* Richtungsvektor rv und Normalenektor nv der Geraden */
    private final Vektor2D rv, nv;

    /**
     * Erstellt eine Gerade durch zwei Punkte
     *
     * @param von  1. Punkt, durch den die Gerade laeuft
     * @param nach 2. Punkt, durch den die Gerade laeuft
     */
    public Gerade2D(Punkt2D von, Punkt2D nach) {
        /*
         * Festlegung einer Gerade2D durch zwei Punkte
         */
        this(von, nach, new Vektor2D(von, nach));
    }

    /**
     * Erstellt eine Gerade durch einen Punkt mit einem Richtungsvektor
     *
     * @param von Punkt, durch den die Gerade laeuft
     * @param v   Richtungsvektor der Geraden
     */
    public Gerade2D(Punkt2D von, Vektor2D v) {
        /*
         * Festlegung einer Gerade2D durch einen Punkt von und dem
         * Richtungsvektor v
         */
        this(von, new Punkt2D(von.x + v.getEndpunkt().x, von.y + v.getEndpunkt().y), v);
    }

    /**
     * Standardkonstruktor fuer eine Gerade, in den jeweiligen Konstruktoren werden die Werte
     * belegt
     *
     * @param von,     nach Punkte, durch die die Gerade laeuft
     * @param richtung der Geraden
     */
    private Gerade2D(Punkt2D von, Punkt2D nach, Vektor2D richtung) {
        if (von.equals(nach)) {
            Log.d("gerwalex",
                    "Fehler bei Gerade(Punkt von, Punkt nach, Vektor richtung), Parameter: " + von + " / " + nach +
                            " / " + richtung.toString());
            throw new IllegalArgumentException("Die Basispunkte einer Gerade duerfen nicht identisch sein");
        }
        double x1 = von.x;
        double y1 = von.y;
        double x2 = nach.x;
        double y2 = nach.y;
        punkt1 = von;
        punkt2 = nach;
        rv = richtung.getEinheitsvektor();
        double dx = x2 - x1;
        double dy = y2 - y1;
        a = -dy;
        b = dx;
        c = -(x1 * a + y1 * b);
        nv = new Vektor2D(new Punkt2D(a, b)).getEinheitsvektor();
        if (x2 != x1) {
            m = (y2 - y1) / (x2 - x1);
            n = y1 - x1 * m;
        } else {
            m = Double.NaN;
            n = Double.NaN;
        }
    }

    /**
     * Ermittelt den Abstand der Geraden zu einem Punkt P
     *
     * @param x, y Koordinaten des Punktes, zu dem der Abstand ermittelt werden soll
     * @return Abstand
     */
    public double getAbstand(double x, double y) {
        double q = a * a + b * b;
        double p = a * x + b * y + c;
        return (p) / Math.sqrt(q);
    }

    /**
     * Ermittelt den Abstand der Geraden zu einem Punkt P
     *
     * @param pkt Punkt, zu dem der Abstand ermittelt werden soll
     * @return Abstand
     */
    public double getAbstand(Punkt2D pkt) {
        double q = a * a + b * b;
        double p = a * pkt.x + b * pkt.y + c;
        return (p) / Math.sqrt(q);
    }


    /**
     * Ermittelt den Abstand zweier Punkte auf der Geraden.
     *
     * @param p Ein Punkt auf der Geraden
     * @param q Zweiter Punkt auf der Geraden
     * @return Abstand der Punkte. Liegt einer der Punkte nicht auf der Geraden, wird Double.NaN
     * zurueckgegebeb
     * @throws IllegalArgumentException wenn einer oder beide Punkte nicht auf der Geraden liegen
     */
    public double getAbstandPunkte(Punkt2D p, Punkt2D q) throws IllegalArgumentException {
        if (isPunktAufGerade(p) || isPunktAufGerade(q)) {
            return p.abstand(q);
        } else {
            throw new IllegalArgumentException("Beide Punkte muessen auf der Geraden liegen");
        }
    }

    /**
     * Liefert den Lotpunkt auf der Geraden zum uebergebenen Punkt zurueck
     *
     * @param p Punkt, zu dem der Lotpunkt berechnet werden soll. Darf nicht auf der Geraden liegen
     * @return Punkt auf der Gerade, der senkrecht zum uebergebenen Punkt liegt.
     */
    public Punkt2D getLotpunkt(Punkt2D p) {
        double abstand = (double) getAbstand(p);
        double q1 = p.x + nv.getEndpunkt().x * abstand;
        double q2 = p.y + nv.getEndpunkt().y * abstand;
        Punkt2D q = new Punkt2D(q1, q2);
        if (!isPunktAufGerade(q)) {
            q1 = p.x - nv.getEndpunkt().x * abstand;
            q2 = p.y - nv.getEndpunkt().y * abstand;
            q = new Punkt2D(q1, q2);
        }
        if (!isPunktAufGerade(q)) {
            System.out.println(p + " nicht auf Gerade");
        }
        return q;
    }

    /**
     * Endpunkt der Geraden bei der Konstruktion
     *
     * @return Endpunkt
     */
    public Punkt2D getNach() {
        return punkt2;
    }

    /**
     * Normalenvektor des Richtungsvektors der Geraden
     *
     * @return liefert den Normalenvektor als Einheitsvektor zurueck
     */
    public Vektor2D getNormalenvektor() {
        return nv;
    }

    /**
     * Ermittelt zwei Punkte auf der Gerade, die einen Abstand d vom uebergebenen Punkt p (der nicht
     * auf der Geraden liegt) haben.
     *
     * @param p Punkt, zu dem der Abstand der Geraden berechnet werden soll. Darf nicht auf der
     *          Geraden liegen, ansonsten gibt die Methode false zurueck
     * @param d gewuenschter Abstand der Punkte auf der Geraden
     * @return Array mit 2 Punkten oder Null, wenn Punkt auf der Geraden liegt.
     */
    @Nullable
    public Punkt2D[] getPunkteAufGerade(Punkt2D p, double d) {
        if (isPunktAufGerade(p)) {
            return null;
        }
        Kreis2D k = new Kreis2D(p, d);
        return getSchnittpunkt(k);
    }

    /**
     * Richtungsvektor der Geraden als Einheitsvektor (Vektorlaenge = 1)
     *
     * @return liefert den Richtungsvektor als Einheitsvektor zurueck
     */
    public Vektor2D getRichtungsvektor() {
        return rv;
    }

    /**
     * Schnittpunkt zweier Geraden
     *
     * @param g Gerade, mit der ein Schnittpunkt ermittelt werden soll
     * @return Schnittpunkt. Ist null, wenn Geraden parallel laufen
     */
    public Punkt2D getSchnittpunkt(Gerade2D g) {
        // liefert den Schnittpunkt zweier Geraden
        double x, y;
        if (g.a != 0) {
            y = (a / g.a * g.c - c) / (b - a / g.a * g.b);
            x = (g.b * y + g.c) / (-g.a);
        } else if (a != 0) {
            y = (g.a / a * c - g.c) / (g.b - g.a / a * b);
            x = (b * y + c) / (-a);
        } else {
            return null;
        }
        return new Punkt2D(x, y);
    }

    /**
     * Schnittpunkte der Geraden mit einem {@link Kreis2D}
     *
     * @param k Kreis
     * @return Schnittpunkte der Gerade mit dem Kreis. Gibt es keine Schnittpunkte, wird jeweils
     * null zurueckgeliefert. Bei genau einem Schnittpunkt (Tangente) sind beide Punkte identisch
     */
    public Punkt2D[] getSchnittpunkt(Kreis2D k) {
        /*
         * liefert die Schnittpunkte eines uebergebenen Kreises mit der Gerade
         */
        Punkt2D[] v = new Punkt2D[2];
        v[0] = null;
        v[1] = null;
        double r = k.getRadius();
        double m1 = k.getMittelpunkt().x;
        double m2 = k.getMittelpunkt().y;
        if ((Math.round(b * 1E6)) / 1E6 != 0) {
            double s = -b * m2 - c;
            double t = (b * b * m1 + s * a) / (a * a + b * b);
            double z1 = r * r * b * b - b * b * m1 * m1 - s * s;
            double z2 = a * a + b * b;
            double z3 = t * t;
            double z4 = z1 / z2 + z3;
            double zwischenergebnis = Math.round(z4 * 1000000);
            if (zwischenergebnis >= 0) {
                zwischenergebnis = (double) Math.sqrt(zwischenergebnis / 1000000);
                zwischenergebnis = -zwischenergebnis;
                double x = zwischenergebnis + t;
                v[0] = new Punkt2D(x, (-c - a * x) / b);
                x = -zwischenergebnis + t;
                v[1] = new Punkt2D(x, (-c - a * x) / b);
            }
        } else {
            double x = c / a;
            double y = (double) (m2 + Math.sqrt(r * r - Math.pow(c / a - m1, 2)));
            v[0] = new Punkt2D(x, y);
            y = (double) (m2 - Math.sqrt(r * r - Math.pow(c / a - m1, 2)));
            v[1] = new Punkt2D(x, y);
        }
        return v;
    }

    /**
     * Startpunkt der Geraden bei der Konstruktion
     *
     * @return Startpunkt
     */
    public Punkt2D getVon() {
        return punkt1;
    }

    /**
     * Winkel der Geraden zur Y-Achse
     *
     * @return ermittelt den Winkel den die Gerade mit der y-Achse bildet (in 360 Grad-Darstellung)
     */
    public double getYAxisAngle() {
        return rv.getYAxisAngle();
    }

    /**
     * Prueft, ob ein Punkt auf der Geraden liegt
     *
     * @param p Punkt
     * @return true, wenn Punkt auf der Geraden liegt
     */
    public boolean isPunktAufGerade(Punkt2D p) {
        return Math.round(getAbstand(p) * 1E6) == 0.0;
    }

    /**
     * Rundet eine Double auf 6 Stellen (1E6)
     *
     * @param d Wert, der gerundet werden soll
     * @return gerundeter Wert
     */
    private double rundeWert(double d) {
        return Math.round(d * 1E6) / 1E6;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        double a = rundeWert(this.a);
        double b = rundeWert(this.b);
        double c = rundeWert(this.c);
        return ("Geradengleichung: " + a + " x  + " + b + " y + " + c + " = 0 Punkt: " + punkt1.toString() +
                "Normalenvektor: " + getNormalenvektor().toString() + ", Richtungsvektor: " + rv.toString() + "m = " +
                rundeWert(m) + ", n = " + rundeWert(n) + "(y= " + rundeWert(m) + " x " + rundeWert(n) + ")");
    }

    /**
     * Verschiebt die Gerade parallel in einen neuen Punkt.
     *
     * @param p Punkt, in den die neue Gerade verschoben werden soll
     * @return neue Gerade, verschoben in den Punkt.Der Punkt ist Startpunkt der Gearden
     */
    public Gerade2D verschiebeParallell(Punkt2D p) {
        return new Gerade2D(p, getRichtungsvektor());
    }
}
