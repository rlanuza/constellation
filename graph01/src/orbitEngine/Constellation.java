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
    private static Body[] bodies_new;
    private static Body[] bodies_old;
    private static double[][] dist;
    private static double[][] dist_x;
    private static double[][] dist_y;
    private static double[][] dist_z;

    public static boolean loadConstellation(String constelationStr) {
        // Load all constellation data from file
        String[] lines = constelationStr.split("\\r?\\n|\\r");;
        for (String line : lines) {
            if (line.startsWith("#")) {
                LOG.info(String.format("Comment: %s", line));
            } else {
                LOG.info(String.format("Body:    %s", line));
                String[] datas = line.split(",");
                if (datas.length == ASTRO_STRING_FIELDS) {
                    String name = datas[0].trim();
                    double mass = Double.valueOf(datas[1].trim());
                    double diameter = Double.valueOf(datas[2].trim());
                    double x = Double.valueOf(datas[3].trim());
                    double y = Double.valueOf(datas[4].trim());
                    double z = Double.valueOf(datas[5].trim());
                    double vx = Double.valueOf(datas[6].trim());
                    double vy = Double.valueOf(datas[7].trim());
                    double vz = Double.valueOf(datas[8].trim());
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
        dist = new double[bodies.size()][bodies.size()];
        dist_x = new double[bodies.size()][bodies.size()];
        dist_y = new double[bodies.size()][bodies.size()];
        dist_z = new double[bodies.size()][bodies.size()];

        return true;
    }

    void calculateDistances() {
        double dx, dy, dz, d2;
        for (int i = 0; i < bodies_old.length; i++) {
            for (int j = i + 1; i < bodies_old.length; j++) {
                dx = bodies_old[j].x - bodies_old[i].x;
                dy = bodies_old[j].y - bodies_old[i].y;
                dz = bodies_old[j].z - bodies_old[i].z;
                dist[i][j] = dx * dx + dy * dy + dz * dz;
                dist_x[i][j] = dx;
                dist_y[i][j] = dy;
                dist_z[i][j] = dz;
            }
        }
    }
    
    void step() {
        
    }
    
}
