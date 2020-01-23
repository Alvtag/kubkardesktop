package com.alvtag.kubkardesktop.models;

import java.util.ArrayList;
import java.util.List;

public class Racer {
    private static int racersCounter = 0;

    public static void resetRacersCounter() {
        racersCounter = 0;
    }

    private final String name;
    private final int racerId;
    private final List<RaceResult> raceResultList;

    public Racer(String name) {
        racersCounter++;
        this.racerId = racersCounter;

        this.name = name;

        raceResultList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getRacerId() {
        return racerId;
    }

    public void addRaceResult(){

    }

    public int getAverageRaceTimeMs() {
        if (raceResultList == null || raceResultList.size() <= 1) {
            return 0;
        }
        int raceCounter = 0;
        long totalTimeMs = 0;
        for (; raceCounter < raceResultList.size(); ) {
            totalTimeMs += raceResultList.get(raceCounter).getTimeForRacerMs(racerId);
            raceCounter++;
        }
        double averageTime = ((double) totalTimeMs) / ((double) raceCounter);
        return (int) averageTime;
    }
}
