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

    private ArrayList<RouteCandidate> routeCandidate = new ArrayList<>();
    private ArrayList<RouteCandidate> routeLandings = new ArrayList<>();

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
        // @Todo Add and calculate collision angle
        double relativeLandSpeed = new Vector3d(spacecraft.vx, spacecraft.vy, spacecraft.vz).minus(landBody.vx, landBody.vy, landBody.vz).magnitude();
        String arrivalDate = dateString();
        routeLandings.add(new RouteCandidate(0, startTime, speed, spacecraft.mass, relativeLandSpeed, kineticLost, arrivalDate));
        report.print("****************\nSpacecraft Land on date: %s, in: %s.\n Energy lost on landing: %e Joules\n++++++++++++++++", arrivalDate, landBody.name, kineticLost);
    }

    /**
     * Check if the target has been overtaken
     *
     * @Todo Think if a heuristic by independent axis can help
     */
    boolean overtaking() {
        double dSpacecraftToTarget = Constellation.dist[targetIndex][spacecraftIndex];

        // Aproaching to the target
        if (dSpacecraftToTarget < minTargetDistance) {  // A new minimum distance --> Continue this
            minTargetDistance = dSpacecraftToTarget;
            return false;
        } else if (dSpacecraftToTarget < (minTargetDistance + OVERTAKE_DISTANCE_TOLERANCE)) {
            // Distance growing but still into tolerance. Probably getting worse
            return false;
        }
        // Overtaking the target:
        // Heuristic a) the distance must be near than a limit else reject the iteration
        if (dSpacecraftToTarget > MAX_OVERTAKE_DISTANCE_10) {
            report.print(" -> End test iteration: very far target overtaking. (%6.1f-radius) [distance: %e > %e :10*MAX_OVERTAKE_DISTANCE].",
                    dSpacecraftToTarget / target.radius, dSpacecraftToTarget, MAX_OVERTAKE_DISTANCE_10);
            farLaunch = true;
            newInitialConditionsLaunch = true;
            return true;
        }
        if (dSpacecraftToTarget > MAX_OVERTAKE_DISTANCE) {
            report.print(" -> End test iteration: far target overtaking. (%6.1f-radius) [distance: %e > %e :MAX_OVERTAKE_DISTANCE].",
                    dSpacecraftToTarget / target.radius, dSpacecraftToTarget, MAX_OVERTAKE_DISTANCE);
            newInitialConditionsLaunch = true;
            return true;
        }
        // Heuristic b) if the distance is really short the iteration steps will be reduced with a nea-launch query
        String sLog1;
        if (dSpacecraftToTarget < MAX_OVERTAKE_DISTANCE_01) {
            nearLaunch = true;
            sLog1 = " -> Near overtaking.";
        } else {
            sLog1 = " -> Far overtaking. ";
        }
        String sLog2 = String.format("distance:%.3e (%6.1f-radius) [dx=% .3e, dy=% .3e, dz=% .3e].",
                dSpacecraftToTarget, dSpacecraftToTarget / target.radius, target.x - spacecraft.x, target.y - spacecraft.y, target.z - spacecraft.z);
        // Heuristic c) Calculate a new taget based on the error compensation with a sinple iteration counter limit
        if (stepsLimitOnCandidate > 0) {    // Prepare a new iteration modifying the target with the last error
            stepsLimitOnCandidate--;
            // Store the position of the Spacecraft and Target on overtaking time to let us plan a new fine adjust launch
            spacecraftFail = new Vector3d(spacecraft);
            targetFail = new Vector3d(target);
            report.printLog("%s %s New temptative [%3d of %3d] with speed vector correction", sLog1, sLog2,
                    STEPS_LIMIT_ON_CANDIDATE - stepsLimitOnCandidate, STEPS_LIMIT_ON_CANDIDATE);                //@Todo this NOT for release
            return true;
        } else {
            report.print("%s %s The STEPS_LIMIT_ON_CANDIDATE = %d temptatives were consumed", sLog1, sLog2, STEPS_LIMIT_ON_CANDIDATE);
            routeCandidate.add(new RouteCandidate(dSpacecraftToTarget, startTime, speed, spacecraft.mass, 0, 0, dateString()));
            newInitialConditionsLaunch = true;
            return true;
        }
    }

    /**
     * Program next launch conditions
     *
     * @return true until no new conditions programmed
     */
    public boolean nextLaunch() {
        String sLog;
        if (farLaunch) {
            farLaunch = false;
            speed += stepSpeed * 10;
            sLog = "far-launch";
        } else if (nearLaunch) {
            nearLaunch = false;
            nearLaunchOnSpeedScan = true;
            speed += stepSpeed / 10;
            sLog = "near-launch";
        } else {
            nearLaunchOnSpeedScan = true;
            speed += stepSpeed;
            sLog = "std-launch";
        }

        if (speed > stopSpeed) {
            if (nearLaunchOnSpeedScan) {
                nearLaunchOnSpeedScan = false;
                startTime += stepTime;
                sLog = "time-std-launch";
            } else {
                startTime += stepTime * 10;
                sLog = "time-far-launch";
            }
            speed = startSpeed;
            if (startTime > stopTime) {
                //@Todo report all routecandidates
                report.print("\n=====================\n-Correct Landings");
                for (RouteCandidate routeLand : routeLandings) {
                    report.print(" Landing: %s", routeLand.report());
                }
                report.print("\n~~~~~~~~~~~~~~~~~~~~~\n-Near Approachs");
                for (RouteCandidate routeNear : routeCandidate) {
                    report.print(" Landing: %s", routeNear.report());
                }
                return false;
            }
        }
        report.print("- Next Launch time: %s (epoch: %.0f), with speed: %g node:'%s'", dateString(startTime), startTime, speed, sLog);
        newInitialConditionsLaunch = true;
        return true;
    }

    /**
     * Time to launch check
     */
    boolean timeToLaunch(double seconds) {
        return (seconds >= startTime);
    }

    /**
     * Launch to the next target iteration point. We will use this to calculate the error if we miss the target and adjust next launch
     */
    public void launchToNextTarget() {
        minTargetDistance = Double.MAX_VALUE;
        if (newInitialConditionsLaunch) {
            // Old approach: correction.reset();
            stepsLimitOnCandidate = STEPS_LIMIT_ON_CANDIDATE;
            // Straight launch using the planet speed vector
            direction = new Vector3d(origin.vx, origin.vy, origin.vz);
        } else {
            // ^speedAdjust = (^targetFail - ^spacecraftFail) * (direction / distance(origin, targetFail) * 4??
            Vector3d speedAdjust = targetFail.minus(spacecraftFail).scale(3 * direction.magnitude() / distance(origin, targetFail));
            direction = direction.plus(speedAdjust);
        }
        newInitialConditionsLaunch = false;
        // Direction to the target and the 3 distance proyections
        double directionM = direction.magnitude();
        // Calculate the launch speed with the original speed and the vector ^direction normalized;
        // ^relativeSpacecraftSpeed = ^direction * ( ||speedMagnitude|| / ||directionMagnitude||)
        // ^absoluteSpacecraftSpeed = ^relativeSpacecraftSpeed   + ^origin
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
