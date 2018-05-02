/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import java.util.ArrayList;
import static orbitEngine.Engine.dateString;
import userInterface.Report;

public class Route {

    private final Body spacecraft;
    private Body origin;
    private Body target;
    private Body star;
    private Body landBody;
    private int spacecraftIndex;
    private int originIndex;
    private int targetIndex;
    private int starIndex;
    private boolean spacecraftLand;
    private double kineticLost;

    private Vector3d spacecraftFail;
    private Vector3d targetFail;
    Vector3d direction;

    private double startTime;
    private final double stopTime;
    private final double stepTime;
    private final double startSpeed;
    private final double stopSpeed;
    private final double stepSpeed;
    private boolean launched;
    private double speed;
    private double minTargetDistance;
    private boolean newInitialConditionsLaunch;
    private boolean farLaunch;
    private boolean nearLaunch;
    private boolean nearLaunchOnSpeedScan;
    private int stepsLimitOnCandidate;

    private final int STEPS_LIMIT_ON_CANDIDATE;
    private final double LAUNCH_ELEVATION;
    private final double OVERTAKE_DISTANCE_TOLERANCE;
    private final double MAX_OVERTAKE_DISTANCE;
    private final double MAX_OVERTAKE_DISTANCE_10;
    private final double MAX_OVERTAKE_DISTANCE_01;
    private Report report;

    private ArrayList<RouteCandidate> routecandidates = new ArrayList<>();

    public Route(Report report, Body spacecraft, Body origin, Body target, Body star,
            double startTime, double stopTime, double stepTime,
            double startSpeed, double stopSpeed, double stepSpeed,
            double LAUNCH_ELEVATION,
            double OVERTAKE_DISTANCE_TOLERANCE,
            double MAX_OVERTAKE_RADIUS,
            int STEPS_LIMIT_ON_CANDIDATE) {
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
        MAX_OVERTAKE_DISTANCE = MAX_OVERTAKE_RADIUS * target.radius;
        MAX_OVERTAKE_DISTANCE_10 = MAX_OVERTAKE_DISTANCE * 10;
        MAX_OVERTAKE_DISTANCE_01 = MAX_OVERTAKE_DISTANCE / 10;

        this.STEPS_LIMIT_ON_CANDIDATE = STEPS_LIMIT_ON_CANDIDATE;
        this.LAUNCH_ELEVATION = LAUNCH_ELEVATION;
        speed = startSpeed;
        newInitialConditionsLaunch = true;
        farLaunch = false;
        nearLaunch = false;
        launched = false;
        nearLaunchOnSpeedScan = false;
        minTargetDistance = Double.MAX_VALUE;
        spacecraftLand = false;

    }

    /**
     * Reset Origin and destination bodies for a new calculus
     */
    void resetBodyValues(Constellation constellation) {
        origin = constellation.getBody(originIndex);
        target = constellation.getBody(targetIndex);
    }

    void mergeBodies(Body b1, Body b2, double kineticLost) {
        if (b2 == spacecraft) {
            landBody = b1;
        } else if (b1 == spacecraft) {
            landBody = b2;
        } else {
            return;
        }
        spacecraftLand = true;
        this.kineticLost = kineticLost;
        routecandidates.add(0, new RouteCandidate(true, 0, startTime, speed, 1, kineticLost));
    }

    /**
     * Check if the target has been overtaken
     */
    boolean overtaking() {
        // @Todo-DONE Heuristic a changes 1) MAX_OVERTAKE_DISTANCE = F(Radius planet).
        // @Todo-DONE Heuristic b changes 2) Accept some (dStartToTarget > dStartToSpacecraft) to fine adjust
        // @Todo-DONE Heuristic changes 3) Correct time and energy delta if near
        // @Todo Heuristic changes 4) Correct launch speed with angle to avoid lost energy sum with planet
        double dSpacecraftToTarget = Constellation.dist[targetIndex][spacecraftIndex];

        if (dSpacecraftToTarget < minTargetDistance) {  // A new minimum distance --> Continue this
            minTargetDistance = dSpacecraftToTarget;
            return false;
        } else if (dSpacecraftToTarget > (minTargetDistance + OVERTAKE_DISTANCE_TOLERANCE)) {     // A clear overtaking
            // Heuristic a) the distance must be near than a limit else reject the iteration
            if (dSpacecraftToTarget > MAX_OVERTAKE_DISTANCE_10) {
                report.print(" -> overtaking: distance to target %e > %e [10*MAX_OVERTAKE_DISTANCE] --> Far, iteration end",
                        dSpacecraftToTarget, MAX_OVERTAKE_DISTANCE_10);
                farLaunch = true;
                newInitialConditionsLaunch = true;
                return true;
            }
            if (dSpacecraftToTarget > MAX_OVERTAKE_DISTANCE) {
                report.print(" -> overtaking: distance to target %e > %e [MAX_OVERTAKE_DISTANCE] --> Iteration end",
                        dSpacecraftToTarget, MAX_OVERTAKE_DISTANCE);
                newInitialConditionsLaunch = true;
                return true;
            }
            String sLog;
            if (dSpacecraftToTarget < MAX_OVERTAKE_DISTANCE_01) {
                nearLaunch = true;
                sLog = String.format(" -> overtaking: distance:%e [dx=%e, dy=%e, dz=%e]. [NEAR]", dSpacecraftToTarget, target.x - spacecraft.x, target.y - spacecraft.y, target.z - spacecraft.z);
            } else {
                sLog = String.format(" -> overtaking: distance:%e [dx=%e, dy=%e, dz=%e].", dSpacecraftToTarget, target.x - spacecraft.x, target.y - spacecraft.y, target.z - spacecraft.z);
            }
            // Heuristic b) the distance to STAR must be far than Target else reject the iteration
            // @Todo Heuristic check if a adaptation is possible or finally deprecate the "Heuristic b" filter
            /*
            double dStartToTarget = (targetIndex > starIndex) ? Constellation.dist[starIndex][targetIndex] : Constellation.dist[targetIndex][starIndex];
            double dStartToSpacecraft = (spacecraftIndex > starIndex) ? Constellation.dist[starIndex][spacecraftIndex] : Constellation.dist[spacecraftIndex][starIndex];
            if (dStartToTarget > dStartToSpacecraft) {
                report.print(" -> overtaking: As the star distance to target %e > %e, the rocket distance to target. This launch iteration is dismissed",
                        dStartToTarget, dStartToSpacecraft);
                newInitialConditionsLaunch = true;
                return true;
            }
             */

            // Heuristic c) Calculate a new taget based on the error compensation with a sinple iteration counter limit
            // Prepare a new iteration if the conditions are good modifying the target
            if (stepsLimitOnCandidate > 0) {
                stepsLimitOnCandidate--;
                // Store the position of the Spacecraft and Target on overtaking time to let us plan a new fine adjust launch
                spacecraftFail = new Vector3d(spacecraft);
                targetFail = new Vector3d(target);

                //@Todo this for release report.printLog("%s New temptative [%d of %d] with speed vector correction", sLog, STEPS_LIMIT_ON_CANDIDATE - stepsLimitOnCandidate, STEPS_LIMIT_ON_CANDIDATE);
                report.print("%s New temptative [%d of %d] with speed vector correction", sLog, STEPS_LIMIT_ON_CANDIDATE - stepsLimitOnCandidate, STEPS_LIMIT_ON_CANDIDATE);                //@Todo this NOT for release
                return true;
            } else {
                report.print("%s The STEPS_LIMIT_ON_CANDIDATE = %d temptatives were consumed", sLog, STEPS_LIMIT_ON_CANDIDATE);
                routecandidates.add(new RouteCandidate(false, dSpacecraftToTarget, startTime, speed, 2, 0));
                newInitialConditionsLaunch = true;
                return true;
            }
            // @Todo Heuristic check if a adaptation is possible or finally deprecate the "Heuristic b" filter
            // @Todo Heuristic by independent axis

        } else {    // A distance in tolerance, but probably getting worse
            return false;
        }
    }

