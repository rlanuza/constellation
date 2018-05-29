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

    public RouteCandidate(double distance, double launchTime, double overtakeTime, double launchSpeed, double mass) {
        double launchEnergy = launchSpeed * launchSpeed * mass / 2.0;
        report = String.format("Launch time: (%.0f) %s; Overtake date: (%.0f) %s;"
                + " Launch speed: %e; Launch energy: %e; Overtake distance: %e",
                launchTime, dateString(launchTime), overtakeTime, dateString(overtakeTime), launchSpeed, launchEnergy, distance);
        reportCSV = String.format("%.0f# %s# %.0f# %s# %g# %g# %g",
                launchTime, dateString(launchTime), overtakeTime,
                dateString(overtakeTime), launchSpeed, launchEnergy, distance
        ).replace(",", ".").replace(".", CSV_DECIMAL_POINT).replace("#", CSV_DELIMITER);
    }

    public RouteCandidate(String name, double launchTime, double landTime, double launchSpeed, double mass, double landSpeed, double lostKineticEnergy, Vector3d launchVector) {
        double launchEnergy = launchSpeed * launchSpeed * mass / 2.0;
        double landEnergy = landSpeed * landSpeed * mass / 2.0;
        report = String.format("name: %s;Launch time: (%.0f) %s; Land date: (%.0f) %s;"
                + " Launch speed: %e; Launch energy: %e; Landing speed: %e;"
                + " Land energy: %e; EnergyLost: %e"
                + " Launch Vx: %e; Launch Vy: %e; Launch Vz: %e",
                name, launchTime, dateString(launchTime), landTime, dateString(landTime),
                launchSpeed, launchEnergy, landSpeed, landEnergy, lostKineticEnergy,
                launchVector.x, launchVector.y, launchVector.z);
        reportCSV = String.format("%s# %.0f# %s# %.0f# %s# %.10g# %.10g# %.10g# %.10g# %.10g# %.10g# %.10g# %.10g",
                name, launchTime, dateString(launchTime), landTime, dateString(landTime),
                launchSpeed, launchEnergy, landSpeed, landEnergy, lostKineticEnergy,
                launchVector.x, launchVector.y, launchVector.z
        ).replace(",", ".").replace(".", CSV_DECIMAL_POINT).replace("#", CSV_DELIMITER);
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
        return "Land body name#Launch epoch# Launch date# Land epoch# Land date# Launch speed"
                + "# Launch energy# Landing speed# Land energy# EnergyLost"
                + "# Launch Vx# Launch Vy# Launch Vz"
                        .replace("#", CSV_DELIMITER);
    }

    static String reportCSV_overtakeHead() {
        return "Launch epoch# Launch date# Overtake epoch# Overtake date# Launch speed"
                + "# Launch energy# Overtake distance"
                        .replace("#", CSV_DELIMITER);
    }

}
