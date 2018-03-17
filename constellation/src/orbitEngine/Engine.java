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
import userInterface.Parameters;

public class Engine {

    private double stepTime;
    static private long seconds;
    private long steepsPerPlot;

    private final Constellation constellation;

    public Engine() {
        constellation = new Constellation();
        stepTime = Parameters.STEP_TIME;
        seconds = Parameters.START_EPOCH_TIME;
        steepsPerPlot = Parameters.STEPS_PER_PLOT;
    }

    public void link(GraphConstellation graphConstellation) {
        constellation.link(graphConstellation);
        constellation.pushToGraphic();
    }

    static public String dateString() {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/LL/dd-HH:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    public void run() {
        seconds += stepTime * steepsPerPlot;
        switch (Parameters.CALCULUS_METHOD) {
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
        constellation.pushToGraphic();
    }
}
