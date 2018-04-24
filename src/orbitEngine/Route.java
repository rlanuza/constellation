/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import static orbitEngine.Engine.dateString;
import userInterface.Report;

public class Route {

    private final Body spacecraft;
    private Body origin;
    private Body target;
    private Body star;
    private int spacecraftIndex;
    private int originIndex;
    private int targetIndex;
    private int starIndex;

    private final Position spacecraftFail = new Position();
    private final Position targetFail = new Position();
    private final Position correction = new Position();
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
    private boolean newInitialConditionsLaunch;
    private int stepsLimitOnCandidate;

    private int STEPS_LIMIT_ON_CANDIDATE;
    private double LAUNCH_ELEVATION = 1;
    private double OVERTAKE_DISTANCE_TOLERANCE;
    private double MAX_OVERTAKE_DISTANCE;
    private Report report;

    public Route(Report report, Body spacecraft, Body origin, Body target, Body star,
            double startTime, double stopTime, double stepTime,
            double startSpeed, double stopSpeed, double stepSpeed,
            double LAUNCH_ELEVATION,
            double OVERTAKE_DISTANCE_TOLERANCE,
            double MAX_OVERTAKE_DISTANCE,
            int stepsLimOnCandidate) {
        this.report = report;
        this.spacecraft = spacecraft;
        this.origin = origin;
        this.target = target;
        this.star = star;
        spacecraftIndex = spacecraft.getIndex();
        originIndex = origin.getIndex();
        targetIndex = target.getIndex();
        starIndex = star.getIndex();
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.stepTime = stepTime;
        this.startSpeed = startSpeed;
        this.stopSpeed = stopSpeed;
        this.stepSpeed = stepSpeed;
        this.OVERTAKE_DISTANCE_TOLERANCE = OVERTAKE_DISTANCE_TOLERANCE;
        this.MAX_OVERTAKE_DISTANCE = MAX_OVERTAKE_DISTANCE;
        this.STEPS_LIMIT_ON_CANDIDATE = stepsLimOnCandidate;
        time = startTime;
        speed = startSpeed;
        newInitialConditionsLaunch = true;
        launched = false;
        minTargetDistance = Double.MAX_VALUE;
    }

    /**
     * Reset Origin and destination bodies for a new calculus
     */
    void resetBodyValues(Constellation constellation) {
        origin = constellation.getBody(originIndex);
        target = constellation.getBody(targetIndex);
    }

    /**
     * Check if the target has been overtaken
     */
    boolean overtaking() {
        double dSpacecraftToTarget = Constellation.dist[targetIndex][spacecraftIndex];
        if (dSpacecraftToTarget < minTargetDistance) {  // A new minimum distance --> Continue this
            minTargetDistance = dSpacecraftToTarget;
            return false;
        } else if (dSpacecraftToTarget > (minTargetDistance + OVERTAKE_DISTANCE_TOLERANCE)) {     // A clear overtaking
            // Heuristic a) the distance must be near than a limit else reject the iteration
            if (dSpacecraftToTarget > MAX_OVERTAKE_DISTANCE) {
                report.print(" -> overtaking: As the distance to target %e > %e [MAX_OVERTAKE_DISTANCE]. This launch iteration is dismissed",
                        dSpacecraftToTarget, MAX_OVERTAKE_DISTANCE);
                newInitialConditionsLaunch = true;
                launched = false;
                return true;
            }
            // Heuristic b) the distance to STAR must be far than Target else reject the iteration
            double dStartToTarget = (targetIndex > starIndex) ? Constellation.dist[starIndex][targetIndex] : Constellation.dist[targetIndex][starIndex];
            double dStartToSpacecraft = (spacecraftIndex > starIndex) ? Constellation.dist[starIndex][spacecraftIndex] : Constellation.dist[spacecraftIndex][starIndex];
            if (dStartToTarget > dStartToSpacecraft) {
                report.print(" -> overtaking: As the star distance to target %e > %e, the rocket distance to target. This launch iteration is dismissed",
                        dStartToTarget, dStartToSpacecraft);
                newInitialConditionsLaunch = true;
                launched = false;
                return true;
            }

            report.print("  Spacecraft fail: dist.:%e [dx=%e, dy=%e, dz=%e]\n", distance(spacecraft, target),
                    target.x - spacecraft.x, target.y - spacecraft.y, target.z - spacecraft.z);
            // Heuristic c) Calculate a new taget based on the error compensation with a sinple iteration counter limit
            // Prepare a new iteration if the conditions are good modifying the target
            if (stepsLimitOnCandidate > 0) {
                stepsLimitOnCandidate--;
                spacecraftFail.x = spacecraft.x;
                spacecraftFail.y = spacecraft.y;
                spacecraftFail.z = spacecraft.z;
                targetFail.x = target.x;
                targetFail.y = target.y;
                targetFail.z = target.z;
                report.print(" -> overtaking: New temptative [%d of %d] adjusting the initial speed vector",
                        STEPS_LIMIT_ON_CANDIDATE - stepsLimitOnCandidate, STEPS_LIMIT_ON_CANDIDATE);
                launched = false;
                return true;
            } else {
                report.print(" -> overtaking: No more temptative all STEPS_LIMIT_ON_CANDIDATE = %d were consumed", STEPS_LIMIT_ON_CANDIDATE);
                newInitialConditionsLaunch = true;
                launched = false;
                return true;
            }
        } else {    // A distance in tolerance, but probably getting worse
            return false;
        }
    }

