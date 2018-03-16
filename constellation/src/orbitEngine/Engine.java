/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import graphEngine.GraphConstellation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Engine {

    private double stepTime = 60;
    static private long seconds = 1520294400; //Epoc of 2018-Mar-06 00:00:00.0000 TDB

    private final Constellation constellation;

    public Engine(GraphConstellation graphConstellation, String constellationFile) {
        constellation = new Constellation(graphConstellation);
        try {
            String contents = new String(Files.readAllBytes(Paths.get(constellationFile)));
            constellation.loadConstellation(contents);
        } catch (IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
        constellation.pushToGraphic();
    }

    static public String dateString() {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/LL/dd-HH:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }
    int k = 0;

    public void run(int stepsByGraph) {
        seconds += stepTime * stepsByGraph;

        for (int i = 0; i < stepsByGraph; i++) {
            for (int j = 0; j < 10000000; j++) {
                k = j;
            }
            //constellation.step_basic(stepTime);
            constellation.step_jerk(stepTime);
            //constellation.step_basic_Schwarzschild(stepTime);
            //constellation.step_jerk_Schwarzschild(stepTime);
            //System.out.print("Izarbe es una petarda");
        }
        constellation.pushToGraphic();
    }
}