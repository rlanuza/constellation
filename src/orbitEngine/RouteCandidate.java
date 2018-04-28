/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

public class RouteCandidate {

    private boolean land;
    private double distance;
    private double startTime;
    private double startSpeed;
    private double landSpeed;
    private double energy;

    public RouteCandidate(boolean land, double distance, double startTime, double startSpeed, double landSpeed, double energy) {
        this.land = land;
        this.distance = distance;
        this.startTime = startTime;
        this.startSpeed = startSpeed;
        this.landSpeed = landSpeed;
        this.energy = energy;
    }
}
