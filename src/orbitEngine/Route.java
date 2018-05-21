/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import java.util.ArrayList;
import static orbitEngine.Engine.dateEpoch;
import static orbitEngine.Engine.dateString;
import userInterface.Command;
import userInterface.Report;

public class Route {

    private final Body spacecraft;
    private Body origin;
    private Body target;
    private Body landBody;
    private int spacecraftIndex;
    private int originIndex;
    private int targetIndex;
    private boolean spacecraftLand;
    private double kineticLost;

    private Vector3d spacecraftFail;
    private Vector3d targetFail;
    Vector3d direction;

    private double startTime;
    private final double stopTime;
    private final double stepTime;
    private final double overStepTimeFactorOnFarTargets;
    private final double startSpeed;
    private final double stopSpeed;
    private final double stepSpeed;
    private final double underStepSpeedFactorOnNearTargets;
    private final double overStepSpeedFactorOnFarTargets;

    private boolean launched;
    private double launchSpeed;
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

    public Route(Report report, Body spacecraft, Body origin, Body target, Command cmd) {
        this.report = report;
        this.spacecraft = spacecraft;
        this.origin = origin;
        this.target = target;
        spacecraftIndex = spacecraft.getIndex();
        originIndex = origin.getIndex();
        targetIndex = target.getIndex();
        this.startTime = cmd.MIN_LAUNCH_TIME;
        this.stopTime = cmd.MAX_LAUNCH_TIME;
        this.stepTime = cmd.STEP_LAUNCH_TIME;
        this.overStepTimeFactorOnFarTargets = cmd.OVER_STEP_TIME_FACTOR;
        this.startSpeed = cmd.MIN_SPEED;
        this.stopSpeed = cmd.MAX_SPEED;
        this.stepSpeed = cmd.STEP_SPEED;
        this.underStepSpeedFactorOnNearTargets = cmd.UNDER_STEP_SPEED_FACTOR;
        this.overStepSpeedFactorOnFarTargets = cmd.OVER_STEP_SPEED_FACTOR;
        this.OVERTAKE_DISTANCE_TOLERANCE = cmd.OVERTAKE_DISTANCE_TOLERANCE;
        MAX_OVERTAKE_DISTANCE = cmd.MAX_OVERTAKE_RADIUS * target.radius;
        MAX_OVERTAKE_DISTANCE_10 = MAX_OVERTAKE_DISTANCE * 10;
        MAX_OVERTAKE_DISTANCE_01 = MAX_OVERTAKE_DISTANCE / 10;

        this.STEPS_LIMIT_ON_CANDIDATE = cmd.STEPS_LIMIT_ON_CANDIDATE;
        this.LAUNCH_ELEVATION = cmd.LAUNCH_ELEVATION;
        launchSpeed = startSpeed;
        newInitialConditionsLaunch = true;
        farLaunch = false;
        nearLaunch = false;
        launched = false;
        nearLaunchOnSpeedScan = false;
        minTargetDistance = Double.MAX_VALUE;
        spacecraftLand = false;

        report.print_LandCSV(RouteCandidate.reportCSV_landHead());
        report.print_NearCSV(RouteCandidate.reportCSV_overtakeHead());
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

        RouteCandidate routeLand = new RouteCandidate(landBody.name, startTime, dateEpoch(), launchSpeed, spacecraft.mass, relativeLandSpeed, kineticLost);
        report.print_LandCSV(routeLand.reportCSV());
        routeLandings.add(routeLand);
        report.print("****************\nSpacecraft Land on date: %s, in: %s.\n Energy lost on landing: %e Joules\n++++++++++++++++", dateString(), landBody.name, kineticLost);
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
            RouteCandidate routeNear = new RouteCandidate(dSpacecraftToTarget, startTime, dateEpoch(), launchSpeed, spacecraft.mass);
            routeCandidate.add(routeNear);
            report.print_NearCSV(routeNear.reportCSV());
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
            launchSpeed += stepSpeed * overStepSpeedFactorOnFarTargets;
            sLog = "far-launch";
        } else if (nearLaunch) {
            nearLaunch = false;
            nearLaunchOnSpeedScan = true;
            launchSpeed += stepSpeed / underStepSpeedFactorOnNearTargets;
            sLog = "near-launch";
        } else {
            nearLaunchOnSpeedScan = true;
            launchSpeed += stepSpeed;
            sLog = "std-launch";
        }

        if (launchSpeed > stopSpeed) {
            if (nearLaunchOnSpeedScan) {
                nearLaunchOnSpeedScan = false;
                startTime += stepTime;
                sLog = "time-std-launch";
            } else {
                startTime += stepTime * overStepTimeFactorOnFarTargets;
                sLog = "time-far-launch";
            }
            launchSpeed = startSpeed;
            if (startTime > stopTime) {
                //@Todo report all routecandidates
                report.print("\n=====================\n-Correct Landings");
                for (RouteCandidate routeLand : routeLandings) {
                    report.print(" Landing: %s", routeLand.report());
                }
                report.print("\n~~~~~~~~~~~~~~~~~~~~~\n-Near Approachs");
                for (RouteCandidate routeNear : routeCandidate) {
                    report.print(" Overtake: %s", routeNear.report());
                }
                /*
                report.print_LandCSV(RouteCandidate.reportCSV_landHead());
                for (RouteCandidate routeLand : routeLandings) {
                    report.print_LandCSV(routeLand.reportCSV());
                }
                report.print_NearCSV(RouteCandidate.reportCSV_overtakeHead());
                for (RouteCandidate routeNear : routeCandidate) {
                    report.print_NearCSV(routeNear.reportCSV());
                }
                 */
                return false;
            }
        }
        report.print("- Next Launch time: %s (epoch: %.0f), with speed: %g node:'%s'", dateString(startTime), startTime, launchSpeed, sLog);
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
            // ^speedAdjust = (^targetFail - ^spacecraftFail) * (direction / distance(origin, targetFail) * 4??
            Vector3d speedAdjust = targetFail.minus(spacecraftFail).scale(3 * direction.magnitude() / distance(origin, targetFail));
            direction = direction.plus(speedAdjust);
        }
        newInitialConditionsLaunch = false;
        // Direction to the target and the 3 distance proyections
        double directionM = direction.magnitude();
        // Calculate the launch speed with the speed of the origin planet and the vector ^direction normalized;
        // ^relativeSpacecraftSpeed = ^direction * ( ||speedMagnitude|| / ||directionMagnitude||)
        // ^absoluteSpacecraftSpeed = ^relativeSpacecraftSpeed   + ^origin
        spacecraft.loadSpeed(direction.scale(launchSpeed / directionM).plus(origin.vx, origin.vy, origin.vz));

        // Calculate the launch position as the origin body position that points to destination
        // ^launchPosition = ^direction * ( launchRadius / ||directionMagnitude||) + ^origin
        double launchRadius = origin.getRadius() + LAUNCH_ELEVATION + spacecraft.getRadius();
        spacecraft.loadPosition(direction.scale(launchRadius / directionM).plus(origin));
        report.printLog("  Origin at x:%g, y:%g, z:%g", origin.x, origin.y, origin.z);

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
