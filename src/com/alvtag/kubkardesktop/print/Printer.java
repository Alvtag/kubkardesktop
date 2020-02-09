package com.alvtag.kubkardesktop.print;

import com.alvtag.kubkardesktop.HeatForm;
import com.alvtag.kubkardesktop.models.Heat;
import com.alvtag.kubkardesktop.models.Racer;

import java.awt.*;
import java.awt.geom.Line2D;
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
    private static final int COLUMN_SPACER = 30;

    private Heat[] heatsArray;
    private List<Racer> racerList;
    private HashMap<Integer, Boolean> activeTracks;

    public Printer(Heat[] heatsArray, List<Racer> racerList, HashMap<Integer, Boolean> activeTracks) {
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

            int x1 = INCH + columnWidth + COLUMN_SPACER;
            for (String s : HeatForm.COLOR_STRINGS) {
                g.drawString(s, x1, y);
                x1 += columnWidth;
            }

            // 2 Per-heat racers List (all six lanes will be allocated printing spaced )
            int maxX = 0;
            y = INCH;
            for (int i = 0; i < heatsArray.length; i++) {
                Heat heat = heatsArray[i];
                int x2 = INCH;
                int heatIndex = heat.getHeatId() + 1;
                g.drawString("Heat " + heatIndex, x2, y);
                x2 += columnWidth + COLUMN_SPACER;
                List<Racer> racers = heat.getRacers();

                int racerIndex = 0;
                for (int trackIndex = 0; trackIndex < activeTracks.keySet().size(); trackIndex++) {
//                for (int trackIndex = 0, racersSize = racers.size(); trackIndex < racersSize; trackIndex++) {

                    if (activeTracks.get(trackIndex)) {
                        Racer racer = racers.get(racerIndex);
                        g.drawString(String.valueOf(racer.getRacerId()), x2, y);
                        racerIndex++;
                    }

                    x2 += columnWidth;
                }
                maxX = Math.max(maxX, x2);
                y += (i % 3 == 2) ? (2 * charHeight) : charHeight;
            }

            // 3 Participant List
            y = INCH;
            int x3 = maxX;
            for (Racer racer : racerList) {
                String string = "" + racer.getRacerId() + " – " + racer.getName();
                g.drawString(string, x3, y);
                y += charHeight;
            }
            return (PAGE_EXISTS);
        } else {
            return (NO_SUCH_PAGE);
        }
    }
}
