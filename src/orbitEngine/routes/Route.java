/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine.routes;

import orbitEngine.Body;
import orbitEngine.Position;

public class Route {

    private final Body spacecraft;
    private final Body origin;
    private final Body target;
    private final Position virtualTarget = new Position();
    private final Position missedTargetTarget = new Position();
    private final long startTime;
    private final long stopTime;
    private final long stepTime;
    private final double startSpeed;
    private final double stopSpeed;
    private final double stepSpeed;
    public double time;
    private double speed;
    private double missedTargetDistance;

    public Route(Body spacecraft, Body origin, Body target, long startTime, long stopTime, long stepTime, double startSpeed, double stopSpeed, double stepSpeed) {
        this.spacecraft = spacecraft;
        this.origin = origin;
        this.target = target;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.stepTime = stepTime;
        this.startSpeed = startSpeed;
        this.stopSpeed = stopSpeed;
        this.stepSpeed = stepSpeed;
        this.time = startTime;
        this.speed = startSpeed;
        missedTargetDistance = 0.0;
    }

    /**
     * Set the next virtual target
     *
     * @Todo the target will be different on every retry, this must be adjusted to the middle of the missed old target
     */
    private void setNextTarget() {
        if (missedTargetDistance == 0.0) {
            virtualTarget.x = target.x;
            virtualTarget.y = target.y;
            virtualTarget.z = target.z;
        } else {
            virtualTarget.x = (target.x + missedTargetTarget.x) / 2.0;
            virtualTarget.y = (target.y + missedTargetTarget.y) / 2.0;
            virtualTarget.z = (target.z + missedTargetTarget.z) / 2.0;
        }
    }

    /**
     * Program next launch conditions
     *
     * @return true until no new conditions programmed
     * @Todo Analyze the best place to call this iterator: Route.launch or Engine...
     */
    public boolean nextLaunch() {
        time += stepTime;
        if (time > stopTime) {
            time = startTime;
            speed += stepSpeed;
            if (speed > stopSpeed) {
                return false;
            }
        }
        return true;
    }

    /**
     * Launch to the next target iteration point. We will use this to calculate the error if we miss the target and adjust next launch
     */
    public void launchToNextTarget() {
        setNextTarget();
        //@Todo precalculate the spped for this launch with apis to iterate spped and time
        // Distance to the target and the 3 distance proyections
        double dx = virtualTarget.x - origin.x;
        double dy = virtualTarget.y - origin.y;
        double dz = virtualTarget.z - origin.z;
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

    /**
     * @return the spacecraft
     */
    public Body getSpacecraft() {
        return spacecraft;
    }
}
