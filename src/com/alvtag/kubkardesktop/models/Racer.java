package com.alvtag.kubkardesktop.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Racer implements Comparable<Racer> {
    private static int racersCounter = 0;

    public static void resetRacersCounter() {
        racersCounter = 0;
    }

    private final String name;
    private final int racerId;
    private final List<Integer> raceResultList = new ArrayList<>();

    public Racer(String name) {
        racersCounter++;
        this.racerId = racersCounter;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getRacerId() {
        return racerId;
    }

    public void clearRaceResults() {
        raceResultList.clear();
    }

    public int getAverageRaceTimeMs() {
        if (raceResultList.size() < 1) {
            return 1000000000;
        }
        int raceCounter = 0;
        long totalTimeMs = 0;
        for (; raceCounter < raceResultList.size(); ) {
            totalTimeMs += raceResultList.get(raceCounter);
            raceCounter++;
        }
        double averageTime = ((double) totalTimeMs) / ((double) raceCounter);
        return (int) averageTime;
    }

    public void addRaceTime(int trackTime) {
        raceResultList.add(trackTime);
    }

    @Override
    public int compareTo(Racer o) {
        return Comparator.comparing(Racer::getAverageRaceTimeMs).compare(this, o);
    }

    @Override
    public String toString() {
        //average time readout, in seconds
        int time = getAverageRaceTimeMs();
        return String.format("%d | %s | %d.%ds", racerId, name, (time / 1000), (time % 1000));
    }
}
