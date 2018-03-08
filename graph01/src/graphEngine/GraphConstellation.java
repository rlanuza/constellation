package graphEngine;

import orbitEngine.Body;

public class GraphConstellation {

    int lim_top = 0;
    int lim_bottom = 0;
    int lim_left = 0;
    int lim_right = 0;
    int lim_radius = 0;
    /*@Todo manage point of view reference to transform */
 /*@Todo possible we nedd manage double database to let us rotate*/
    double ref_x = 1;
    double ref_y = 1;
    double ref_z = 0;

    // Orbit list points
    GraphBody[] gBody;
    double scale = 1e-6;

    public void initConstellation(Body[] body) {
        gBody = new GraphBody[body.length];
        int r;
        for (int i = 0; i < body.length; i++) {
            r = (int) (body[i].getRadius() * scale);
            if (lim_radius < r) {
                lim_radius = r;
            }
            gBody[i] = new GraphBody();
            gBody[i].name = body[i].getName();
            gBody[i].radius = r;
            gBody[i].color = body[i].getColor();
        }
    }

    public void updateConstellation(Body[] body) {
        int x, y;
        for (int i = 0; i < body.length; i++) {
            x = (int) (body[i].getX() * scale);
            y = (int) (body[i].getY() * scale);
            if (y > lim_top) {
                lim_top = y;
            } else if (lim_bottom > y) {
                lim_bottom = y;
            }
            if (y > lim_right) {
                lim_right = y;
            } else if (lim_left > y) {
                lim_left = y;
            }
            gBody[i].orbit = new GraphOrbit();
            gBody[i].orbit.addOrbitPoint(x, y);
        }
    }

    /*@Todo add re-scale comands*/
 /*@Todo calculate limits with lim_radius pad */
}
