package graphEngine;

import java.awt.Graphics2D;
import java.awt.Point;
import orbitEngine.Body;

public class GraphConstellation {

    public int lim_top = 0;
    public int lim_bottom = 0;
    public int lim_left = 0;
    public int lim_right = 0;
    /*@Todo manage point of view reference to transform */
 /*@Todo It's possible that we need manage double database to let us rotate*/
    public double ref_x = 1;
    public double ref_y = 1;
    public double ref_z = 0;

    // Orbit list points
    GraphBody[] grBody;
    final static double SCALE = 1.3e-10;
    double scale = SCALE;

    GraphRotation rotation = new GraphRotation();

    public GraphConstellation() {
        rotation.addRotation(0, 0, 0);
    }

    public void initConstellation(Body[] body) {
        grBody = new GraphBody[body.length];
        for (int i = 0; i < body.length; i++) {
            grBody[i] = new GraphBody();
            grBody[i].name = body[i].getName();
            grBody[i].radius = body[i].getRadius();
            grBody[i].radius_i = (int) (grBody[i].radius * scale) + 1;
            grBody[i].color = body[i].getColor();
            grBody[i].orbit = new GraphOrbit();
        }
    }

    synchronized public void updateGrConstellation(Body[] body) {
        for (int i = 0; i < body.length; i++) {
            /*@Todo autoadjust screen option
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
             */
            grBody[i].orbit.addOrbitPoint(scale, body[i]);
        }
    }

    synchronized public void rescaleGrConstellation(double zoom) {
        scale = SCALE * zoom;
        for (GraphBody grB : grBody) {
            grB.radius_i = (int) (grB.radius * scale) + 1;
            grB.orbit.rescaleOrbit(scale);
        }
    }

    private void drawCircle(Graphics2D g2d, int x, int y, int radius) {
        int diameter = radius * 2;
        g2d.drawOval(x - radius, y - radius, diameter, diameter);
    }

    void paintConstellation(Graphics2D g2d) {
        if (grBody != null) {
            for (GraphBody grBody : grBody) {
                g2d.setColor(grBody.color);
                if (!grBody.orbit.proyectionPointList.isEmpty()) {
                    int x0 = grBody.orbit.proyectionPointList.get(0).x;
                    int y0 = grBody.orbit.proyectionPointList.get(0).y;
                    for (Point orbitPoint : grBody.orbit.proyectionPointList) {
                        int x1 = orbitPoint.x;
                        int y1 = orbitPoint.y;
                        g2d.drawLine(x0, y0, x1, y1);
                        x0 = x1;
                        y0 = y1;
                    }
                    drawCircle(g2d, x0, y0, grBody.radius_i);
                    g2d.drawString(grBody.name, x0, y0);
                }
            }
        }
    }
}
