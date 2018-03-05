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
    static ArrayList<Body> bodies = new ArrayList<Body>();

    public static boolean loadConstellation(String constelationStr) {
        String[] lines = constelationStr.split("\\r?\\n|\\r");;

        for (String line : lines) {
            LOG.info(String.format("loadConstellation line:\n\t(%s) ", line));
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
        return true;
    }
}
