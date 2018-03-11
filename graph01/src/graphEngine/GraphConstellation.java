package graphEngine;

import java.awt.Point;
import orbitEngine.Body;
import orbitEngine.Position;

public class GraphConstellation {

    public int lim_top = 0;
    public int lim_bottom = 0;
    public int lim_left = 0;
    public int lim_right = 0;
    public int lim_radius = 0;
    /*@Todo manage point of view reference to transform */
 /*@Todo possible we nedd manage double database to let us rotate*/
    public double ref_x = 1;
    public double ref_y = 1;
    public double ref_z = 0;

    // Orbit list points
    GraphBody[] gBody;
    double scale = 1e-9;

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
            gBody[i].orbit = new GraphOrbit();
        }
    }

    public void updateGrConstellation(Body[] body) {
        int ix, iy;
        for (int i = 0; i < body.length; i++) {
            Position p_xyz = new Position();
            p_xyz.x = body[i].x;
            p_xyz.y = body[i].y;
            p_xyz.z = body[i].z;

            ix = (int) (p_xyz.x * scale);
            iy = (int) -(p_xyz.y * scale);
            if (iy > lim_top) {
                lim_top = iy;
            } else if (lim_bottom > iy) {
                lim_bottom = iy;
            }
            if (iy > lim_right) {
                lim_right = iy;
            } else if (lim_left > iy) {
                lim_left = iy;
            }
            Point p = new Point(ix, iy);
            gBody[i].orbit.addOrbitPoint(p, p_xyz);
        }
    }

    /*@Todo add re-scale comands*/
 /*@Todo calculate screen limits with lim_radius pad */
 /*@Todo add methods to synchronize the orbit xyz-double with the proyection: xi,yi*/
}
