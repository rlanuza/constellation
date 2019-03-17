package graphEngine;

import java.awt.Color;

/**
 * Represents the essential information that characterize a graphical body
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public final class GraphBody {

    /**
     * Index of the body used to let us indexed access.
     */
    int index;
    /**
     * Name of the body e.g: "Earth".
     */
    String name;
    /**
     * Average radius of the body. Units m.
     */
    double radius;
    /**
     * Scaled radius of the body in integer notation.
     */
    int radius_i;
    /**
     * Color assigned to the body.
     */
    Color color;
    /**
     * Graphical body orbit.
     */
    GraphOrbit orbit;

    /**
     * Create a new graphical body.
     *
     * @param Index of the body used to let us indexed access.
     * @param name Name of the body.
     * @param radius Average radius of the body. Unit m.
     * @param Color assigned to the body.
     * @param orbit body orbit.
     * @param scale scale used to convert to graphical.
     */
    public GraphBody(int index, String name, double radius, Color color, GraphOrbit orbit, double scale) {
        this.index = index;
        this.name = name;
        this.radius = radius;

        this.color = color;
        this.orbit = orbit;
        recalculateGrRadius(scale);
    }

    /**
     * Recalculate the scaled radius of the body with the scale.
     */
    public void recalculateGrRadius(double scale) {
        radius_i = (int) (radius * scale);
    }
}