    /**
     * Program next launch conditions
     *
     * @return true until no new conditions programmed
     */
    public boolean nextLaunch() {
        if (farLaunch) {
            farLaunch = false;
            speed += stepSpeed * 10;
        } else if (nearLaunch) {
            nearLaunch = false;
            nearLaunchOnSpeedScan = true;
            speed += stepSpeed / 10;
        } else {
            nearLaunchOnSpeedScan = true;
            speed += stepSpeed;
        }

        if (speed > stopSpeed) {
            if (nearLaunchOnSpeedScan) {
                nearLaunchOnSpeedScan = false;
                startTime += stepTime;
            } else {
                startTime += stepTime * 10;
            }
            speed = startSpeed;
            if (startTime > stopTime) {
                return false;
            }
        }
        report.print("- Next Launch time: %s (epoch: %.0f), with speed: %g", dateString(startTime), startTime, speed);
        newInitialConditionsLaunch = true;
        return true;
    }

    /**
     * Time to launch check
     */
    boolean timeToLaunch(double seconds) {
        return (seconds >= startTime);
    }

    boolean timeToSave(double seconds) {
        return (seconds >= startTime);
    }

    /**
     * Launch to the next target iteration point. We will use this to calculate
     * the error if we miss the target and adjust next launch
     */
    public void launchToNextTarget() {
        minTargetDistance = Double.MAX_VALUE;
        if (newInitialConditionsLaunch) {
            // Old approach: correction.reset();
            stepsLimitOnCandidate = STEPS_LIMIT_ON_CANDIDATE;
            // Straight launch using the planet speed vector
            direction = new Vector3d(origin.vx, origin.vy, origin.vz);
        } else {
            // ^speedAdjust = (^targetFail - ^spacecraftFail) * (direction / distance(origin, targetFail)
            Vector3d speedAdjust = targetFail.minus(spacecraftFail).scale(direction.magnitude() / distance(origin, targetFail));
            direction = direction.plus(speedAdjust);
        }
        newInitialConditionsLaunch = false;
        // Direction to the target and the 3 distance proyections
        double directionM = direction.magnitude();
        // Calculate the launch speed with the original speed and the vector ^direction normalized;
        // ^launchSpeed = ^direction * ( ||speedMagnitude|| / ||directionMagnitude||)
        spacecraft.loadSpeed(direction.scale(speed / directionM).plus(origin.vx, origin.vy, origin.vz));

        // Calculate the launch position as the origin body position that points to destination
        // ^launchPosition = ^direction * ( launchRadius / ||directionMagnitude||) + ^origin
        double launchRadius = origin.getRadius() + LAUNCH_ELEVATION + spacecraft.getRadius();
        spacecraft.loadPosition(direction.scale(launchRadius / directionM).plus(origin));

        spacecraftLand = false;
        launched = true;
    }

    private double distance(Body a, Vector3d b) {
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
        if (spacecraftLand) {
            report.print("****************\nSpacecraft Land on date: %s, in: %s.\n Energy lost on landing: %e Joules\n++++++++++++++++", dateString(), landBody.name, kineticLost);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return if the rocket is launched
     */
    public boolean isLaunched() {
        return this.launched;
    }

    /**
     * Set launched to false
     */
    public void clearLaunched() {
        this.launched = false;
    }

    /**
     * @return true if not newInitialConditionsLaunch
     */
    public boolean repeatInitialConditions() {
        return !(spacecraftLand || newInitialConditionsLaunch);
    }
}
