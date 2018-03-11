package graphEngine;

import java.awt.Point;
import java.util.ArrayList;
import orbitEngine.Position;

public class GraphOrbit {

    // Orbit list points
    ArrayList<Point> proyectionPointList = new ArrayList<>();
    ArrayList<Position> positionList = new ArrayList<>();

    public void addOrbitPoint(Point p,Position p_xyz) {
        proyectionPointList.add(p);
        positionList.add(p_xyz);
    }
}
