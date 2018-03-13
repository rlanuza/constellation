package graphEngine;

import java.awt.Graphics2D;
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
 /*@Todo possible we need manage double database to let us rotate*/
    public double ref_x = 1;
    public double ref_y = 1;
    public double ref_z = 0;

    // Orbit list points
    GraphBody[] gBody;
    double scale = 1.3e-10;

    GraphRotation rotation = new GraphRotation();

    public GraphConstellation() {
        rotation.addRotation(0, 0, 0);
    }

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

    private void drawCircle(Graphics2D g2d, int x, int y, int radius) {
        int diameter = radius * 2;
        g2d.drawOval(x - radius, y - radius, diameter, diameter);
    }

    /*@Todo add re-scale commands*/
 /*@Todo calculate screen limits with lim_radius pad */
 /*@Todo add methods to synchronize the orbit xyz-double with the proyection: xi,yi*/
    void paintConstellation(Graphics2D g2d) {
        if (gBody != null) {
            for (GraphBody grBody : gBody) {
                g2d.setColor(grBody.color);
                int x0 = grBody.orbit.proyectionPointList.get(0).x;
                int y0 = grBody.orbit.proyectionPointList.get(0).y;
                for (Point orbitPoint : grBody.orbit.proyectionPointList) {
                    int x1 = orbitPoint.x;
                    int y1 = orbitPoint.y;
                    g2d.drawLine(x0, y0, x1, y1);
                    x0 = x1;
                    y0 = y1;
                }
                drawCircle(g2d, x0, y0, grBody.radius);
                g2d.drawString(grBody.name, x0, y0);
            }
        }
    }
}
