package com.alvtag.kubkardesktop;

import com.alvtag.kubkardesktop.interfaces.HomeFormInterface;
import com.alvtag.kubkardesktop.lookups.PPN;
import com.alvtag.kubkardesktop.models.Heat;
import com.alvtag.kubkardesktop.models.Racer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RaceOverViewForm {

    private JPanel rootPanel;
    private JList<Heat> heatsJList;
    private JButton backButton;
    private JButton printHeatsButton;
    private JSpinner printHeatCopiesSpinner;
    private JSpinner printAverageTimesCopiesSpinner;
    private JButton startHeatButton;
    private JList<Racer> averageTimesJList;
    private JButton printAverageTimesButton;
    private List<Racer> racerList;
    private Heat[] heatsArray;

    public Heat[] getHeatsArray() {
        return heatsArray;
    }

    public RaceOverViewForm(HomeFormInterface homeFormInterface) {
        setWidgetListeners(homeFormInterface);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void setRaceParams(int trackCount, List<Racer> racerList) {
        this.racerList = racerList;
        heatsArray = getHeatsArray(trackCount, racerList);
        heatsJList.setListData(heatsArray);
        heatsJList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
    }

    private void setWidgetListeners(HomeFormInterface homeFormInterface) {
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                homeFormInterface.showHomeForm();
            }
        });
        startHeatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = heatsJList.getSelectedIndex();
                if (selection < 0 || selection >= racerList.size()) {
                    JOptionPane.showMessageDialog(((JButton) e.getSource()).getRootPane(), "Invalid heat choice!");
                    return;
                }

                homeFormInterface.startHeat(heatsArray[selection]);
                homeFormInterface.showHeatForm();
            }
        });
        printHeatCopiesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //todo alvtag set min to one
            }
        });
        printAverageTimesCopiesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //todo alvtag set min to one
            }
        });
        printHeatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO print X copies as JSpinner printCopiesSpinner determines
            }
        });
    }

    private static Heat[] getHeatsArray(int trackCount, List<Racer> racerList) {
        int[][] rawHeatData = PPN.getHeatsArray(trackCount, racerList);

        // Convert rawHeatData into an array of Heats
        Heat[] heats = new Heat[rawHeatData.length];
        for (int i = 0; i < rawHeatData.length; i++) {
            //for each heat

            List<Racer> racers = new ArrayList<>();
            for (int j = 0; j < rawHeatData[i].length; j++) {
                //each racer
                int racerId = rawHeatData[i][j];
                racers.add(getRacerFromList(racerId, racerList));
            }
            heats[i] = new Heat(racers, i);
        }
        return heats;
    }

    private static Racer getRacerFromList(int racerId, List<Racer> racerList) {
        for (Racer racer : racerList) {
            if (racerId == racer.getRacerId()) {
                return racer;
            }
        }
        throw new IllegalStateException("no racer data found for " + racerId + "!!!");
    }

    public void notifyHeatsChanged() {
        heatsJList.setListData(heatsArray);
        heatsJList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        setAverageTimes();
    }

    private void setAverageTimes() {
        for (Racer racer : racerList) {
            racer.clearRaceResults();
        }

        for (Heat heat : heatsArray) {
            if (!heat.isCompleted()) {
                continue;
            }
            List<Racer> racers = heat.getRacers();
            int racerIndex = 0;
            for (int singleTrackRaceTime : heat.getRaceTimesMS()) {
                if (singleTrackRaceTime < 1) {
                    continue;
                }
                if (racerIndex >= racers.size()) {
                    return;
                }
                racers.get(racerIndex).addRaceTime(singleTrackRaceTime);
                racerIndex++;
            }
        }
        List<Racer> racersToSort = new ArrayList<>();
        racersToSort.addAll(racerList);
        Collections.sort(racersToSort);
        Racer[] sortedRacers = new Racer[racersToSort.size()];
        for (int i = 0; i < racersToSort.size(); i++) {
            sortedRacers[i] = racersToSort.get(i);
        }

        averageTimesJList.setListData(sortedRacers);
        averageTimesJList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
    }
}
