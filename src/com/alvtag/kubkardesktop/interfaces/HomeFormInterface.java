package com.alvtag.kubkardesktop.interfaces;

import com.alvtag.kubkardesktop.models.Heat;

import java.util.HashMap;

public interface HomeFormInterface {
    void showHomeForm();

    void showHeatForm();

    void startHeat(Heat heat);

    void showRacerOverViewForm();

    void notifyHeatsChanged();

    HashMap<Integer, Boolean> getActiveTracks();
}