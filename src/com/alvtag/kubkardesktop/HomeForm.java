package com.alvtag.kubkardesktop;

import com.alvtag.kubkardesktop.interfaces.HomeFormInterface;
import com.alvtag.kubkardesktop.models.Heat;
import com.alvtag.kubkardesktop.models.Racer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeForm implements HomeFormInterface {
    //todo: Refactor these statics into some kind of "App" file
    private static JFrame homeFrame;
    private static HomeForm homeForm;

    private static JFrame raceFrame;
    private static RaceOverViewForm raceOverViewForm;

    private static JFrame heatFrame;
    private static HeatForm heatForm;

    private JButton startRaceButton;
    private JPanel rootPanel;
    private JCheckBox lane0CheckBox;
    private JCheckBox lane1CheckBox;
    private JCheckBox lane2CheckBox;
    private JCheckBox lane3CheckBox;
    private JCheckBox lane4CheckBox;
    private JCheckBox lane5CheckBox;
    private JButton loadRacerCsvButton;
    private JList<String> racersJList;
    private JLabel racersCountLabel;
    private List<Racer> racersList;

    public HomeForm() {
        startRaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(((JButton)e.getSource()).getRootPane(), "Hello there.");
                if (racersList == null || racersList.size() < 2) {
                    JOptionPane.showMessageDialog(((JButton) e.getSource()).getRootPane(), "At least 2 racers needed!");
                    return;
                }

                int trackCount = 0;
                if (lane0CheckBox.isSelected()) trackCount++;
                if (lane1CheckBox.isSelected()) trackCount++;
                if (lane2CheckBox.isSelected()) trackCount++;
                if (lane3CheckBox.isSelected()) trackCount++;
                if (lane4CheckBox.isSelected()) trackCount++;
                if (lane5CheckBox.isSelected()) trackCount++;

                if (trackCount == 0 || trackCount == 1) {
                    JOptionPane.showMessageDialog(((JButton) e.getSource()).getRootPane(),
                            "Select at least two tracks!");
                    return;
                } else if (trackCount > racersList.size()) {
                    JOptionPane.showMessageDialog(((JButton) e.getSource()).getRootPane(),
                            "You've selected more tracks than we have racers! Please de-select some tracks.");
                    return;
                }

                raceOverViewForm.setRaceParams(
                        lane0CheckBox.isSelected(),
                        lane1CheckBox.isSelected(),
                        lane2CheckBox.isSelected(),
                        lane3CheckBox.isSelected(),
                        lane4CheckBox.isSelected(),
                        lane5CheckBox.isSelected(),
                        trackCount,
                        racersList
                );

                showRacerOverViewForm();
            }
        });
        loadRacerCsvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    readRacersList(selectedFile);
                    setRacersList();
                }
            }
        });
    }

    @Override
    public void showHomeForm() {
        homeFrame.setVisible(true);
        raceFrame.setVisible(false);
        heatFrame.setVisible(false);
    }

    @Override
    public void showHeatForm() {
        homeFrame.setVisible(false);
        raceFrame.setVisible(false);
        heatFrame.setVisible(true);
    }

    @Override
    public void startHeat(Heat heat) {
        heatForm.startHeat(heat);
        heatFrame.setTitle("Heat " + (heat.getHeatId() + 1)); //heats indexed at zero
    }

    @Override
    public void showRacerOverViewForm() {
        homeFrame.setVisible(false);
        raceFrame.setVisible(true);
        heatFrame.setVisible(false);
    }

    @Override
    public void notifyHeatsChanged() {
        raceOverViewForm.notifyHeatsChanged();
    }

    @Override
    public HashMap<Integer, Boolean> getActiveTracks() {
        HashMap<Integer, Boolean> result = new HashMap<>();
        result.put(0, lane0CheckBox.isSelected());
        result.put(1, lane1CheckBox.isSelected());
        result.put(2, lane2CheckBox.isSelected());
        result.put(3, lane3CheckBox.isSelected());
        result.put(4, lane4CheckBox.isSelected());
        result.put(5, lane5CheckBox.isSelected());
        return result;
    }

    private void setRacersList() {
        Object[] src = racersList.toArray();
        String[] dest = new String[src.length];
        for (int i = 0; i < src.length; i++) {
            dest[i] = ((Racer) src[i]).getName();
        }

        racersJList.setListData(dest);
        racersJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        racersJList.setLayoutOrientation(JList.VERTICAL);
        racersCountLabel.setText(String.format("%d racers", racersList.size()));
    }

    private void readRacersList(File selectedFile) {
        BufferedReader reader;
        racersList = new ArrayList<>();
        try {
            Racer.resetRacersCounter();
            reader = new BufferedReader(new FileReader(selectedFile));
            String line = reader.readLine();
            while (line != null) {
                racersList.add(new Racer(line));
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        homeFrame = new JFrame("KubKarDesktop");
        homeForm = new HomeForm();
        homeFrame.setContentPane(homeForm.rootPanel);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.pack();
        homeFrame.setVisible(true);

        raceFrame = new JFrame("Race Overview");
        raceOverViewForm = new RaceOverViewForm(homeForm);
        raceFrame.setContentPane(raceOverViewForm.getRootPanel());
        raceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        raceFrame.pack();
        raceFrame.setVisible(false);

        heatFrame = new JFrame("Heat");
        heatForm = new HeatForm(homeForm);
        heatFrame.setContentPane(heatForm.getRootPanel());
        heatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        heatFrame.pack();
        heatFrame.setVisible(false);
    }

}
