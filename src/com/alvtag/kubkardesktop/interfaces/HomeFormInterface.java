package com.alvtag.kubkardesktop.interfaces;

import com.alvtag.kubkardesktop.models.Heat;

public interface HomeFormInterface {
    void showHomeForm();

    void showHeatForm();

    void startHeat(Heat heat);

    void showRacerOverViewForm();

    void notifyHeatsChanged();
}