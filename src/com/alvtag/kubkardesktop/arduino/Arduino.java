package com.alvtag.kubkardesktop.arduino;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;


public class Arduino {
    private SerialPort comPort;
    private String portDescription;
    private int baud_rate;

    public Arduino() {
        //empty constructor if port undecided
    }

    public Arduino(String portDescription) {
        //make sure to set baud rate after
        this.portDescription = portDescription;
        comPort = SerialPort.getCommPort(this.portDescription);
    }

    public Arduino(String portDescription, int baud_rate) {
        //preferred constructor
        this.portDescription = portDescription;
        comPort = SerialPort.getCommPort(this.portDescription);
        this.baud_rate = baud_rate;
        comPort.setBaudRate(this.baud_rate);
    }


    public boolean openConnection() {
        if (comPort.openPort()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
//            AlertBox alert = new AlertBox(new Dimension(400, 100), "Error Connecting", "Try Another port");
//            alert.display();
            return false;
        }
    }

    public void closeConnection() {
        comPort.closePort();
    }

    public void setPortDescription(String portDescription) {
        this.portDescription = portDescription;
        comPort = SerialPort.getCommPort(this.portDescription);
    }

    public void setBaudRate(int baud_rate) {
        this.baud_rate = baud_rate;
        comPort.setBaudRate(this.baud_rate);
    }

    public String getPortDescription() {
        return portDescription;
    }

    public SerialPort getSerialPort() {
        return comPort;
    }


    public String serialRead() {
        if (comPort == null) {
            return null;
        }
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        InputStream is = comPort.getInputStream();
        String out = "";

        byte[] data = new byte[128];

        // SAMPLE bad processRxDataBuffer command from Arduino:
        //   RACE_END{"laneTime0":4419,"laneTime1":4881,+��Q����":5256,
        //   "laneTime3":5541,"laneTime4":5830,"laneTime5":6270}
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            if (is.available() > 0) {
                int bytesRead = is.read(data);
                buffer.write(data, 0, bytesRead);
                System.in.skip(bytesRead);
            }
        } catch (IOException e) {
            if (!e.getMessage().contains("Illegal seek")) {
                e.printStackTrace();
            }
        }

        return new String(buffer.toByteArray());
    }

    public void serialWrite(String s) {
        if (comPort == null) {
            return;
        }
        //writes the entire string at once.
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        try {
            Thread.sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter pout = new PrintWriter(comPort.getOutputStream());
        pout.print(s);
        pout.flush();

    }

    public void serialWrite(String s, int noOfChars, int delay) {
        //writes the entire string at once.
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        try {
            Thread.sleep(5);
        } catch (Exception e) {
        }
        PrintWriter pout = new PrintWriter(comPort.getOutputStream());
        for (int i = 0; i < s.length(); i += noOfChars) {
            pout.write(s.substring(i, i + noOfChars));
            pout.flush();
            System.out.println(s.substring(i, i + noOfChars));
            try {
                Thread.sleep(delay);
            } catch (Exception e) {
            }
        }
        pout.write(noOfChars);
        pout.flush();

    }

    public void serialWrite(char c) {
        //writes the entire string at once.
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        try {
            Thread.sleep(5);
        } catch (Exception e) {
        }
        PrintWriter pout = new PrintWriter(comPort.getOutputStream());
        pout.write(c);
        pout.flush();
    }

    public void serialWrite(char c, int delay) {
        //writes the entire string at once.
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        try {
            Thread.sleep(5);
        } catch (Exception e) {
        }
        PrintWriter pout = new PrintWriter(comPort.getOutputStream());
        pout.write(c);
        pout.flush();
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
        }
    }
}
