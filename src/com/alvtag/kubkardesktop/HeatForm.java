package com.alvtag.kubkardesktop;

import com.alvtag.kubkardesktop.arduino.Arduino;
import com.alvtag.kubkardesktop.interfaces.HomeFormInterface;
import com.alvtag.kubkardesktop.models.Heat;
import com.alvtag.kubkardesktop.models.RaceResult;
import com.alvtag.kubkardesktop.models.RaceResultDTO;
import com.alvtag.kubkardesktop.models.TrackStatusDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class HeatForm {

    private static final int BAUD_RATE = 9600;
    private static final Color COLOR_GREEN = new Color(0, 162, 7);
    private static final Color COLOR_RED = new Color(162, 0, 8);
    private static final Color COLOR_BLACK = new Color(16, 16, 16);
    private static final Color COLOR_PURPLE = new Color(137, 0, 170);

    // FACTOR/TIME = SCALED-SPEED
    // FACTOR's unit is Km-Seconds per Hour
    // divide factor by the car's racing time (in seconds) to obtain speed at 25x scale
    private static final double TRACK_50_SCALE_25_FACTOR = 1371.6D;
    private static final double TRACK_40_SCALE_25_FACTOR = 1097.28D;

    /**
     * Remember that arduino is single core and that any TX data sent to the board will disrupt & delay operations!
     */
    private static final int HERTZ_10_DELAY = 100;
    private static final String WINDOWS_ARDUINO_PORT = "COM3";
    private static final String MAC_ARDUINO_PORT = "/dev/cu.usbmodem1411";
    private static final String ARDUINO_PORT = MAC_ARDUINO_PORT;

    private JLabel heatTitleLabel;

    private JButton cancelButton;
    private JButton submitButton;
    private JButton startRaceButton;
    private JButton resetRaceButton;

    private JPanel startGateState;
    private JPanel lane0EndGateState;
    private JPanel lane1EndGateState;
    private JPanel lane2EndGateState;
    private JPanel lane3EndGateState;
    private JPanel lane4EndGateState;
    private JPanel lane5EndGateState;
    private JLabel lane0RacerIdJLabel;
    private JLabel lane1RacerIdJLabel;
    private JLabel lane2RacerIdJLabel;
    private JLabel lane3RacerIdJLabel;
    private JLabel lane4RacerIdJLabel;
    private JLabel lane5RacerIdJLabel;
    private JLabel lane0HeatTime;
    private JLabel lane1HeatTime;
    private JLabel lane2HeatTime;
    private JLabel lane3HeatTime;
    private JLabel lane4HeatTime;
    private JLabel lane5HeatTime;
    private JLabel lane0ScaledSpeed;
    private JLabel lane1ScaledSpeed;
    private JLabel lane2ScaledSpeed;
    private JLabel lane3ScaledSpeed;
    private JLabel lane4ScaledSpeed;
    private JLabel lane5ScaledSpeed;
    private JPanel stateIdlePanel;
    private JPanel stateReadyPanel;
    private JPanel stateRacingPanel;
    private JPanel rootPanel;

    private JLabel[] laneRacerIdJLabelList;
    private JPanel[] laneEndGateStatePanelList;
    private JLabel[] laneHeatTimeJLabelList;
    private JLabel[] laneScaledSpeedLabelList;

    private HomeFormInterface homeFormInterface;

    private Arduino arduino = null;
    private String arduinoRxDataBuffer = "";
    private int heatState;
    private Heat heat;
    private RaceResult raceResult;

    long runnableCounter = -1;

    private TimerTask arduinoCommRunnable = new TimerTask() {
        @Override
        public void run() {
            runnableCounter++;
            // Don't request status update during RACING state since it interrupts detection
            if (runnableCounter % 2 == 0 && heatState != Heat.STATE_RACING) {
                //System.out.println("requestStatusTimerTask start");
                arduino.serialWrite("$$STATUS%%");
                return;
            }

//            System.out.println("arduinoCommRunnable start");
            String newData = arduino.serialRead().trim();
            arduinoRxDataBuffer = arduinoRxDataBuffer.concat(newData);
            processRxDataBuffer();
        }
    };

    private void processRxDataBuffer() {
        int indexBeginOfFirstCommand = arduinoRxDataBuffer.indexOf("$$");
        int indexEndOfFirstCommand = arduinoRxDataBuffer.indexOf("%%");
        if (indexBeginOfFirstCommand != -1 && indexEndOfFirstCommand != -1 && indexBeginOfFirstCommand < indexEndOfFirstCommand) {
            //read a command and set the remainder to buffer. (any data before the firs '$$' will be dropped.)
            String jsonString = arduinoRxDataBuffer.substring(indexBeginOfFirstCommand + 2, indexEndOfFirstCommand);
            arduinoRxDataBuffer = arduinoRxDataBuffer.substring(indexEndOfFirstCommand + 2);

            System.out.println("processRxDataBuffer command from Arduino:" + jsonString);
            if (jsonString.contains("STATUS")) {
                jsonString = jsonString.replace("STATUS", "");
                Gson gson = new GsonBuilder().setLenient().create();
                TrackStatusDTO status = gson.fromJson(jsonString, TrackStatusDTO.class);
                setTrackStatus(status);
                //System.out.println("Status Read:" + status);
            } else if (jsonString.contains("END_RACE")) {
                jsonString = jsonString.replace("END_RACE", "");
                Gson gson = new Gson();
                RaceResultDTO resultDTO = gson.fromJson(jsonString, RaceResultDTO.class);

                int[] racers = new int[heat.getRacers().size()];
                for (int i = 0; i < racers.length; i++) {
                    racers[i] = heat.getRacers().get(i).getRacerId();
                }

                int[] trackTimes = resultDTO.toArray(); // array size always 6
                int[] raceTimes = new int[racers.length];

                //e.g. racers 1,2,3,4 on tracks X, 1, X, 2, 3, 4
                int racerCounter = 0;
                int trackCounter = 0;
                while (trackCounter < trackTimes.length) {
                    if (trackTimes[trackCounter] < 0) {
                        trackCounter++;
                        continue;
                    }
                    raceTimes[racerCounter] = trackTimes[trackCounter];
                    racerCounter++;
                    trackCounter++;
                }

                RaceResult raceResult = new RaceResult(racers);
                raceResult.setRacerResults(raceTimes);

                this.raceResult = raceResult;
                setRaceResult(raceTimes);
                heatState = Heat.STATE_IDLE;
            }
        }
    }

    private void setRaceResult(int[] raceTimes) {
        for (int i = 0; i < raceTimes.length; i++) {
            int time = raceTimes[i];
            if (time > 0) {
                laneHeatTimeJLabelList[i].setText(String.format("%d.%d s", (time / 1000), (time % 1000)));
                double timeInSeconds = ((double) time) / 1000D;
                double scaledSpeed = TRACK_40_SCALE_25_FACTOR / timeInSeconds;
                System.out.println("Racer " + i + " | time:" + time + " s | Scaled speed:" + scaledSpeed + " km/h");
                laneScaledSpeedLabelList[i].setText(String.format("%.2f km/h", scaledSpeed));
            }
        }
    }

    private void setTrackStatus(TrackStatusDTO status) {
        startGateState.setBackground(status.gate == 0 ? COLOR_BLACK : COLOR_GREEN);
        laneEndGateStatePanelList[0].setBackground(status.gate0 == 0 ? COLOR_BLACK : COLOR_GREEN);
        laneEndGateStatePanelList[1].setBackground(status.gate1 == 0 ? COLOR_BLACK : COLOR_GREEN);
        laneEndGateStatePanelList[2].setBackground(status.gate2 == 0 ? COLOR_BLACK : COLOR_GREEN);
        laneEndGateStatePanelList[3].setBackground(status.gate3 == 0 ? COLOR_BLACK : COLOR_GREEN);
        laneEndGateStatePanelList[4].setBackground(status.gate4 == 0 ? COLOR_BLACK : COLOR_GREEN);
        laneEndGateStatePanelList[5].setBackground(status.gate5 == 0 ? COLOR_BLACK : COLOR_GREEN);
        heatState = status.raceState;
        switch (heatState) {
            case Heat.STATE_IDLE:
                setHeatStateIdle();
                break;
            case Heat.STATE_READY:
                setHeatStateReady();
                break;
            case Heat.STATE_RACING:
                setHeatStateRacing();
                break;
        }
    }

    public HeatForm(HomeFormInterface homeFormInterface) {
        this.homeFormInterface = homeFormInterface;

        laneRacerIdJLabelList = new JLabel[]{
                lane0RacerIdJLabel, lane1RacerIdJLabel, lane2RacerIdJLabel,
                lane3RacerIdJLabel, lane4RacerIdJLabel, lane5RacerIdJLabel};
        laneEndGateStatePanelList = new JPanel[]{
                lane0EndGateState,
                lane1EndGateState,
                lane2EndGateState,
                lane3EndGateState,
                lane4EndGateState,
                lane5EndGateState
        };
        laneHeatTimeJLabelList = new JLabel[]{
                lane0HeatTime,
                lane1HeatTime,
                lane2HeatTime,
                lane3HeatTime,
                lane4HeatTime,
                lane5HeatTime
        };
        laneScaledSpeedLabelList = new JLabel[]{
                lane0ScaledSpeed,
                lane1ScaledSpeed,
                lane2ScaledSpeed,
                lane3ScaledSpeed,
                lane4ScaledSpeed,
                lane5ScaledSpeed
        };

        setButtonListeners(homeFormInterface);
        initiateArduino();
    }

    private void initiateArduino() {
        if (arduino == null) {
            arduino = new Arduino(ARDUINO_PORT, BAUD_RATE);
            boolean success = arduino.openConnection();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Arduino connect success:" + success);
            if (!success) {
                JOptionPane.showMessageDialog(rootPanel, "Arduino connection failed on " + ARDUINO_PORT + ". " +
                        "Check:1) Serial Monitor locking port. 2)Arduino Connected. 3) PC platform. " +
                        "Then restart the application.");
            } else {
                // kick off two tasks; one to read the buffer for data arrived from arduino;
                // one to request status updates from arduino
                System.out.println("Arduino connect success. kick off timers");

                //TODO: vary the speed of STATUS requests based on state
                Timer timer = new Timer();
                timer.schedule(arduinoCommRunnable, HERTZ_10_DELAY, HERTZ_10_DELAY);


            }
        }
    }

    public void closeArduinoConnection() {
        arduino.closeConnection();
    }

    private void setButtonListeners(HomeFormInterface homeFormInterface) {

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (raceResult == null) {
                    return;
                }
                heat.setRaceTimesMS(raceResult.getRaceTimesMs());
                homeFormInterface.notifyHeatsChanged();
                homeFormInterface.showRacerOverViewForm();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                homeFormInterface.showRacerOverViewForm();
            }
        });
        startRaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (heatState == Heat.STATE_IDLE) {
                    StringBuilder sb = new StringBuilder("$$BEGIN_RACE|");
                    HashMap<Integer, Boolean> hashMap = homeFormInterface.getActiveTracks();
                    for (int i = 0; i < 6; i++) {
                        if (hashMap.get(i)) {
                            sb.append(i);
                        }
                    }
                    sb.append("%%");
                    arduino.serialWrite(sb.toString());
                }
            }
        });
        resetRaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                heatState = Heat.STATE_IDLE;
                raceResult = null;
                for (int i = 0; i < 6; i++) {
                    laneScaledSpeedLabelList[i].setText("");
                    laneHeatTimeJLabelList[i].setText("");
                }
            }
        });
    }

    private void setHeatStateIdle() {
        stateIdlePanel.setBackground(COLOR_GREEN);
        stateReadyPanel.setBackground(COLOR_PURPLE);
        stateRacingPanel.setBackground(COLOR_PURPLE);
    }

    private void setHeatStateReady() {
        stateIdlePanel.setBackground(COLOR_PURPLE);
        stateReadyPanel.setBackground(COLOR_GREEN);
        stateRacingPanel.setBackground(COLOR_PURPLE);
    }

    private void setHeatStateRacing() {
        stateIdlePanel.setBackground(COLOR_PURPLE);
        stateReadyPanel.setBackground(COLOR_PURPLE);
        stateRacingPanel.setBackground(COLOR_GREEN);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void startHeat(Heat heat) {
        heatTitleLabel.setText("Heat " + (heat.getHeatId() + 1));
        this.heat = heat;
        for (int i = 0; i < 6; i++) {
            laneScaledSpeedLabelList[i].setText("");
            laneHeatTimeJLabelList[i].setText("");
        }

        HashMap<Integer, Boolean> trackActivesHashMap = homeFormInterface.getActiveTracks();
        for (int trackIndex = 0, racerIndex = 0; trackIndex < 6; trackIndex++) {
            boolean isTrackActive = trackActivesHashMap.get(trackIndex);
            if (isTrackActive) {
                laneRacerIdJLabelList[trackIndex].setText(String.valueOf(heat.getRacers().get(racerIndex).getRacerId()));
                racerIndex++;
            } else {
                laneRacerIdJLabelList[trackIndex].setText("");
            }
        }

        if (heat.getRaceTimesMS() != null) {
            setRaceResult(heat.getRaceTimesMS());
        }
    }
}
