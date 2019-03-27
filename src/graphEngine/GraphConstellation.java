package graphEngine;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import orbitEngine.Body;
import userInterface.Parameter;

/**
 * Represents the essential information that characterize a graphical constellation
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class GraphConstellation {

    /**
     * Graphical bodies with its orbit points.
     */
    GraphBody[] grBody;

    /**
     * Graphical scale in meters by pixel.
     */
    private double scale;

    /**
     * Graphical center.
     */
    GraphBody grBodyCenter = null;

    /**
     * Graphical rotation engine.
     */
    GraphRotation rotation;

    /**
     * Get the scale in string format to print in screen.
     *
     * @return scale string
     */
    public String getScaleString() {
        String s = String.format("Scale: %.4e m/pixel", 1 / scale);
        return s;
    }

    /**
     * Create a new graphical constellation.
     *
     * @param rotation rotation of the model to represent in screen.
     */
    GraphConstellation(GraphRotation rotation) {
        this.rotation = rotation;
        scale = Parameter.METERS_PER_PIXEL;
    }

    /**
     * Initialize the graphical constellation.
     *
     * @param body array of physical bodies to represent on graphically.
     */
    public synchronized void initConstellation(Body[] body) {
        grBody = new GraphBody[body.length];
        for (int i = 0; i < body.length; i++) {
            int j = body[i].getIndex();
            grBody[j] = new GraphBody(
                    body[i].getIndex(), body[i].getName(), body[i].getRadius(),
                    body[i].getColor(), new GraphOrbit(rotation), scale
            );
        }
    }

    /**
     * Re-index the constellation bodies adding a new body.
     *
     * @param body array of physical bodies to represent on graphically.
     */
    public synchronized void reindexConstellation(Body[] body) {
        GraphBody[] grTmp = new GraphBody[Body.getNextIndex()];
        // Copy all old bodies, even the collided.
        int i, j;
        for (i = 0, j = 0; i < grBody.length; i++) {
            grTmp[i] = grBody[i];
            if (grBody[i].index == body[j].getIndex()) {
                j++;
            } else {
                grTmp[i].name = "";
                grTmp[i].radius = 0;
            }
        }
        // Add the new element that arrives in last position
        grTmp[i] = new GraphBody(
                body[j].getIndex(), body[j].getName(), body[j].getRadius(),
                body[j].getColor(), new GraphOrbit(rotation), scale
        );
        // Replace the list
        grBody = grTmp;
    }

    /**
     * Update a new orbit point to the graphical constellation.
     *
     * @param body array of physical bodies to represent on graphically.
     */
    public synchronized void pushOrbitPointToGraphicConstellation(Body[] body) {
        for (int i = 0; i < body.length; i++) {
            grBody[body[i].getIndex()].orbit.addOrbitPoint(scale, body[i]);
        }
    }

    /**
     * Re-scale And Rotate the graphical constellation.
     *
     * @param zoom new zoom to re-scale.
     */
    synchronized void rescaleAndRotateGraphicConstellation(double zoom) {
        scale = Parameter.METERS_PER_PIXEL * zoom;
        for (GraphBody grB : grBody) {
            grB.recalculateGrRadius(scale);
            grB.orbit.rescaleAndRotateOrbit(scale);
        }
    }

    /**
     * Rotate the graphical constellation.
     */
    synchronized void rotateGraphicConstellation() {
        for (GraphBody grB : grBody) {
            grB.orbit.rescaleAndRotateOrbit(scale);
        }
    }

    /**
     * New Graphical center of constellation.
     *
     * @param bodyCenter new bodyCenter.
     */
    synchronized void newGraphicalConstellationCenter(String bodyCenter) {
        grBodyCenter = null;
        for (GraphBody grB : grBody) {
            if (grB.name.equals(bodyCenter)) {
                grBodyCenter = grB;
            }
        }
    }

    /**
     * Draw a circle to show a graphical body.
     *
     * @param g2d    graphic 2d object.
     * @param x      coordenate x.
     * @param y      coordenate y.
     * @param radius circle radius.
     */
    private void drawCircle(Graphics2D g2d, int x, int y, int radius) {
        int diameter = radius * 2;
        g2d.drawOval(x - radius, y - radius, diameter, diameter);
    }

    /**
     * Paint the constallation.
     *
     * @param g2d graphic 2d object.
     */
    synchronized void paintConstellation(Graphics2D g2d) {
        if (grBody != null) {
            // Get the center of the Graphical projection.
            int xc = 0;
            int yc = 0;
            if (grBodyCenter != null) {
                ArrayList<Point> p = grBodyCenter.orbit.projectionPointList;
                xc = p.get(p.size() - 1).x;
                yc = p.get(p.size() - 1).y;
            }
            // Plot the projected body constellation.
            for (GraphBody grB : grBody) {
                g2d.setColor(grB.color);
                if (!grB.orbit.projectionPointList.isEmpty()) {
                    int x0 = grB.orbit.projectionPointList.get(0).x - xc;
                    int y0 = grB.orbit.projectionPointList.get(0).y - yc;
                    // Plot the body orbit.
                    for (Point orbitPoint : grB.orbit.projectionPointList) {
                        int x1 = orbitPoint.x - xc;;
                        int y1 = orbitPoint.y - yc;;
                        g2d.drawLine(x0, y0, x1, y1);
                        x0 = x1;
                        y0 = y1;
                    }
                    // Plot the body.
                    drawCircle(g2d, x0, y0, grB.radius_i);
                    // Plot the name.
                    g2d.drawString(grB.name, x0, y0);
                }
            }
        }
    }
}
