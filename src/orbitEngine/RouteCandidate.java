/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import static orbitEngine.Engine.dateString;

public class RouteCandidate {

    private double distance;
    private double launchTime;
    private double launchSpeed;
    private double launchEnergy;
    private double landSpeed;
    private double landEnergy;
    private double lostKineticEnergy;
    private double landTime;

    public RouteCandidate(double distance, double launchTime, double launchSpeed, double mass, double landSpeed, double lostKineticEnergy,
            double landTime) {
        this.distance = distance;
        this.launchTime = launchTime;
        this.launchSpeed = launchSpeed;
        this.launchEnergy = launchSpeed * launchSpeed * mass / 2.0;
        this.landSpeed = landSpeed;
        this.landEnergy = landSpeed * landSpeed * mass / 2.0;
        this.lostKineticEnergy = lostKineticEnergy;
        this.landTime = landTime;
    }

    String report() {
        return String.format("Distance: %e; Launch time: (%.0f) %s; Launch speed: %e; Launch energy: %e; Landing speed: %e; Land energy: %e; EnergyLost: %e; Land date: (%.0f) %s",
                distance, launchTime, dateString(launchTime), launchSpeed, landSpeed, launchEnergy, landEnergy, lostKineticEnergy, landTime, dateString(landTime));
    }
}
