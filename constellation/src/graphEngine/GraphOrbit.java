package graphEngine;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import orbitEngine.Body;
import orbitEngine.Position;
import userInterface.Parameters;

public class GraphOrbit {

    // Orbit list points
    CopyOnWriteArrayList<Point> proyectionPointList = new CopyOnWriteArrayList<>();
    private ArrayList<Position> point3d = new ArrayList<>();
    private GraphRotation rotation = null;
    private Point p_old = new Point(0, 0);

    GraphOrbit(GraphRotation rotation) {
        this.rotation = rotation;
    }

    synchronized void addOrbitPoint(double scale, Body body) {
        if (point3d.size() > Parameters.MAX_ORBIT_POINTS) {
            point3d.remove(0);
        }
        if (proyectionPointList.size() > Parameters.MAX_ORBIT_POINTS) {
            proyectionPointList.remove(0);
        }
        Position p_xyz = new Position();
        p_xyz.x = body.x;
        p_xyz.y = body.y;
        p_xyz.z = body.z;
        Position r_p_xyz = rotation.rotatePosition(p_xyz);
        Point p = new Point((int) (r_p_xyz.x * scale), (int) -(r_p_xyz.y * scale));
        point3d.add(p_xyz);
        if ((p.x != p_old.x) || (p.y != p_old.y)) {
            proyectionPointList.add(p);
            p_old = p;
        }
    }

    synchronized void rescaleOrbit(double scale) {
        proyectionPointList.clear();
        proyectionPointList = new CopyOnWriteArrayList<>();
        Point p_old = new Point(0, 0);
        for (Position p_xyz : point3d) {
            Position r_p_xyz = rotation.rotatePosition(p_xyz);
            Point p = new Point((int) (r_p_xyz.x * scale), (int) -(r_p_xyz.y * scale));
            if ((p.x != p_old.x) || (p.y != p_old.y)) {
                proyectionPointList.add(p);
                p_old = p;
            }
        }
    }
}
