package graphEngine;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import orbitEngine.Position;

public class GraphOrbit {

    // Orbit list points
    CopyOnWriteArrayList<Point> proyectionPointList = new CopyOnWriteArrayList<>();
    private ArrayList<Position> positionList = new ArrayList<>();

    public void addOrbitPoint(Point p, Position p_xyz) {
        proyectionPointList.add(p);
        positionList.add(p_xyz);
    }
}
