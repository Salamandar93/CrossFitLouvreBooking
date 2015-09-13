package com.danielan.crossfitlouvrebooking.holder;

/**
 * Created by Daniel AN on 13/09/2015.
 */
public class Creneau implements Comparable<Creneau> {
    private String mCrenoId;
    private String mCrenoDebut;
    private String mCrenoFin;

    public Creneau(String crenoDebut, String crenoFin) {
        this.mCrenoDebut = crenoDebut;
        this.mCrenoFin = crenoFin;
    }

    public String toString() {
        return mCrenoDebut + " - " + mCrenoFin;
    }

    public String getCrenoId() {
        return mCrenoId;
    }

    public void setCrenoId(String mCrenoId) {
        this.mCrenoId = mCrenoId;
    }

    public String getCrenoDebut() {
        return mCrenoDebut;
    }

    public void setCrenoDebut(String mCrenoDebut) {
        this.mCrenoDebut = mCrenoDebut;
    }

    public String getCrenoFin() {
        return mCrenoFin;
    }

    public void setCrenoFin(String mCrenoFin) {
        this.mCrenoFin = mCrenoFin;
    }

    @Override
    public int compareTo(Creneau another) {
        return this.mCrenoDebut.compareTo(another.getCrenoDebut());
    }
}
