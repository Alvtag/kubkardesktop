package com.alvtag.kubkardesktop.print;

import com.alvtag.kubkardesktop.HeatForm;
import com.alvtag.kubkardesktop.RaceOverViewForm;
import com.alvtag.kubkardesktop.models.Heat;
import com.alvtag.kubkardesktop.models.Racer;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.util.HashMap;
import java.util.List;

public class Printer implements Printable {
//  public static void main(String[] args) {
//    Printer example1 = new Printer();
//    System.exit(0);
//  }

    private static final int INCH = 72;
    private static final int SPACER = 30;

    private Heat[] heatsArray;
    private List<Racer> racerList;
    private HashMap<Integer, Boolean> activeTracks;
    private boolean printRaceTimes;

    /**
     * @param printRaceTimes If true, prints resulting times. If false
     */
    public Printer(Heat[] heatsArray, List<Racer> racerList, HashMap<Integer, Boolean> activeTracks, boolean printRaceTimes) {
        this.printRaceTimes = printRaceTimes;
        this.heatsArray = heatsArray;
        this.racerList = racerList;
        this.activeTracks = activeTracks;
    }

    public void startJob() {
        //--- Create a printerJob object
        PrinterJob printJob = PrinterJob.getPrinterJob();

        printJob.setPrintable(this);
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (Exception PrintException) {
                PrintException.printStackTrace();
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int page) {
        int columnWidth = g.getFontMetrics().stringWidth("Heat 18");
        int charHeight = g.getFontMetrics().getAscent();

        if (page == 0) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.black);
            //--- Translate the origin to be (0,0)
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // 1 lane labels (all six lanes will be allocated printing spaced )
            int y = INCH - charHeight;

            int x1 = INCH + columnWidth + SPACER;
            for (String s : HeatForm.COLOR_STRINGS) {
                g.drawString(s, x1, y);
                x1 += columnWidth;
            }

            // 2 Per-heat racers List (all six lanes will be allocated printing spaced )
            int maxX = 0;
            int maxY = 0;
            y = INCH;
            for (int i = 0; i < heatsArray.length; i++) {
                Heat heat = heatsArray[i];
                int x2 = INCH;
                int heatIndex = heat.getHeatId() + 1;
                g.drawString("Heat " + heatIndex, x2, y);
                x2 += columnWidth + SPACER;
                List<Racer> racers = heat.getRacers();

                int racerIndex = 0;
                for (int trackIndex = 0; trackIndex < activeTracks.keySet().size(); trackIndex++) {
                    if (activeTracks.get(trackIndex)) {
                        Racer racer = racers.get(racerIndex);
                        if (printRaceTimes) {

                            if (heat.getRaceTimesMS() != null) {
                                int time = heat.getRaceTimesMS()[racerIndex];
                                String s = String.format("%d.%d s", (time / 1000), (time % 1000));
                                g.drawString(s, x2, y);
                            } else {
                                g.drawString("-1", x2, y);
                            }
                        } else {
                            g.drawString(String.valueOf(racer.getRacerId()), x2, y);
                        }
                        racerIndex++;
                    }
                    x2 += columnWidth;
                }
                maxX = Math.max(maxX, x2);
                y += (i % 3 == 2) ? (2 * charHeight) : charHeight;
                maxY = Math.max(maxY, y);
            }

            // 3 Participant List (only for heat printing)
            if (!printRaceTimes) {
                y = INCH;
                int x3 = maxX;
                for (Racer racer : racerList) {
                    String string = "" + racer.getRacerId() + " – " + racer.getName();
                    g.drawString(string, x3, y);
                    y += charHeight;
                    maxY = Math.max(maxY, y);
                }
            }

            // 3 fastest average times
            if (printRaceTimes) {
                int x4 = INCH;
                y = maxY+ SPACER;

                Racer[] racers = RaceOverViewForm.getAverageTimeSortedRacersArray(heatsArray, racerList);

                for (int i = 0; i < racers.length && i < 3; i++) {
                    StringBuilder stringBuilder = new StringBuilder("#");
                    stringBuilder.append(i);
                    stringBuilder.append(": ");
                    stringBuilder.append(racers[i].getName());
                    stringBuilder.append("(");
                    stringBuilder.append(racers[i].getRacerId());
                    stringBuilder.append(") ");
                    stringBuilder.append(": ");
                    int time = racers[i].getAverageRaceTimeMs();
                    stringBuilder.append(String.format("%d.%d s", (time / 1000), (time % 1000)));
                    g.drawString(stringBuilder.toString(), x4, y);
                    y += charHeight;
                }
            }
            return (PAGE_EXISTS);
        } else {
            return (NO_SUCH_PAGE);
        }
    }
}
