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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Roberto
 */
public class Engine {

    private double stepTime = 3600;

    private final Constellation constellation;

    public Engine(GraphConstellation gconstell, String constellationFile) {

        constellation = new Constellation(gconstell);
        try {
            String contents = new String(Files.readAllBytes(Paths.get(constellationFile)));
            constellation.loadConstellation(contents);
        } catch (IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            constellation.step_basic(stepTime);
            constellation.pushToGraphic();
            //constellation.step_jerk(stepTime);
            //constellation.step_basic_Schwarzschild(stepTime);
            //constellation.step_jerk_Schwarzschild(stepTime);
            
        }
        
    }
}
