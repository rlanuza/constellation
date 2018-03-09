package graphEngine;

import java.awt.Point;
import java.util.ArrayList;
import orbitEngine.Position;

public class GraphOrbit {

    // Orbit list points
    ArrayList<Point> pointList = new ArrayList<>();
    ArrayList<Position> positionList = new ArrayList<>();

    public void addOrbitPoint(Point p,Position p_xyz) {
        pointList.add(p);
        positionList.add(p_xyz);
    }
}
