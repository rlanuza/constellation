/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import static orbitEngine.Engine.dateString;
import userInterface.Command;

public class RouteCandidate {

    private static String CSV_DELIMITER = ",";
    private static String CSV_DECIMAL_POINT = ".";

    private final String report;
    private final String reportCSV;

    public RouteCandidate(double distance, double launchTime,
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

    public RouteCandidate(String name, double launchTime, double landTime,
            double launchSpeed, double mass, double landSpeed,
            double lostKineticEnergy, Vector3d launchVector) {
        double launchEnergy = launchSpeed * launchSpeed * mass / 2.0;
        double landEnergy = landSpeed * landSpeed * mass / 2.0;

        report = adaptDelimiters(
                String.format("name: %s;Launch time: (%.0f) %s;"
                        + " Land date: (%.0f) %s; Launch speed: %e;"
                        + " Launch energy: %e; Landing speed: %e;"
                        + " Land energy: %e; EnergyLost: %e Launch Vx: %e;"
                        + " Launch Vy: %e; Launch Vz: %e",
                        name, launchTime, dateString(launchTime), landTime,
                        dateString(landTime), launchSpeed, launchEnergy,
                        landSpeed, landEnergy, lostKineticEnergy,
                        launchVector.x, launchVector.y, launchVector.z)
        );
        reportCSV = adaptDelimiters(
                String.format("%s# %.0f# %s# %.0f# %s# %.12g# %.12g# %.12g#"
                        + " %.12g# %.12g# %.12g# %.12g# %.12g",
                        name, launchTime, dateString(launchTime), landTime,
                        dateString(landTime), launchSpeed, launchEnergy,
                        landSpeed, landEnergy, lostKineticEnergy,
                        launchVector.x, launchVector.y, launchVector.z)
        );
    }

    private String adaptDelimiters(String sRaw) {
        return sRaw
                .replace(",", ".")
                .replace(".", CSV_DECIMAL_POINT)
                .replace("#", CSV_DELIMITER);
    }

    String report() {
        return report;
    }

    String reportCSV() {
        return reportCSV;
    }

    static void setFormat(Command cmd) {
        RouteCandidate.CSV_DELIMITER = cmd.CSV_DELIMITER;
        RouteCandidate.CSV_DECIMAL_POINT = cmd.CSV_DECIMAL_POINT;
    }

    static String reportCSV_landHead() {
        String s = "Land body name#Launch epoch# Launch date# Land epoch#"
                + " Land date# Launch speed# Launch energy# Landing speed#"
                + " Land energy# EnergyLost# Launch Vx# Launch Vy# Launch Vz";
        return s.replace("#", CSV_DELIMITER);
    }

    static String reportCSV_overtakeHead() {
        String s = "Launch epoch# Launch date# Overtake epoch# Overtake date#"
                + " Launch speed# Launch energy# Overtake distance";
        return s.replace("#", CSV_DELIMITER);
    }

}
