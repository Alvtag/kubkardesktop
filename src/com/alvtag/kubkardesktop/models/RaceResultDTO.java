package com.alvtag.kubkardesktop.models;

public class RaceResultDTO {
    public int laneTime0 = -1;
    public int laneTime1 = -1;
    public int laneTime2 = -1;
    public int laneTime3 = -1;
    public int laneTime4 = -1;
    public int laneTime5 = -1;

    public int[] toArray() {
        int[] result = new int[6];
        result[0] = laneTime0;
        result[1] = laneTime1;
        result[2] = laneTime2;
        result[3] = laneTime3;
        result[4] = laneTime4;
        result[5] = laneTime5;
        return result;
    }
}
