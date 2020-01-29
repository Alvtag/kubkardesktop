package com.alvtag.kubkardesktop.models;

public class RaceResult {
    private final int heatId;
    private final int[] racerIds;
    private int[] raceTimesMs;

    public RaceResult(int[] racers, int[] raceTimesMS, int heatId) {
        this.heatId = heatId;
        this.racerIds = racers;
        raceTimesMs = new int[racerIds.length];

        this.raceTimesMs = raceTimesMS;
    }

    public int[] getRaceTimesMs() {
        return raceTimesMs;
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
