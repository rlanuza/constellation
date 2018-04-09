package graphEngine;

import java.awt.Graphics2D;
import java.awt.Point;
import orbitEngine.Body;
import userInterface.Parameter;

public class GraphConstellation {

    // Orbit list points
    GraphBody[] grBody;
    private double metersPerPixel;
    private double scale;

    public String getScaleString() {
        String s = String.format("Scale: %.4e m/pixel", 1 / scale);
        return s;
    }

    GraphRotation rotation;

    GraphConstellation(GraphRotation rotation) {
        this.rotation = rotation;
        metersPerPixel = Parameter.METERS_PER_PIXEL;
        scale = metersPerPixel;
    }

    public synchronized void initConstellation(Body[] b) {
        grBody = new GraphBody[b.length];
        for (int i = 0; i < b.length; i++) {
            int j = b[i].getIndex();
            grBody[j] = new GraphBody(
                    b[i].getIndex(), b[i].getName(), b[i].getRadius(),
                    b[i].getColor(), new GraphOrbit(rotation), scale
            );
        }
    }

    public synchronized void reindexConstellation(Body[] b) {
        GraphBody[] grTmp = new GraphBody[Body.getNextIndex()];
        // Copy all old bodies, even the collided
        int i, j;
        for (i = 0, j = 0; i < grBody.length; i++) {
            grTmp[i] = grBody[i];
            if (grBody[i].index == b[j].getIndex()) {
                j++;
            } else {
                grTmp[i].name = "";
                grTmp[i].radius = 0;
            }
        }
        // Add the new element that arrives in last position
        grTmp[i] = new GraphBody(
                b[j].getIndex(), b[j].getName(), b[j].getRadius(),
                b[j].getColor(), new GraphOrbit(rotation), scale
        );
        // Replace the list
        grBody = grTmp;
    }

    public synchronized void updateGrConstellation(Body[] body) {
        for (int i = 0; i < body.length; i++) {
            grBody[body[i].getIndex()].orbit.addOrbitPoint(scale, body[i]);
        }
    }

    synchronized void rescaleGrConstellation(double zoom) {
        scale = metersPerPixel * zoom;
        for (GraphBody grB : grBody) {
            grB.recalculateGrRadius(scale);
            grB.orbit.rescaleOrbit(scale);
        }
    }

    synchronized void rotateGrConstellation() {
        for (GraphBody grB : grBody) {
            grB.orbit.rescaleOrbit(scale);
        }
    }

    private void drawCircle(Graphics2D g2d, int x, int y, int radius) {
        int diameter = radius * 2;
        g2d.drawOval(x - radius, y - radius, diameter, diameter);
    }

    synchronized void paintConstellation(Graphics2D g2d) {
        if (grBody != null) {
            for (GraphBody grB : grBody) {
                g2d.setColor(grB.color);
                if (!grB.orbit.proyectionPointList.isEmpty()) {
                    int x0 = grB.orbit.proyectionPointList.get(0).x;
                    int y0 = grB.orbit.proyectionPointList.get(0).y;
                    for (Point orbitPoint : grB.orbit.proyectionPointList) {
                        int x1 = orbitPoint.x;
                        int y1 = orbitPoint.y;
                        g2d.drawLine(x0, y0, x1, y1);
                        x0 = x1;
                        y0 = y1;
                    }
                    drawCircle(g2d, x0, y0, grB.radius_i);
                    g2d.drawString(grB.name, x0, y0);
                }
            }
        }
    }
}
