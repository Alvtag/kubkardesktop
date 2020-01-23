package com.alvtag.kubkardesktop;

import com.alvtag.kubkardesktop.interfaces.HomeFormInterface;
import com.alvtag.kubkardesktop.lookups.PPN;
import com.alvtag.kubkardesktop.models.Heat;
import com.alvtag.kubkardesktop.models.Racer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RaceOverViewForm {

    private JPanel rootPanel;
    private JList<Heat> heatsJList;
    private JButton backButton;
    private JButton printButton;
    private JSpinner printCopiesSpinner;
    private JButton startHeatButton;
    private List<Racer> racerList;
    private Heat[] heatsArray;

    public Heat[] getHeatsArray() {
        return heatsArray;
    }

    public RaceOverViewForm(HomeFormInterface homeFormInterface) {
        setButtonListeners(homeFormInterface);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void setRaceParams(boolean isLane0Active, boolean isLane1Active, boolean isLane2Active,
                              boolean isLane3Active, boolean isLane4Active, boolean isLane5Active,
                              int trackCount, List<Racer> racerList) {
        this.racerList = racerList;
        heatsArray = getHeatsArray(trackCount, racerList);
        heatsJList.setListData(heatsArray);
        heatsJList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
    }

    private void setButtonListeners(HomeFormInterface homeFormInterface) {
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
    }

    private static Heat[] getHeatsArray(int trackCount, List<Racer> racerList) {
        int[][] rawHeatData = null;
        switch (trackCount) {
            case 3:
                rawHeatData = PPN.LANE_3_CAR_3;
                break;
            case 4:
                rawHeatData = PPN.LANE_4_CAR_4;
                break;
            case 6:
                switch (racerList.size()) {
                    case 6:
                        rawHeatData = PPN.LANE_6_CAR_6;
                        break;
                    case 7:
                        rawHeatData = PPN.LANE_6_CAR_7;
                        break;
                    case 8:
                        rawHeatData = PPN.LANE_6_CAR_8;
                        break;
                    case 9:
                        rawHeatData = PPN.LANE_6_CAR_9;
                        break;
                    case 10:
                        rawHeatData = PPN.LANE_6_CAR_10;
                        break;
                    case 11:
                        rawHeatData = PPN.LANE_6_CAR_11;
                        break;
                    case 12:
                        rawHeatData = PPN.LANE_6_CAR_12;
                        break;
                    case 13:
                        rawHeatData = PPN.LANE_6_CAR_13;
                        break;
                    case 14:
                        rawHeatData = PPN.LANE_6_CAR_14;
                        break;
                    case 15:
                        rawHeatData = PPN.LANE_6_CAR_15;
                        break;
                    case 16:
                        rawHeatData = PPN.LANE_6_CAR_16;
                        break;
                    case 17:
                        rawHeatData = PPN.LANE_6_CAR_17;
                        break;
                    case 18:
                        rawHeatData = PPN.LANE_6_CAR_18;
                        break;
                    case 19:
                        rawHeatData = PPN.LANE_6_CAR_19;
                        break;
                    case 20:
                        rawHeatData = PPN.LANE_6_CAR_20;
                        break;
                    case 21:
                        rawHeatData = PPN.LANE_6_CAR_21;
                        break;
                    case 22:
                        rawHeatData = PPN.LANE_6_CAR_22;
                        break;
                    case 23:
                        rawHeatData = PPN.LANE_6_CAR_23;
                        break;
                    case 24:
                        rawHeatData = PPN.LANE_6_CAR_24;
                        break;
                    case 25:
                        rawHeatData = PPN.LANE_6_CAR_25;
                        break;
                }
                break;
        }
        if (rawHeatData == null) {
            throw new IllegalStateException("no heats data found for " + trackCount + " lanes and " + racerList.size() + " cars");
        }
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
    }
}
