package orbitEngine;

import java.util.ArrayList;
import static orbitEngine.Engine.dateEpoch;
import static orbitEngine.Engine.dateString;
import userInterface.Command;
import userInterface.Report;

/**
 * Represents the essential information methods that characterize a route.
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class Route {

    /**
     * Spacecraft body.
     */
    private final Body spacecraft;
    /**
     * Spacecraft origin body.
     */
    private Body origin;
    /**
     * Spacecraft target body.
     */
    private Body target;
    /**
     * Spacecraft real land body.
     */
    private Body landBody;
    /**
     * Spacecraft body index.
     */
    private final int spacecraftIndex;
    /**
     * Spacecraft origin body index.
     */
    private final int originIndex;
    /**
     * Spacecraft target body index.
     */
    private final int targetIndex;
    /**
     * Confirm that spacecraft is landed.
     */
    private boolean spacecraftLand;
    /**
     * Spacecraft position on target fail.
     */
    private Vector3d spacecraftFail = null;
    /**
     * Target position on target fail.
     */
    private Vector3d targetFail = null;
    /**
     * Launch vector.
     */
    private Vector3d launchVector;
    /**
     * Start or initial launch time.
     */
    private double startTime;
    /**
     * Stop or maximum launch time.
     */
    private final double stopTime;
    /**
     * Step launch time.
     */
    private final double stepTime;
    /**
     * Over-step coarse time optimization factor used when lasts target land fails with far distances.
     */
    private final double overStepTimeFactorOnFarTargets;
    /**
     * Start or initial launch speed.
     */
    private final double startSpeed;
    /**
     * Start or final launch speed.
     */
    private final double stopSpeed;
    /**
     * Steep launch speed.
     */
    private final double stepSpeed;
    /**
     * Under-step fine speed optimization factor used when lasts target land fails are in near distances.
     */
    private final double underStepSpeedFactorOnNearTargets;
    /**
     * Over-step coarse speed optimization factor used when lasts target land fails are in far distances.
     */
    private final double overStepSpeedFactorOnFarTargets;
    /**
     * Confirm that spacecraft is launched.
     */
    private boolean launched;
    /**
     * Current launch speed.
     */
    private double launchSpeed;
    /**
     * Minimum distance to target in current route.
     */
    private double minTargetDistance;
    /**
     * Request new initial conditions for the new launch, this means no more iterations.
     */
    private boolean newInitialConditionsLaunch;
    /**
     * Request a far launch conditions for the new launch, this means our routes are far from target.
     */
    private boolean farLaunch;
    /**
     * Request a near launch conditions for the new launch, this means our routes are near to the target.
     */
    private boolean nearLaunch;
    /**
     * Boolean used to detect if a overStepTimeFactorOnFarTargets will be used or not.
     */
    private boolean nearLaunchOnSpeedScan;
    /**
     * Counter that limits the iterations on the same initial planet conditions with different launch angle adjust.
     */
    private int stepsLimitOnCandidate;
    /**
     * Lower distance to the target obtained in the current iteration of the same initial condition.
     */
    private double lowDistanceSpacecraftToTarget;
    /**
     * Steps accepted to iterate on a candidate route with same initial planet conditions with different launch angle adjust.
     */
    private final int STEPS_LIMIT_ON_CANDIDATE;
    /**
     * Launcher elevation or distance to ground of origin to avoid collision detection on launch time.
     */
    private final double LAUNCH_ELEVATION;
    /**
     * Tolerance that we accept to continue route iteration calculus when the rocket seems to get distance from a failed target.
     */
    private final double OVERTAKE_DISTANCE_TOLERANCE;
    /**
     * Max overtake distance. When it is exceeded the iterations with different launch angle are finished.
     */
    private final double MAX_OVERTAKE_DISTANCE;
    /**
     * Max overtake distance with a factor 10. this program a far launch optimization because the target is difficult.
     */
    private final double MAX_OVERTAKE_DISTANCE_10;
    /**
     * Max overtake distance with a factor 1/10. this program a near launch optimization because the target is probable.
     */
    private final double MAX_OVERTAKE_DISTANCE_01;
    /**
     * Launch correction factor to increase the iteration adjust.
     */
    private final double LAUNCH_CORRECTION_FACTOR;
    /**
     * Link to class used to log reports.
     */
    private final Report report;
    /**
     * List of route candidates discarded. Potential targets with some extra adjust.
     */
    private final ArrayList<RouteCandidateReport> routeCandidate = new ArrayList<>(0);
    /**
     * List of route landings.
     */
    private final ArrayList<RouteCandidateReport> routeLandings = new ArrayList<>(0);

    /**
     * Create a new route
     *
     * @param report link to report class.
     * @param spacecraft reference to the spacecraft.
     * @param origin reference to the origin planet to launch the spacecraft.
     * @param target reference to the target planet to be reach by the spacecraft.
     * @param cmd reference to the command input to get the route calculus requirements.
     */
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
        OVERTAKE_DISTANCE_TOLERANCE = cmd.OVERTAKE_TOLERANCE_RADIUS * target.radius;
        MAX_OVERTAKE_DISTANCE = cmd.MAX_OVERTAKE_RADIUS * target.radius;
        MAX_OVERTAKE_DISTANCE_10 = MAX_OVERTAKE_DISTANCE * 10;
        MAX_OVERTAKE_DISTANCE_01 = MAX_OVERTAKE_DISTANCE / 10;
        this.STEPS_LIMIT_ON_CANDIDATE = cmd.STEPS_LIMIT_ON_CANDIDATE;
        this.LAUNCH_CORRECTION_FACTOR = cmd.LAUNCH_CORRECTION_FACTOR;
        this.LAUNCH_ELEVATION = cmd.LAUNCH_ELEVATION;
        launchSpeed = startSpeed;
        newInitialConditionsLaunch = true;
        farLaunch = false;
        nearLaunch = false;
        launched = false;
        nearLaunchOnSpeedScan = false;
        minTargetDistance = Double.MAX_VALUE;
        spacecraftLand = false;

        RouteCandidateReport.setFormat(cmd);
        report.print_LandCSV(RouteCandidateReport.reportCSV_landHead());
        report.print_NearCSV(RouteCandidateReport.reportCSV_overtakeHead());
    }

    /**
     * Reset Origin and destination bodies to start a new calculus
     *
     * @param constellation constellation state to recover.
     */
    void resetBodyValues(Constellation constellation) {
        origin = constellation.getBody(originIndex);
        target = constellation.getBody(targetIndex);
    }

    /**
     * Merge spacecraft and a planet bodies
     *
     * @param b1 fist body
     * @param b2 second body
     * @param kineticLost kinetic lost
     */
    void mergeBodies(Body b1, Body b2, double kineticLost) {
        if (b2 == spacecraft) {
            landBody = b1;
        } else if (b1 == spacecraft) {
            landBody = b2;
        } else {
            return;
        }
        spacecraftLand = true;
        // @Todo Can be improved with collision angle
        double relativeLandSpeed = new Vector3d(spacecraft.vx, spacecraft.vy, spacecraft.vz).minus(landBody.vx, landBody.vy, landBody.vz).magnitude();

        RouteCandidateReport routeLand = new RouteCandidateReport(landBody.name, startTime, dateEpoch(), launchSpeed, spacecraft.mass, relativeLandSpeed, launchVector);
        report.print_LandCSV(routeLand.reportCSV());
        routeLandings.add(routeLand);
        report.print("****************\nSpacecraft Land on date: %s, in: %s.\n Energy lost on landing: %e Joules\n++++++++++++++++", dateString(), landBody.name, kineticLost);
    }

    /**
     * Check if the target has been overtaken
     *
     * @return true when a valid overtaken is detected
     */
    boolean overtaking() {
        double dSpacecraftToTarget = Constellation.dist[targetIndex][spacecraftIndex];

        // Approaching to the target
        if (dSpacecraftToTarget < minTargetDistance) {  // A new minimum distance --> Continue this
            minTargetDistance = dSpacecraftToTarget;
            return false;
        }
        if (spacecraftFail == null) {
            spacecraftFail = new Vector3d(spacecraft);
            targetFail = new Vector3d(target);
        }
        if (dSpacecraftToTarget < (minTargetDistance + OVERTAKE_DISTANCE_TOLERANCE)) {
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
        } else if (dSpacecraftToTarget > MAX_OVERTAKE_DISTANCE) {
            report.print(" -> End test iteration: far target overtaking. (%6.1f-radius) [distance: %e > %e :MAX_OVERTAKE_DISTANCE].",
                    dSpacecraftToTarget / target.radius, dSpacecraftToTarget, MAX_OVERTAKE_DISTANCE);
            newInitialConditionsLaunch = true;
        } else {
            // Heuristic b) if the distance is really short the iteration steps will be reduced with a nea-launch query
            String sLog1;
            if (dSpacecraftToTarget < MAX_OVERTAKE_DISTANCE_01) {
                nearLaunch = true;
                sLog1 = " -> Near overtaking.";
            } else {
                sLog1 = " -> Far overtaking. ";
            }
            if (dSpacecraftToTarget < lowDistanceSpacecraftToTarget) {
                lowDistanceSpacecraftToTarget = dSpacecraftToTarget;
            }
            String sLog2 = String.format("distance:%.3e (%6.1f-radius) [dx=% .3e, dy=% .3e, dz=% .3e].",
                    dSpacecraftToTarget, dSpacecraftToTarget / target.radius, target.x - spacecraft.x, target.y - spacecraft.y, target.z - spacecraft.z);
            // Heuristic c) Calculate a new target based on the error compensation with a simple iteration counter limit
            if (stepsLimitOnCandidate > 0) {    // Prepare a new iteration modifying the target with the last error
                if (dSpacecraftToTarget < (2 * lowDistanceSpacecraftToTarget)) {
                    stepsLimitOnCandidate--;
                    report.printLog("%s %s New tentative [%3d of %3d] with speed vector correction", sLog1, sLog2,
                            STEPS_LIMIT_ON_CANDIDATE - stepsLimitOnCandidate, STEPS_LIMIT_ON_CANDIDATE);
                    return true;
                } else {
                    report.print("%s %s No more tentative because the correction heuristic is failing", sLog1, sLog2);
                }
            } else {
                report.print("%s %s The STEPS_LIMIT_ON_CANDIDATE = %d tentative were consumed", sLog1, sLog2, STEPS_LIMIT_ON_CANDIDATE);
            }
            RouteCandidateReport routeNear = new RouteCandidateReport(dSpacecraftToTarget, startTime, dateEpoch(), launchSpeed, spacecraft.mass);
            routeCandidate.add(routeNear);
            report.print_NearCSV(routeNear.reportCSV());
            newInitialConditionsLaunch = true;
        }
        return true;
    }

    /**
     * Program next launch conditions depending the next iteration step parameters and last overtaken measures. Also detect the end of the
     * program and order the conclusion report
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
                // Report all route candidates
                report.print("\n=====================\n-Correct Landings");
                for (RouteCandidateReport routeLand : routeLandings) {
                    report.print(" Landing: %s", routeLand.report());
                }
                report.print("\n~~~~~~~~~~~~~~~~~~~~~\n-Near Approaches");
                for (RouteCandidateReport routeNear : routeCandidate) {
                    report.print(" Overtake: %s", routeNear.report());
                }
                return false;
            }
        }
        report.print("- Next Launch time: %s (epoch: %.0f), with speed: %g node:'%s'",
                dateString(startTime), startTime, launchSpeed, sLog);
        newInitialConditionsLaunch = true;
        return true;
    }

    /**
     * Time to launch check
     *
     * @param seconds time in seconds to detect the launch time
     * @return true when launch time
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
            stepsLimitOnCandidate = STEPS_LIMIT_ON_CANDIDATE;
            // Straight launch using the planet speed vector
            launchVector = new Vector3d(origin.vx, origin.vy, origin.vz);
        } else {
            // ^directionCorrection = (^targetFail - ^spacecraftFail) * (||launchVector|| / distance(origin, targetFail) * LAUNCH_CORRECTION_FACTOR??
            Vector3d directionCorrection = targetFail
                    .minus(spacecraftFail)
                    .scale(LAUNCH_CORRECTION_FACTOR * launchSpeed
                            / distance(origin, targetFail));
            launchVector = launchVector.plus(directionCorrection);
        }
        newInitialConditionsLaunch = false;
        // Direction to the target and the 3 distance projections
        launchVector = launchVector.scale(launchSpeed / launchVector.magnitude());
        // Calculate the launch speed with the speed of the origin planet and the vector ^launchVector normalized;
        // ^relativeSpacecraftSpeed = ^launchVector * (||speedMagnitude|| / ||directionMagnitude||)
        // ^absoluteSpacecraftSpeed = ^relativeSpacecraftSpeed + ^origin
        spacecraft.loadSpeed(launchVector.plus(origin.vx, origin.vy, origin.vz));

        // Calculate the launch position as the origin body position that points to destination
        // ^launchPosition = ^launchVector *(launchRadius / ||directionMagnitude||) + ^origin
        double launchRadius = origin.getRadius() + LAUNCH_ELEVATION + spacecraft.getRadius();
        spacecraft.loadPosition(launchVector.scale(launchRadius / launchSpeed).plus(origin));
        report.printLog("  Origin at x:%g, y:%g, z:%g", origin.x, origin.y, origin.z);

        // Store the last launch parameters
        lowDistanceSpacecraftToTarget = Double.MAX_VALUE;
        spacecraftFail = null;
        targetFail = null;
        spacecraftLand = false;
        launched = true;
    }

    /**
     * Distance between a body and a vector3d point
     *
     * @param a Body to get position
     * @param b Vector3d point
     * @return distance between both elements. Unit m.
     */
    private double distance(Body a, Vector3d b) {
        double dabx = b.x - a.x;
        double daby = b.y - a.y;
        double dabz = b.z - a.z;
        return Math.sqrt(dabx * dabx + daby * daby + dabz * dabz);
    }

    /**
     * Route spacecraft
     *
     * @return the spacecraft reference
     */
    public Body getSpacecraft() {
        return spacecraft;
    }

    /**
     * Landing confirmation
     *
     * @return true if spacecraft land
     */
    public boolean spacecraftLand() {
        return spacecraftLand;
    }

    /**
     * Launching confirmation
     *
     * @return true if the rocket is launched
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
