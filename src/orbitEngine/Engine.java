/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import graphEngine.GraphConstellation;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import orbitEngine.routes.Route;
import userInterface.Command;
import userInterface.Parameter;

public class Engine {

    private final double stepTime;
    private static long seconds;
    private final Constellation constellation;
    private Route route;

    public Engine() {
        constellation = new Constellation();
        stepTime = Parameter.STEP_TIME;
        seconds = Parameter.START_EPOCH_TIME;
    }

    public void link(GraphConstellation graphConstellation) {
        constellation.link(graphConstellation);
        constellation.pushToGraphic();
    }

    static public String dateString() {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/LL/dd-HH:mm:ss", Locale.ENGLISH);
        return "Date: " + dateTime.format(formatter);
    }

    public void run(long steepsPerPlot) {
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

    public boolean runRoute(long steepsPerPlot) {
        for (int i = 0; i < steepsPerPlot; i++) {
            constellation.step_jerk(stepTime);
            seconds += stepTime;
            if (route.spacecraftLand()) {
                System.out.printf("Spacecraft Land on time '%s' as: %s\n", dateString(), route.spacecraftLandName());
                constellation.pushToGraphic();
                //System.exit(0);
                return true;
            }
            if (seconds >= route.time) {
                route.launchToNextTarget();
            }
        }
        constellation.pushToGraphic();
        return false;
    }

    public void setRoute(Command cmd) {
        Body spacecraft = new Body(cmd.NAME, cmd.MASS, cmd.RADIUS, cmd.COLOR);
        Body origin = constellation.getBody(cmd.ORIGIN);
        Body target = constellation.getBody(cmd.TARGET);
        if ((origin == null) || (target == null)) {
            System.out.printf("Error, the origin '%s' or the target '%s' is not in our constellation\n", cmd.ORIGIN, cmd.TARGET);
            System.exit(1);
        }
        route = new Route(spacecraft, origin, target, cmd.MIN_LAUNCH_TIME, cmd.MAX_LAUNCH_TIME, cmd.STEP_LAUNCH_TIME, cmd.MIN_SPEED, cmd.MAX_SPEED, cmd.STEP_SPEED);
        constellation.addRocket(route);
    }
}
