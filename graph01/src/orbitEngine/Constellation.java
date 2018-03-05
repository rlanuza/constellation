/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author Roberto
 */
public class Constellation {

    public static final int ASTRO_STRING_FIELDS = 9;
    private static final Logger LOG = Logger.getLogger(Constellation.class.getName());
    // To let us load sequentially
    private static ArrayList<Body> bodies = new ArrayList<Body>();

    // Once we'll have all data loaded we'll convert o array and implement 2 copies to store old and new position each step
    public static Body[] bodies_new;
    public static Body[] bodies_old;

    public static boolean loadConstellation(String constelationStr) {
        // Load all constellation data from file
        String[] lines = constelationStr.split("\\r?\\n|\\r");;
        for (String line : lines) {
            if (line.startsWith("#")) {
                LOG.info(String.format("Comment: %s", line));
            } else {
                LOG.info(String.format("Body: %s", line));
                String[] datas = line.split(",");
                if (datas.length == ASTRO_STRING_FIELDS) {
                    String name = datas[0].trim();
                    float mass = Float.valueOf(datas[1].trim());
                    float diameter = Float.valueOf(datas[2].trim());
                    float x = Float.valueOf(datas[3].trim());
                    float y = Float.valueOf(datas[4].trim());
                    float z = Float.valueOf(datas[5].trim());
                    float vx = Float.valueOf(datas[6].trim());
                    float vy = Float.valueOf(datas[7].trim());
                    float vz = Float.valueOf(datas[8].trim());
                    Body new_body = new Body(name, mass, diameter, x, y, z, vx, vy, vz);
                    bodies.add(new_body);
                } else {
                    LOG.warning(String.format("loadConstellation: The number of parameters is not: (%d)", ASTRO_STRING_FIELDS));
                    return false;
                }
            }
        }
        // Now we'll generate array copies of the ArrayList (array is faster to be faster) for old and new position 
        bodies_new = bodies.toArray(new Body[bodies.size()]);
        bodies_old = bodies.toArray(new Body[bodies.size()]);

        return true;
    }
}
