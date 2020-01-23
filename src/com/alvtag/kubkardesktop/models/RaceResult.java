package com.alvtag.kubkardesktop.models;

public class RaceResult {
    private static int heatsCounter = 0;

    private final int heatId;
    private final int[] racerIds;
    private int[] raceTimesMs;

    public static void resetRacersCounter() {
        heatsCounter = 0;
    }

    public RaceResult(int[] racers) {
        heatsCounter++;
        this.heatId = heatsCounter;

        this.racerIds = racers;
        raceTimesMs = new int[racerIds.length];
    }

    public int[] getRaceTimesMs() {
        return raceTimesMs;
    }

    public void setRacerResults(int[] raceTimesMS) {
        if (racerIds.length != raceTimesMS.length) {
            System.out.println("~~~ DUMP ~~~");
            for (int i = 0; i < raceTimesMS.length; i++) {
                System.out.println("" + i + ":" + raceTimesMS);
            }
            throw new IllegalArgumentException("heat " + heatId + " has " + racerIds.length + " but " +
                    raceTimesMS.length + " results were reported!");
        }
        this.raceTimesMs = raceTimesMS;

    }

    /**
     * @return 0 if race has not been finished
     * -1 if racerId is not found in this heat
     */
    public int getTimeForRacerMs(int racerId) {

        for (int i = 0; i < racerIds.length; i++) {
            if (racerIds[i] == racerId) {
                return raceTimesMs[i];
            }
        }
        return -1;
    }
}
