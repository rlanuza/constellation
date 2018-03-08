package graphEngine;

import java.awt.Point;
import java.util.ArrayList;

public class GraphOrbit {

    // Orbit list points
    ArrayList<Point> pointList = new ArrayList<>();

    public void addOrbitPoint(int x, int y) {
        Point point = new Point(x, y);
        pointList.add(point);
    }
}
