/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Report {

    private ArrayList<String> reportList;

    private String reportFile;

    Report(String reportFile) {
        reportList = new ArrayList<>();
        this.reportFile = reportFile;
    }

    public synchronized void printLog(String strFormat, Object... args) {
        String result = String.format(strFormat, args);
        String[] lines = result.split("\\r?\\n|\\r");;
        for (String line : lines) {
            reportList.add(line + "\n");
        }
        if (reportList.size() > 100) {
            partiallDump();
        }
    }

    public synchronized void print(String strFormat, Object... args) {
        String result = String.format(strFormat, args);
        String[] lines = result.split("\\r?\\n|\\r");;
        for (String line : lines) {
            System.out.println(line);
            reportList.add(line + "\n");
        }
        if (reportList.size() > 100) {
            partiallDump();
        }
    }

    public synchronized void print(String line) {
        System.out.println(line);
        reportList.add(line + "\n");
        if (reportList.size() > 100) {
            partiallDump();
        }
    }

    private void partiallDump() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile, true))) {
            for (String line : reportList) {
                writer.append(line);
            }
            reportList.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void dump() {
        partiallDump();
        /*
        try {
            File file = new File(reportFile);
            file.getParentFile().mkdirs();

            try (PrintWriter out = new PrintWriter(reportFile)) {
                for (String line : reportList) {
                    out.println(line);
                }
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
         */
    }
}
