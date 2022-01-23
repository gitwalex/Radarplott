package com.gerwalex.radarplott.math;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Die Klasse Gerade2D bietet Funktionen zur Berechnung von Werten im 2-dimensionalen Raum
 *
 * @author Alexander Winkler
 */
public class Gerade2D {
    // Gerade2D ax + by +c = 0
    private final float a, b, c;
    // Gerade2D Punkt2D_A + lambda * Richtungsvektor
    // Gerade2D y = mx + n
    private final float m, n;
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
        this(von, new Vektor2D(von, nach));
    }

    /**
     * Erstellt eine Gerade durch einen Punkt mit einem Richtungsvektor
     *
     * @param von Punkt, durch den die Gerade laeuft
     * @param v   Richtungsvektor der Geraden
     */
    public Gerade2D(Punkt2D von, Vektor2D v) {
        Punkt2D nach = von.add(v);
        float x1 = von.x;
        float y1 = von.y;
        float x2 = nach.x;
        float y2 = nach.y;
        punkt1 = von;
        punkt2 = nach;
        rv = v.getEinheitsvektor();
        float dx = x2 - x1;
        float dy = y2 - y1;
        a = -dy;
        b = dx;
        c = -(x1 * a + y1 * b);
        nv = new Vektor2D(new Punkt2D(a, b)).getEinheitsvektor();
        if (x2 != x1) {
            m = (y2 - y1) / (x2 - x1);
            n = y1 - x1 * m;
        } else {
            m = Float.NaN;
            n = Float.NaN;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Gerade2D gerade2D = (Gerade2D) o;
        return Float.compare(gerade2D.a, a) == 0 && Float.compare(gerade2D.b, b) == 0 &&
                Float.compare(gerade2D.c, c) == 0 && Float.compare(gerade2D.m, m) == 0 &&
                Float.compare(gerade2D.n, n) == 0 && punkt1.equals(gerade2D.punkt1) && punkt2.equals(gerade2D.punkt2) &&
                rv.equals(gerade2D.rv) && nv.equals(gerade2D.nv);
    }

    /**
     * Ermittelt den Abstand der Geraden zu einem Punkt P
     *
     * @param x, y Koordinaten des Punktes, zu dem der Abstand ermittelt werden soll
     * @return Abstand
     */
    public float getAbstand(float x, float y) {
        float q = a * a + b * b;
        float p = a * x + b * y + c;
        return (float) ((p) / Math.sqrt(q));
    }

    /**
     * Ermittelt den Abstand der Geraden zu einem Punkt P
     *
     * @param pkt Punkt, zu dem der Abstand ermittelt werden soll
     * @return Abstand
     */
    public float getAbstand(Punkt2D pkt) {
        float q = a * a + b * b;
        float p = a * pkt.x + b * pkt.y + c;
        return (float) ((p) / Math.sqrt(q));
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
    public float getAbstandPunkte(Punkt2D p, Punkt2D q) throws IllegalArgumentException {
        if (isPunktAufGerade(p) || isPunktAufGerade(q)) {
            return p.getAbstand(q);
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
        float abstand = getAbstand(p);
        float q1 = p.x + nv.getEndpunkt().x * abstand;
        float q2 = p.y + nv.getEndpunkt().y * abstand;
        Punkt2D q = new Punkt2D(q1, q2);
        if (!isPunktAufGerade(q)) {
            q1 = p.x - nv.getEndpunkt().x * abstand;
            q2 = p.y - nv.getEndpunkt().y * abstand;
            q = new Punkt2D(q1, q2);
        }
        if (!isPunktAufGerade(q)) {
            Log.d("gerwalex", p + " nicht auf Gerade");
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
    public Punkt2D[] getPunkteAufGerade(Punkt2D p, float d) {
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
     * Richtungsvektor der Geraden als Einheitsvektor (Vektorlaenge = 1)
     *
     * @param laenge Länge des Vektors
     * @return liefert den Richtungsvektor als Einheitsvektor zurueck
     */
    public Vektor2D getRichtungsvektor(float laenge) {
        return new Vektor2D(new Punkt2D(rv.x * laenge, rv.y * laenge));
    }

    /**
     * Schnittpunkt zweier Geraden
     *
     * @param g Gerade, mit der ein Schnittpunkt ermittelt werden soll
     * @return Schnittpunkt. Ist null, wenn Geraden parallel laufen
     */
    public Punkt2D getSchnittpunkt(Gerade2D g) {
        // liefert den Schnittpunkt zweier Geraden
        float x, y;
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
     * @return Schnittpunkte der Gerade mit dem Kreis. Gibt es keine Schnittpunkte, wird
     * null zurueckgeliefert. Bei genau einem Schnittpunkt (Tangente) sind beide Punkte identisch
     */
    @Nullable
    public Punkt2D[] getSchnittpunkt(Kreis2D k) {
        /*
         * liefert die Schnittpunkte eines uebergebenen Kreises mit der Gerade
         */
        Punkt2D[] v = null;
        float r = k.getRadius();
        float m1 = k.getMittelpunkt().x;
        float m2 = k.getMittelpunkt().y;
        if ((Math.round(b * 1E6f)) / 1E6f != 0) {
            float s = -b * m2 - c;
            float t = (b * b * m1 + s * a) / (a * a + b * b);
            float z1 = r * r * b * b - b * b * m1 * m1 - s * s;
            float z2 = a * a + b * b;
            float z3 = t * t;
            float z4 = z1 / z2 + z3;
            float zwischenergebnis = Math.round(z4 * 1E6f);
            if (zwischenergebnis >= 0) {
                v = new Punkt2D[2];
                zwischenergebnis = (float) Math.sqrt(zwischenergebnis / 1E6f);
                zwischenergebnis = -zwischenergebnis;
                float x = zwischenergebnis + t;
                v[0] = new Punkt2D(x, (-c - a * x) / b);
                x = -zwischenergebnis + t;
                v[1] = new Punkt2D(x, (-c - a * x) / b);
            }
        } else {
            v = new Punkt2D[2];
            float x = c / a;
            float y = (float) (m2 + Math.sqrt(r * r - Math.pow(c / a - m1, 2)));
            v[0] = new Punkt2D(x, y);
            y = (float) (m2 - Math.sqrt(r * r - Math.pow(c / a - m1, 2)));
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
    public float getYAxisAngle() {
        return rv.getYAxisAngle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, m, n, punkt1, punkt2, rv, nv);
    }

    /**
     * Prueft, ob ein Punkt auf der Geraden liegt
     *
     * @param p Punkt
     * @return true, wenn Punkt auf der Geraden liegt
     */
    public boolean isPunktAufGerade(Punkt2D p) {
        return Math.abs(Math.round(getAbstand(p) * 1E4f)) < 1f;
    }

    /**
     * Rundet eine Double auf 6 Stellen (1E6f)
     *
     * @param d Wert, der gerundet werden soll
     * @return gerundeter Wert
     */
    private float rundeWert(float d) {
        return Math.round(d * 1E6f) / 1E6f;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @NonNull
    @Override
    public String toString() {
        float a = rundeWert(this.a);
        float b = rundeWert(this.b);
        float c = rundeWert(this.c);
        return ("Geradengleichung: " + a + " x  + " + b + " y + " + c + " = 0 Punkt: " + punkt1.toString() +
                "Normalenvektor: " + getNormalenvektor().toString() + ", Richtungsvektor: " + rv.toString() + "m = " +
                rundeWert(m) + ", n = " + rundeWert(n) + "(y= " + rundeWert(m) + " x " + rundeWert(n) + ")");
    }

    /**
     * Verschiebt die Gerade parallel in einen neuen Punkt.
     *
     * @param p Punkt, in den die neue Gerade verschoben werden soll
     * @return neue Gerade, verschoben in den Punkt.Der Punkt ist Startpunkt der Geraden
     */
    public Gerade2D verschiebeParallell(Punkt2D p) {
        return new Gerade2D(p, getRichtungsvektor());
    }
}
