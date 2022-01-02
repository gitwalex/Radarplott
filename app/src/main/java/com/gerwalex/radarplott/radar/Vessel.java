package com.gerwalex.radarplott.radar;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

import java.util.Objects;

public class Vessel extends BaseObservable {
    protected float heading;
    protected Gerade2D kurslinie;
    protected Character name;
    protected float speed;
    protected Punkt2D startPosition, aktPosition;

    protected Vessel() {
        startPosition = aktPosition = new Punkt2D();
    }

    public Vessel(int heading, float speed) {
        name = null; // Kein Name fuer eigenes Schiff
        this.heading = heading % 360;
        this.speed = speed;
        aktPosition = new Punkt2D(0, 0);
        startPosition = aktPosition.getPunkt2D(this.heading, -(speed / 6f));
        kurslinie = new Gerade2D(startPosition, aktPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vessel vessel = (Vessel) o;
        return Objects.equals(name, vessel.name) && startPosition.equals(vessel.startPosition) &&
                aktPosition.equals(vessel.aktPosition) && vessel.heading == heading && vessel.speed == speed;
    }

    public final Punkt2D getAktPosition() {
        return aktPosition;
    }

    public final void setAktPosition(Punkt2D aktPosition) {
        this.aktPosition = aktPosition;
        kurslinie = new Gerade2D(startPosition, aktPosition);
    }

    /**
     * Heading
     *
     * @return heading
     */
    @Bindable
    public final float getHeading() {
        return heading;
    }

    @Bindable
    public final void setHeading(float heading) {
        if (this.heading != heading) {
            this.heading = heading;
            startPosition = aktPosition.getPunkt2D(this.heading % 360, -(speed / 6f));
            kurslinie = new Gerade2D(startPosition, aktPosition);
            notifyChange();
        }
    }

    public int getHeadingFormatted() {
        return Math.round((heading + 0.5f) * 10) / 10;
    }

    public final Gerade2D getKurslinie() {
        return kurslinie;
    }

    public final Punkt2D getPosition(int minutes) {
        return aktPosition.getPunkt2D(heading, (float) (speed * minutes / 60.0));
    }

    /**
     * Speed
     *
     * @return speed
     */
    @Bindable
    public final float getSpeed() {
        return speed;
    }

    @Bindable
    public final void setSpeed(float speed) {
        if (this.speed != speed) {
            this.speed = speed;
            startPosition = aktPosition.getPunkt2D(this.heading, -(speed / 6f));
            kurslinie = new Gerade2D(startPosition, aktPosition);
            notifyChange();
        }
    }

    public final Punkt2D getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Punkt2D startPosition) {
        this.startPosition = startPosition;
        kurslinie = new Gerade2D(startPosition, aktPosition);
    }

    public final float getTimeTo(@NonNull Punkt2D p) {
        if (!kurslinie.isPunktAufGerade(p)) {
            throw new IllegalArgumentException("Punkt nicht auf Kurslinie");
        }
        float timeToP = (float) (aktPosition.getAbstand(p) / speed * 60.0);
        return isPunktInFahrtrichtung(p) ? timeToP : -timeToP;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, heading, kurslinie, kurslinie, speed, startPosition, aktPosition);
    }

    /**
     * Prueft, ob ein Punkt auf der EigenesSchiff liegt. Toleranz ist 1E6.
     *
     * @param p Punkt
     * @return true, wenn der Punkt auf der EigenesSchiff liegt, ansonsten false.
     */
    public final boolean isPunktAufKurslinie(@NonNull Punkt2D p) {
        return Math.round(kurslinie.getAbstand(p.x, p.y) * 1E6) == 0;
    }

    /**
     * Prueft, ob ein Punkt in Fahrtrichtung liegt.
     *
     * @param p zu pruefender Punkt
     * @return true, wenn der Punkt in Fahrtrichtung liegt. Sonst false.
     */
    public final boolean isPunktInFahrtrichtung(Punkt2D p) {
        if (!isPunktAufKurslinie(p)) {
            return false;
        }
        double wegx = kurslinie.getRichtungsvektor().getEndpunkt().x;
        double wegy = kurslinie.getRichtungsvektor().getEndpunkt().y;
        double lambda;
        if (wegx != 0) {
            lambda = (p.x - aktPosition.x) / wegx;
        } else {
            lambda = (p.y - aktPosition.y) / wegy;
        }
        return lambda > 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Vessel{" + "name=" + name + ", heading=" + heading + " speed=" + speed + ",startPosition=" +
                startPosition + ", aktPosition=" + aktPosition + ", kurslinie=" + kurslinie + "}";
    }
}


