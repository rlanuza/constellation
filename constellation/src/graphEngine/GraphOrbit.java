package graphEngine;

import java.awt.Point;
import java.util.concurrent.CopyOnWriteArrayList;
import orbitEngine.Position;

public class GraphOrbit {

    // Orbit list points
    CopyOnWriteArrayList<Point> proyectionPointList = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Position> point3d = new CopyOnWriteArrayList<>();

    public void addOrbitPoint(Point p, Position p_xyz) {
        proyectionPointList.add(p);
        point3d.add(p_xyz);
    }
}
