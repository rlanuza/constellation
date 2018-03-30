package graphEngine;

import java.awt.Color;

public final class GraphBody {

    int index;
    String name;
    double radius;
    int radius_i;
    Color color;
    GraphOrbit orbit;

    public GraphBody(int index, String name, double radius, Color color, GraphOrbit orbit, double scale) {
        this.index = index;
        this.name = name;
        this.radius = radius;

        this.color = color;
        this.orbit = orbit;
        recalculateGrRadius(scale);
    }

    public void recalculateGrRadius(double scale) {
        radius_i = (int) (radius * scale) + 1;
    }
}
