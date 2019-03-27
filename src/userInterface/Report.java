package userInterface;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Implement the report interface.
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class Report {

    /**
     * Report list buffer.
     */
    private ArrayList<String> reportList;

    /**
     * Report file name.
     */
    private String reportFile;

    /**
     * Create a new report.
     *
     * @param reportFile is the report fine name.
     */
    Report(String reportFile) {
        reportList = new ArrayList<>();
        this.reportFile = reportFile;
    }

    /**
     * Buffered formatted print to log file.
     *
     * @param strFormat print with formatted arguments string.
     * @param args      variable number arguments to print.
     */
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

    /**
     * Buffered formatted print to log file and stdout.
     *
     * @param strFormat print with formatted arguments string.
     * @param args      variable number arguments to print.
     */
    public synchronized void print(String strFormat, Object... args) {
        String result = String.format(strFormat, args);
        String[] lines = result.split("\\r?\\n|\\r");
        for (String line : lines) {
            System.out.println(line);
            reportList.add(line + "\n");
        }
        if (reportList.size() > 100) {
            partiallDump();
        }
    }

    /**
     * Buffered print to log file.
     *
     * @param line print string.
     */
    public synchronized void print(String line) {
        System.out.println(line);
        reportList.add(line + "\n");
        if (reportList.size() > 100) {
            partiallDump();
        }
    }

    /**
     * Private print buffer to file. The users must carry with the synchronization.
     */
    private void partiallDump() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile + ".log", true))) {
            for (String line : reportList) {
                writer.append(line);
            }
            reportList.clear();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Synchronized print buffer interface.
     */
    public synchronized void dump() {
        partiallDump();
    }

    /**
     * Print landing line to CSV file.
     *
     * @param csvLine line in csv format.
     */
    public void print_LandCSV(String csvLine) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile + "_land.csv", true))) {
            writer.append(csvLine + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Print near approach line to CSV file.
     *
     * @param csvLine line in csv format.
     */
    public void print_NearCSV(String csvLine) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile + "_near.csv", true))) {
            writer.append(csvLine + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
