package orbitEngine;

import graphEngine.GraphScreen;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import userInterface.Command;
import userInterface.Parameter;
import userInterface.Report;

/**
 * Represents the essential information that characterize a gravitational engine.
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class Engine {

    /**
     * Time step of each calculus approximation. Units seconds.
     */
    private final double stepTime;
    /**
     * Current time of simulation. Units seconds.
     */
    private static double seconds;
    /**
     * Instant when save the constellation to recover later. Units seconds.
     */
    private static double secondsToRecover;
    /**
     * Confirm that constellation is saved.
     */
    private static boolean secondsToRecoverStored;
    /**
     * Constellation to move.
     */
    private final Constellation constellation;
    /**
     * Route to use in the launch.
     */
    private Route route;
    /**
     * Link to the related graphical screen class.
     */
    private GraphScreen screen;

    /**
     * Create a new engine.
     */
    public Engine() {
        constellation = new Constellation();
        stepTime = Parameter.STEP_TIME;
        seconds = Parameter.START_EPOCH_TIME;
    }

    /**
     * Save the current engine state.
     */
    private void saveEngine() {
        constellation.saveConstellation();
        secondsToRecover = seconds;
        secondsToRecoverStored = true;
    }

    /**
     * Recover the current last engine state saved.
     */
    private void recoverEngine() {
        constellation.recoverConstellation();
        constellation.resetGrConstellation();
        route.resetBodyValues(constellation);
        seconds = secondsToRecover;
        secondsToRecoverStored = false;
    }

    /**
     * Links the engine with the graphical screen manager
     *
     * @param screen graphic screen reference.
     */
    public void link(GraphScreen screen) {
        this.screen = screen;
        constellation.link(screen.getGraphConstellation());
        constellation.pushOrbitPointToGraphic();
    }

    /**
     * Current date Epoch.
     *
     * @return date in seconds.
     */
    static public double dateEpoch() {
        return seconds;
    }

    /**
     * Current date formatted.
     *
     * @return date string.
     */
    static public String dateString() {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond((long) seconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/LL/dd-HH:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    /**
     * Date formatted.
     *
     * @param seconds_time a date in seconds.
     * @return date string.
     */
    static public String dateString(double seconds_time) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond((long) seconds_time, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/LL/dd-HH:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    /**
     * Run a section the gravitational engine simulation and periodic graph refresh.
     *
     * @param steepsPerPlot number of steeps to execute by each graphical refresh.
     */
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
        constellation.pushOrbitPointToGraphic();
    }

    /**
     * Run the complete gravitational engine simulation.
     */
    public void runSimulation() {
        long simulationPlots = Parameter.SIMULATION_STEPS / Parameter.STEPS_PER_PLOT;
        for (long i = 0; i < simulationPlots; i++) {
            run(Parameter.STEPS_PER_PLOT);
            screen.updateConstellation();
        }
    }

    /**
     *
     * Run a section the gravitational engine route simulation and periodic graph refresh.
     *
     * @param steepsPerPlot number of steeps to execute by each graphical refresh.
     * @return true when the spacecraft land or lost the target
     */
    private boolean runRoute(long steepsPerPlot) {
        for (int i = 0; i < steepsPerPlot; i++) {
            if (route.isLaunched()) {
                if (route.spacecraftLand()) {
                    constellation.pushOrbitPointToGraphic();
                    return true;
                } else if (route.overtaking()) {
                    constellation.pushOrbitPointToGraphic();
                    return true;
                }
            } else {
                if (!secondsToRecoverStored) {
                    if (route.timeToLaunch(seconds + stepTime)) {
                        saveEngine();
                    }
                } else if (route.timeToLaunch(seconds)) {
                    route.launchToNextTarget();
                    constellation.addRocket(route);
                }
            }
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
        }
        constellation.pushOrbitPointToGraphic();
        return false;
    }

    /**
     * Run the complete gravitational engine route simulation.
     *
     * @param cmd command route parameters.
     * @param report report class user to log the route results
     */
    public void runSimulationTravel(Command cmd, Report report) {
        setRoute(cmd, report);
        report.print("+++++++++++++++++++++\n-Start a new simulation");
        long simulationPlots = Parameter.SIMULATION_STEPS / Parameter.STEPS_PER_PLOT;
        saveEngine();
        do {
            do {
                recoverEngine();

                for (long i = 0; i < simulationPlots; i++) {
                    if (runRoute(Parameter.STEPS_PER_PLOT)) {
                        screen.updateConstellation();
                        break;
                    }
                    screen.updateConstellation();
                }
                route.clearLaunched();
            } while (route.repeatInitialConditions());
        } while (route.nextLaunch());
        report.dump();
    }

    /**
     * Define a route to calculate.
     *
     * @param cmd command route parameters.
     * @param report report class user to log the route results
     */
    private void setRoute(Command cmd, Report report) {
        Body spacecraft = new Body(cmd.NAME, cmd.MASS, cmd.RADIUS, cmd.COLOR);
        Body origin = constellation.getBody(cmd.ORIGIN);
        Body target = constellation.getBody(cmd.TARGET);
        if ((origin == null) || (target == null)) {
            report.print("Error, the origin '%s' or the target '%s' is not in our constellation", cmd.ORIGIN, cmd.TARGET);
            System.exit(1);
        }
        route = new Route(report, spacecraft, origin, target, cmd);
    }
}
