package graphEngine;

import java.awt.Graphics2D;
import java.awt.Point;
import orbitEngine.Body;
import userInterface.Parameters;

public class GraphConstellation {

    // Orbit list points
    GraphBody[] grBody;
    private double metersPerPixel;
    private double scale;

    public String getScaleString() {
        String s = String.format("Scale: %.4e pixel/m", scale);
        return s;
    }

    GraphRotation rotation;

    GraphConstellation(GraphRotation rotation) {
        this.rotation = rotation;
        metersPerPixel = Parameters.METERS_PER_PIXEL;
        scale = metersPerPixel;
    }

    public void initConstellation(Body[] body) {
        grBody = new GraphBody[body.length];
        for (int i = 0; i < body.length; i++) {
            grBody[i] = new GraphBody();
            grBody[i].name = body[i].getName();
            grBody[i].radius = body[i].getRadius();
            grBody[i].radius_i = (int) (grBody[i].radius * scale) + 1;
            grBody[i].color = body[i].getColor();
            grBody[i].orbit = new GraphOrbit(rotation);
        }
    }

    public synchronized void updateGrConstellation(Body[] body) {
        for (int i = 0; i < body.length; i++) {
            grBody[i].orbit.addOrbitPoint(scale, body[i]);
        }
    }

    synchronized void rescaleGrConstellation(double zoom) {
        scale = metersPerPixel * zoom;
        for (GraphBody grB : grBody) {
            grB.radius_i = (int) (grB.radius * scale) + 1;
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
