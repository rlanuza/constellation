/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine.routes;

import orbitEngine.Body;

public class Route {

    Body spacecraft;
    Body origin;
    Body target;
    double startTime;
    double stopTime;
    double stepTime;
    double startSpeed;
    double stopSpeed;
    double stepSpeed;
    double speed;

    public Route(Body spacecraft, Body origin, Body target, double startTime, double stopTime, double stepTime, double startSpeed, double stopSpeed, double stepSpeed) {
        this.spacecraft = spacecraft;
        this.origin = origin;
        this.target = target;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.stepTime = stepTime;
        this.startSpeed = startSpeed;
        this.stopSpeed = stopSpeed;
        this.stepSpeed = stepSpeed;
    }

    // Launh to the current point to let us calculate the error when it miss the target
    public void directLaunch() {
        //@Todo precalculate the spped for this launch with apis to iterate spped and time
        // Distance to the target and the 3 distance proyections
        double dx = target.x - origin.x;
        double dy = target.y - origin.y;
        double dz = target.z - origin.z;
        double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
        // Calculate the launch speed as launch speed + origin speed;
        double vxr = speed * dx / d;
        double vyr = speed * dy / d;
        double vzr = speed * dz / d;
        spacecraft.vx = origin.vx + vxr;
        spacecraft.vy = origin.vy + vyr;
        spacecraft.vz = origin.vz + vzr;
        // Calculate the launch position as the origin body position that points to destination
        double r = origin.getRadius();
        double xr = r * dx / d;
        double yr = r * dy / d;
        double zr = r * dz / d;
        spacecraft.x = origin.x + xr;
        spacecraft.y = origin.y + yr;
        spacecraft.z = origin.z + zr;
    }
}
