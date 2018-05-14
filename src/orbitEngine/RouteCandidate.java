/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import static orbitEngine.Engine.dateString;

public class RouteCandidate {

    private boolean land;
    private double distance;
    private double launchTime;
    private double launchSpeed;
    private double launchEnergy;
    private double landSpeed;
    private double landEnergy;
    private double lostKineticEnergy;
    private double finalTime;

    public RouteCandidate(double distance, double launchTime, double overtakeTime, double launchSpeed, double mass) {
        this.land = false;
        this.distance = distance;
        this.launchTime = launchTime;
        this.finalTime = overtakeTime;
        this.launchSpeed = launchSpeed;
        this.launchEnergy = launchSpeed * launchSpeed * mass / 2.0;
    }

    public RouteCandidate(double launchTime, double landTime, double launchSpeed, double mass, double landSpeed, double lostKineticEnergy) {
        this.land = true;
        this.launchTime = launchTime;
        this.finalTime = landTime;
        this.launchSpeed = launchSpeed;
        this.launchEnergy = launchSpeed * launchSpeed * mass / 2.0;
        this.landSpeed = landSpeed;
        this.landEnergy = landSpeed * landSpeed * mass / 2.0;
        this.lostKineticEnergy = lostKineticEnergy;
    }

    String report() {
        String s;
        if (land) {
            s = String.format("Launch time: (%.0f) %s; Land date: (%.0f) %s; Launch speed: %e; Launch energy: %e; Landing speed: %e; Land energy: %e; EnergyLost: %e",
                    launchTime, dateString(launchTime), finalTime, dateString(finalTime), launchSpeed, launchEnergy, landSpeed, landEnergy, lostKineticEnergy);
        } else {
            s = String.format("Overtake: launch time: (%.0f) %s; overtake date: (%.0f) %s; launch speed: %e; launch energy: %e; overtake distance: %e",
                    launchTime, dateString(launchTime), finalTime, dateString(finalTime), launchSpeed, launchEnergy, distance);
        }
        return s;
    }

    String reportCSV() {
        String s;
        if (land) {
            s = String.format("%.0f; %s; %.0f; %s; %e; %e; %e; %e; %e",
                    launchTime, dateString(launchTime), finalTime, dateString(finalTime), launchSpeed, launchEnergy, landSpeed, landEnergy, lostKineticEnergy);
        } else {
            s = String.format("%.0f; %s; %.0f; %s; %e; %e; %e",
                    launchTime, dateString(launchTime), finalTime, dateString(finalTime), launchSpeed, launchEnergy, distance);
        }
        return s.replace(",", ".").replace(";", ",");
    }

    static String reportCSV_landHead() {
        return "Launch epoch, Launch date, Land epoch, Land date, Launch speed, Launch energy, Landing speed, Land energy, EnergyLost";
    }

    static String reportCSV_overtakeHead() {
        return "Launch epoch, Launch date, Overtake epoch, Overtake date, Launch speed, Launch energy, Overtake distance";
    }

}
