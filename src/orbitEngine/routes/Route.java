/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine.routes;

import orbitEngine.Body;

public class Route {

    Body spaceCraft;
    Body origin;
    Body destination;
    double startTime;

    public Route(Body spaceCraft, Body origin, Body destination, double startTime) {
        this.spaceCraft = spaceCraft;
        this.origin = origin;
        this.destination = destination;
        this.startTime = startTime;
    }
}
