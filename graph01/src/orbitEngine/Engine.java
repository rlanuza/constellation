/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

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

    private final Constellation constellation = new Constellation();

    public Engine(String constellationFile) {
        String contents;
        try {
            contents = new String(Files.readAllBytes(Paths.get(constellationFile)));
            constellation.loadConstellation(contents);
        } catch (IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void run(){
        constellation.calculateDistances();
        constellation.step();
        
        /*TODO Calculate distances and distance proyeccion then acelerations and jerks */
        
    }
}
