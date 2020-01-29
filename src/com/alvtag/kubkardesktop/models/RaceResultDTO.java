package com.alvtag.kubkardesktop.models;

public class RaceResultDTO {
    public int l0 = -1;
    public int l1 = -1;
    public int l2 = -1;
    public int l3 = -1;
    public int l4 = -1;
    public int l5 = -1;

    public int[] toArray() {
        int[] result = new int[6];
        result[0] = l0;
        result[1] = l1;
        result[2] = l2;
        result[3] = l3;
        result[4] = l4;
        result[5] = l5;
        return result;
    }
}
