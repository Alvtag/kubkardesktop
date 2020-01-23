package com.alvtag.kubkardesktop.models;

public class TrackStatusDTO {
    public int gate;
    public int gate0;
    public int gate1;
    public int gate2;
    public int gate3;
    public int gate4;
    public int gate5;
    public int raceState;

    @Override
    public String toString() {
        return "TrackStatus{" +
                "gate=" + gate +
                ", gate0=" + gate0 +
                ", gate1=" + gate1 +
                ", gate2=" + gate2 +
                ", gate3=" + gate3 +
                ", gate4=" + gate4 +
                ", gate5=" + gate5 +
                ", raceState=" + raceState +
                '}';
    }
}
