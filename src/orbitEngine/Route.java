/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import static orbitEngine.Engine.dateString;

public class Route {

    private final Body spacecraft;
    private Body origin;
    private Body target;
    private int spacecraftIndex;
    private int targetIndex;
    private final Position virtualTarget = new Position();
    private final Position targetFailed = new Position();
    private final double startTime;
    private final double stopTime;
    private final double stepTime;
    private final double startSpeed;
    private final double stopSpeed;
    private final double stepSpeed;
    private double time;
    private boolean launched;
    private double speed;
    private double minTargetDistance;

    private static final double LAUNCH_ELEVATION = 1;
    private static final double OVERTAKE_DISTANCE_TOLERANCE = 10000.0;

    public Route(Body spacecraft, Body origin, Body target, double startTime, double stopTime, double stepTime, double startSpeed, double stopSpeed, double stepSpeed) {
        this.spacecraft = spacecraft;
        this.origin = origin;
        this.target = target;
        spacecraftIndex = spacecraft.getIndex();
        targetIndex = target.getIndex();
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.stepTime = stepTime;
        this.startSpeed = startSpeed;
        this.stopSpeed = stopSpeed;
        this.stepSpeed = stepSpeed;
        time = startTime;
        speed = startSpeed;
        launched = false;
        minTargetDistance = Double.MAX_VALUE;
    }

    /**
     * Reset Origin and destination bodies for a new calculus
     */
    void resetBodyValues(Constellation constellation) {
        origin = constellation.getBody(origin.getIndex());
        target = constellation.getBody(target.getIndex());
    }

    /**
     * Check if the target has been overtaken
     */
    boolean overtaking() {
        //@Todo decide a good overtaking detection algorithm  and calculate the missedTargetDistance
        double d = Constellation.dist[targetIndex][spacecraftIndex];
        if (d < minTargetDistance) {
            minTargetDistance = d;
            return false;
        } else if (d > (minTargetDistance + OVERTAKE_DISTANCE_TOLERANCE)) {
            // Prepare a new target
            targetFailed.x = spacecraft.x;
            targetFailed.y = spacecraft.y;
            targetFailed.z = spacecraft.z;
            virtualTarget.x = target.x;
            virtualTarget.y = target.y;
            virtualTarget.z = target.z;
            //@Todo this is not the solution to the target, lllok a best hipothesys
            minTargetDistance = Double.MAX_VALUE;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Program next launch conditions
     *
     * @return true until no new conditions programmed
     */
    public boolean nextLaunch(boolean iterateSpeedFirst) {
        launched = false;
        if (iterateSpeedFirst) {
            speed += stepSpeed;
            if (speed > stopSpeed) {
                speed = startSpeed;
                time += stepTime;
                if (time > stopTime) {
                    return false;
                }
            }
        } else {
            time += stepTime;
            if (time > stopTime) {
                time = startTime;
                speed += stepSpeed;
                if (speed > stopSpeed) {
                    return false;
                }
            }
        }
        System.out.printf("Next Launch time '%s' with speed: %f\n", dateString(time), speed);
        return true;
    }

    /**
     * Time to launch check
     */
    boolean timeToLaunch(double seconds) {
        return (seconds >= time);
    }

    /**
     * Launch to the next target iteration point. We will use this to calculate
     * the error if we miss the target and adjust next launch
     */
    public void launchToNextTarget() {
        double virtualTarget_x = target.x;
        double virtualTarget_y = target.y;
        double virtualTarget_z = target.z;
        if (targetFailed != null) {
            //@Todo decide a good overtaking correction algorithm
            //@Todo this is not the solution to the target, lllok a best hipothesys
            virtualTarget_x = (virtualTarget_x + targetFailed.x) / 2.0;
            virtualTarget_y = (virtualTarget_x + targetFailed.y) / 2.0;
            virtualTarget_z = (virtualTarget_x + targetFailed.z) / 2.0;
        }
        //@Todo precalculate the speed for this launch with apis to iterate speed and time
        //@Todo we need a aproaching iterator with some edn condition
        // Distance to the target and the 3 distance proyections
        double dx = virtualTarget_x - origin.x;
        double dy = virtualTarget_y - origin.y;
        double dz = virtualTarget_z - origin.z;
        double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
        // Calculate the launch speed as launch speed + origin speed;
        double vxr = speed * dx / d;
        double vyr = speed * dy / d;
        double vzr = speed * dz / d;
        spacecraft.vx = origin.vx + vxr;
        spacecraft.vy = origin.vy + vyr;
        spacecraft.vz = origin.vz + vzr;
        // Calculate the launch position as the origin body position that points to destination
        double r = origin.getRadius() + spacecraft.getRadius() + LAUNCH_ELEVATION;
        double xr = r * dx / d;
        double yr = r * dy / d;
        double zr = r * dz / d;
        spacecraft.x = origin.x + xr;
        spacecraft.y = origin.y + yr;
        spacecraft.z = origin.z + zr;
        spacecraft.merged = false;
        launched = true;
    }

    /**
     * @return the spacecraft
     */
    public Body getSpacecraft() {
        return spacecraft;
    }

    /**
     * @return if spacecraft land
     */
    public boolean spacecraftLand() {
        return spacecraft.merged;
    }

    /**
     * @return spacecraft land name
     */
    public Body spacecraftLandBody() {
        return spacecraft.mergedWith;
    }

    /**
     * @return if the rocket is launched
     */
    public boolean isLaunched() {
        return launched;
    }
}
