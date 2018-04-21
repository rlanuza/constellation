/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Report {

    private ArrayList<String> reportList;

    private String reportFile;

    Report(String reportFile) {
        reportList = new ArrayList<>();
        this.reportFile = reportFile;
    }

    public void print(String strFormat, Object... args) {
        String result = String.format(strFormat, args);
        String[] lines = result.split("\\r?\\n|\\r");;
        for (String line : lines) {
            reportList.add(line);
        }
    }

    /*
    String fillInString(String str, String ... token){
    return String.format(str, token);
}
     */
    public void print(String newLine) {
        reportList.add(newLine);
    }

    public void dump() {
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
    }
}