    /**
     * Program next launch conditions
     *
     * @param iterateSpeedFirst
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
        report.print("- Next Launch time: %s (epoch: %.2f), with speed: %e", dateString(time), time, speed);
        newInitialConditionsLaunch = true;
        return true;
    }

    /**
     * Time to launch check
     */
    boolean timeToLaunch(double seconds) {
        return (seconds >= time);
    }

    boolean timeToSave(double seconds, double stepTime) {
        return ((seconds + stepTime) >= time);
    }

    /**
     * Launch to the next target iteration point. We will use this to calculate
     * the error if we miss the target and adjust next launch
     */
    public void launchToNextTarget() {
        minTargetDistance = Double.MAX_VALUE;
        final Position direction = new Position();
        // Straight launch using the planet speed vector
        direction.x = origin.vx;
        direction.y = origin.vy;
        direction.z = origin.vz;
        if (newInitialConditionsLaunch) {
            correction.reset();
            stepsLimitOnCandidate = STEPS_LIMIT_ON_CANDIDATE;
        } else {
            double dTargetToOrigin = distance(origin, targetFail);
            // Corrected direction
            correction.x += targetFail.x - spacecraftFail.x;
            correction.y += targetFail.y - spacecraftFail.y;
            correction.z += targetFail.z - spacecraftFail.z;
            direction.x += origin.vx * correction.x / dTargetToOrigin;
            direction.y += origin.vy * correction.y / dTargetToOrigin;
            direction.z += origin.vz * correction.z / dTargetToOrigin;
        }
        newInitialConditionsLaunch = false;
        //System.out.printf("Spacecraft vtarget x:%f , y:%f , z:%f \n", direction.x, direction.y, direction.z);
        //@Todo precalculate the speed for this launch with apis to iterate speed and time
        //@Todo we need a aproaching iterator with some edn condition
        // Direction to the target and the 3 distance proyections
        double directionM = Math.sqrt(direction.x * direction.x + direction.y * direction.y + direction.z * direction.z);
        // Calculate the launch speed as launch speed + origin speed;
        double vxr = speed * direction.x / directionM;
        double vyr = speed * direction.y / directionM;
        double vzr = speed * direction.z / directionM;
        spacecraft.vx = origin.vx + vxr;
        spacecraft.vy = origin.vy + vyr;
        spacecraft.vz = origin.vz + vzr;
        // Calculate the launch position as the origin body position that points to destination
        double r = origin.getRadius() + spacecraft.getRadius() + LAUNCH_ELEVATION;
        double xr = r * direction.x / directionM;
        double yr = r * direction.y / directionM;
        double zr = r * direction.z / directionM;
        spacecraft.x = origin.x + xr;
        spacecraft.y = origin.y + yr;
        spacecraft.z = origin.z + zr;
        spacecraft.merged = false;
        launched = true;
    }

    private double distance(Position a, Position b) {
        double dabx = b.x - a.x;
        double daby = b.y - a.y;
        double dabz = b.z - a.z;
        return Math.sqrt(dabx * dabx + daby * daby + dabz * dabz);
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
        if (spacecraft.merged) {
            report.print("****************\nSpacecraft Land on date: %s, in: %s.\n Energy lost on landing: %e Joules\n++++++++++++++++", dateString(), spacecraft.mergedWith.name, spacecraft.kineticLost);
            return true;
        } else {
            return false;
        }
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

    /**
     * @return true if not newInitialConditionsLaunch
     */
    public boolean repeatInitialConditions() {
        return !(spacecraft.merged || newInitialConditionsLaunch);
    }
}
