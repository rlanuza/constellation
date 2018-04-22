/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import graphEngine.GraphScreen;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import userInterface.Command;
import userInterface.Parameter;
import userInterface.Report;

public class Engine {

    private final double stepTime;
    private static double seconds;
    private final Constellation constellation;
    private Route route;
    private GraphScreen screen;

    public Engine() {
        constellation = new Constellation();
        stepTime = Parameter.STEP_TIME;
    }

    private void resetEngine() {
        constellation.resetConstellation();
        constellation.resetGrConstellation();
        route.resetBodyValues(constellation);
        seconds = Parameter.START_EPOCH_TIME;
    }

    public void link(GraphScreen screen) {
        this.screen = screen;
        constellation.link(screen.getGraphConstellation());
        constellation.pushToGraphic();
    }

    static public String dateString() {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond((long) seconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/LL/dd-HH:mm:ss", Locale.ENGLISH);
        return "Date: " + dateTime.format(formatter);
    }

    static public String dateString(double seconds_time) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond((long) seconds_time, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/LL/dd-HH:mm:ss", Locale.ENGLISH);
        return "Date: " + dateTime.format(formatter);
    }

    private void run(long steepsPerPlot) {
        switch (Parameter.CALCULUS_METHOD) {
            case 0:
                for (int i = 0; i < steepsPerPlot; i++) {
                    constellation.step_basic(stepTime);
                }
                break;
            case 1:
                for (int i = 0; i < steepsPerPlot; i++) {
                    constellation.step_jerk(stepTime);
                }
                break;
            case 2:
                for (int i = 0; i < steepsPerPlot; i++) {
                    constellation.step_basic_Schwarzschild(stepTime);
                }
                break;
            case 3:
                for (int i = 0; i < steepsPerPlot; i++) {
                    constellation.step_jerk_Schwarzschild(stepTime);
                }
                break;
            default:
        }
        seconds += stepTime * steepsPerPlot;
        constellation.pushToGraphic();
    }

    public void runSimulation() {
        long simulationPlots = Parameter.SIMULATION_STEPS / Parameter.STEPS_PER_PLOT;
        for (long i = 0; i < simulationPlots; i++) {
            run(Parameter.STEPS_PER_PLOT);
            screen.updateConstellation();
        }
    }

    /**
     * @return true when the spacecraft land or lost the target
     */
    private boolean runRoute(long steepsPerPlot, Report report) {
        for (int i = 0; i < steepsPerPlot; i++) {
            switch (Parameter.CALCULUS_METHOD) {
                case 0:
                    constellation.step_basic(stepTime);
                    break;
                case 1:
                    constellation.step_jerk(stepTime);
                    break;
                case 2:
                    constellation.step_basic_Schwarzschild(stepTime);
                    break;
                case 3:
                    constellation.step_jerk_Schwarzschild(stepTime);
                    break;
                default:
            }
            seconds += stepTime;
            if (route.isLaunched()) {
                if (route.spacecraftLand()) {
                    report.print("Spacecraft Land on time '%s' in: %s. Energy lost on landing: %eJoules", dateString(), route.spacecraftLandBody().name, route.getSpacecraft().kineticLost);
                    //System.out.printf("Kinetic lost on %s generation: %e\n", name, b2.kineticLost);
                    constellation.pushToGraphic();
                    return true;
                } else if (route.overtaking()) {
                    report.print(" overtaking");
                    constellation.pushToGraphic();
                    return true;
                }
            } else {
                if (route.timeToLaunch(seconds)) {
                    route.launchToNextTarget();
                    constellation.addRocket(route); //@Todo Check if this place is correect to launch teh rocket
                }
            }
        }
        constellation.pushToGraphic();
        return false;
    }

    public void runSimulationTravel(Command cmd, Report report) {
        setRoute(cmd, report);
        report.print("+++++++++++++++++++++\nNew simulation");
        long simulationPlots = Parameter.SIMULATION_STEPS / Parameter.STEPS_PER_PLOT;
        do {
            do {
                resetEngine();
                for (long i = 0; i < simulationPlots; i++) {
                    if (runRoute(Parameter.STEPS_PER_PLOT, report)) {
                        screen.updateConstellation();
                        break;
                    }
                    screen.updateConstellation();
                }
            } while (route.repeatInitialConditions());
        } while (route.nextLaunch(cmd.ITERATE_SPEED_FIRST));
        report.dump();
    }

    private void setRoute(Command cmd, Report report) {
        Body spacecraft = new Body(cmd.NAME, cmd.MASS, cmd.RADIUS, cmd.COLOR);
        Body origin = constellation.getBody(cmd.ORIGIN);
        Body target = constellation.getBody(cmd.TARGET);
        Body star = constellation.getBody(cmd.STAR);
        if ((origin == null) || (target == null)) {
            report.print("Error, the origin '%s' or the target '%s' is not in our constellation", cmd.ORIGIN, cmd.TARGET);
            System.exit(1);
        }
        route = new Route(report, spacecraft, origin, target, star,
                cmd.MIN_LAUNCH_TIME, cmd.MAX_LAUNCH_TIME, cmd.STEP_LAUNCH_TIME,
                cmd.MIN_SPEED, cmd.MAX_SPEED, cmd.STEP_SPEED,
                cmd.LAUNCH_ELEVATION,
                cmd.OVERTAKE_DISTANCE_TOLERANCE,
                cmd.MAX_OVERTAKE_DISTANCE,
                cmd.STEPS_LIMIT_ON_CANDIDATE);
    }
}
