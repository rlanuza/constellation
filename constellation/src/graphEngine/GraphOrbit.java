package graphEngine;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import orbitEngine.Body;
import orbitEngine.Position;

public class GraphOrbit {

    // Orbit list points
    CopyOnWriteArrayList<Point> proyectionPointList = new CopyOnWriteArrayList<>();
    ArrayList<Position> point3d = new ArrayList<>();
    ///int dotVisibleModule = 1;
    ///int dotVisibleCnt = 0;

    synchronized public void addOrbitPoint(double scale, Body body) {
        Position p_xyz = new Position();
        p_xyz.x = body.x;
        p_xyz.y = body.y;
        p_xyz.z = body.z;
        point3d.add(p_xyz);
        Point p = new Point((int) (p_xyz.x * scale), (int) -(p_xyz.y * scale));
        /// if (dotVisibleCnt == 0) {
        proyectionPointList.add(p);
        ///}
        /// dotVisibleCnt = (dotVisibleCnt + 1) % dotVisibleModule;

    }

    synchronized public void rescaleOrbit(double scale) {
        int i = 0;
        proyectionPointList.clear();
        proyectionPointList = new CopyOnWriteArrayList<>();
        for (Position p_xyz : point3d) {
            Point p = new Point((int) (p_xyz.x * scale), (int) -(p_xyz.y * scale));
            proyectionPointList.add(p);
            //proyectionPointList.set(i++, p);
        }
    }
}
