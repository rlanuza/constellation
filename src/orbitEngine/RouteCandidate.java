/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

public class RouteCandidate {

    private double distance;
    private double startTime;
    private double startSpeed;
    private double launchEnergy;
    private double landSpeed;
    private double landEnergy;
    private String arrivalDate;

    public RouteCandidate(double distance, double startTime, double startSpeed, double mass, double landSpeed, double landEnergy,
            String arrivalDate) {
        this.distance = distance;
        this.startTime = startTime;
        this.startSpeed = startSpeed;
        this.launchEnergy = startSpeed * startSpeed * mass / 2.0;
        this.landSpeed = landSpeed;
        this.landEnergy = landEnergy;
        this.arrivalDate = arrivalDate;
    }

    String report() {
        return String.format("distance: %e; Start time: %e; Start speed: %e; Land energy: %e; Landing speed: %e; Land energy: %e; Date %s",
                distance, startTime, startSpeed, landSpeed, launchEnergy, landEnergy, arrivalDate);
    }
}
