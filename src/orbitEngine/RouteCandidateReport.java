package orbitEngine;

import static orbitEngine.Engine.dateString;
import userInterface.Command;

/**
 * Represents the essential information methods that characterize a route candidate reporter.
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class RouteCandidateReport {

    /**
     * CSV field delimiter
     */
    private static String CSV_DELIMITER = ",";
    /**
     * float point delimiter
     */
    private static String CSV_DECIMAL_POINT = ".";

    /**
     * Report line in string format
     */
    private final String report;
    /**
     * Report line in CSV string format
     */
    private final String reportCSV;

    /**
     * Create a new head report line for potential candidate rejected.
     *
     * @param distance when rocket overtake the target. Unit m.
     * @param launchTime launch time. Units seconds.
     * @param overtakeTime overtake time. Units seconds.
     * @param launchSpeed launch speed. Units m/s.
     * @param mass spacecraft mass. Units Kg.
     */
    public RouteCandidateReport(double distance, double launchTime,
            double overtakeTime, double launchSpeed, double mass) {
        double launchEnergy = launchSpeed * launchSpeed * mass / 2.0;
        report = adaptDelimiters(
                String.format("Launch time: (%.0f) %s; Overtake date: (%.0f) %s;"
                        + " Launch speed: %e; Launch energy: %e;"
                        + " Overtake distance: %e",
                        launchTime, dateString(launchTime), overtakeTime,
                        dateString(overtakeTime), launchSpeed, launchEnergy,
                        distance)
        );
        reportCSV = adaptDelimiters(
                String.format("%.0f# %s# %.0f# %s# %.12g# %.12g# %.12g",
                        launchTime, dateString(launchTime), overtakeTime,
                        dateString(overtakeTime), launchSpeed, launchEnergy,
                        distance)
        );
    }

    /**
     * Create a new report line for real landing.
     *
     * @param name land planet name.
     * @param launchTime launch time. Units seconds.
     * @param landTime landing time. Units seconds.
     * @param launchSpeed launch speed. Units m/s.
     * @param mass spacecraft mass. Units Kg.
     * @param landSpeed landing speed. Units m/s.
     * @param launchVector launch vector with normalized direction.
     */
    public RouteCandidateReport(String name, double launchTime, double landTime,
            double launchSpeed, double mass, double landSpeed,
            Vector3d launchVector) {
        double launchEnergy = launchSpeed * launchSpeed * mass / 2.0;
        double landEnergy = landSpeed * landSpeed * mass / 2.0;
        double totalEnergy = launchEnergy + landEnergy;

        report = adaptDelimiters(
                String.format("name: %s;Launch time: (%.0f) %s;"
                        + " Land date: (%.0f) %s; Launch speed: %e;"
                        + " Launch energy: %e; Landing speed: %e;"
                        + " Land energy: %e; Energy: %e Launch Vx: %e;"
                        + " Launch Vy: %e; Launch Vz: %e",
                        name, launchTime, dateString(launchTime), landTime,
                        dateString(landTime), launchSpeed, launchEnergy,
                        landSpeed, landEnergy, totalEnergy,
                        launchVector.x, launchVector.y, launchVector.z)
        );
        reportCSV = adaptDelimiters(
                String.format("%s# %.0f# %s# %.0f# %s# %.12g# %.12g# %.12g#"
                        + " %.12g# %.12g# %.12g# %.12g# %.12g",
                        name, launchTime, dateString(launchTime), landTime,
                        dateString(landTime), launchSpeed, launchEnergy,
                        landSpeed, landEnergy, totalEnergy,
                        launchVector.x, launchVector.y, launchVector.z)
        );
    }

    /**
     * Change default delimiters by the required in log
     *
     * @param sRaw string with raw delimiters.
     * @return string with the required delimiters
     */
    private String adaptDelimiters(String sRaw) {
        return sRaw
                .replace(",", ".")
                .replace(".", CSV_DECIMAL_POINT)
                .replace("#", CSV_DELIMITER);
    }

    /**
     * String format report line
     *
     * @return formatted report line.
     */
    String report() {
        return report;
    }

    /**
     * CSV-String format report line
     *
     * @return CSV-formatted report line.
     */
    String reportCSV() {
        return reportCSV;
    }

    /**
     * Load required CSV and float point delimiters
     *
     * @param cmd command to read the CSV delimiters.
     */
    static void setFormat(Command cmd) {
        RouteCandidateReport.CSV_DELIMITER = cmd.CSV_DELIMITER;
        RouteCandidateReport.CSV_DECIMAL_POINT = cmd.CSV_DECIMAL_POINT;
    }

    /**
     * Create a new head report line for real landing.
     *
     * @return head string for CSV land report
     */
    static String reportCSV_landHead() {
        String s = "Land body name# Launch epoch# Launch date# Land epoch#"
                + " Land date# Launch speed# Launch energy# Landing speed#"
                + " Land energy# Energy# Launch Vx# Launch Vy# Launch Vz";
        return s.replace("#", CSV_DELIMITER);
    }

    /**
     * Create a new head report line for potential candidate rejected.
     *
     * @return head string for CSV overtake report
     */
    static String reportCSV_overtakeHead() {
        String s = "Launch epoch# Launch date# Overtake epoch# Overtake date#"
                + " Launch speed# Launch energy# Overtake distance";
        return s.replace("#", CSV_DELIMITER);
    }
}
