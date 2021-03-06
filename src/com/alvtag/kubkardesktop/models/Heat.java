package com.alvtag.kubkardesktop.models;

import java.util.List;

public class Heat {
    public static final int STATE_IDLE = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_RACING = 2;

    private int heatId;
    private List<Racer> racers;
    private int[] raceTimesMS;

    public Heat(List<Racer> racers, int heatId) {
        this.racers = racers;
        this.heatId = heatId;
    }

    public int getHeatId() {
        return heatId;
    }

    public List<Racer> getRacers() {
        return racers;
    }

    public void setRaceTimesMS(int[] raceTimesMS) {
        this.raceTimesMS = raceTimesMS;
    }

    public int[] getRaceTimesMS() {
        return raceTimesMS;
    }

    public int[] getRaceTimesMSWithoutSkips() {
        int[] result = new int[racers.size()];
        int j = 0;
        for (int raceTime : raceTimesMS) {
            if (raceTime != -1) {
                result[j] = raceTime;
                j++;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Heat ");
        sb.append((heatId + 1));//indexed at zero
        sb.append(" :  ");

        for (Racer racer : racers) {
            sb.append(racer.getRacerId());
            sb.append(" | ");
        }
        sb.append(isCompleted() ? "COMPLETED" : "NOT RUN");
        return sb.toString();
    }

    public boolean isCompleted() {
        return raceTimesMS != null;
    }
}
