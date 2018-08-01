package graphEngine;

import java.awt.Point;
import java.util.ArrayList;
import orbitEngine.Body;
import orbitEngine.Vector3d;
import userInterface.Parameter;

/*@Todo: comentar fichero $$$$$$$$$ESTOY COMENTANDO AQUI $$$$$$$$$*/
public class GraphOrbit {

    // Orbit list points
    ArrayList<Point> proyectionPointList = new ArrayList<>();
    private final ArrayList<Vector3d> point3d = new ArrayList<>();
    private GraphRotation rotation = null;
    private Point pOld = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

    GraphOrbit(GraphRotation rotation) {
        this.rotation = rotation;
    }

    synchronized void addOrbitPoint(double scale, Body body) {
        if (point3d.size() > Parameter.MAX_ORBIT_POINTS) {
            point3d.remove(0);
        }
        if (proyectionPointList.size() > Parameter.MAX_ORBIT_POINTS) {
            proyectionPointList.remove(0);
        }
        Vector3d p_xyz = new Vector3d();
        p_xyz.x = body.x;
        p_xyz.y = body.y;
        p_xyz.z = body.z;
        Vector3d r_p_xyz = rotation.rotatePosition(p_xyz);
        Point p = new Point((int) (r_p_xyz.x * scale), (int) -(r_p_xyz.y * scale));
        point3d.add(p_xyz);
        if ((p.x != pOld.x) || (p.y != pOld.y)) {
            proyectionPointList.add(p);
            pOld = p;
        }
    }

    synchronized void rescaleOrbit(double scale) {
        proyectionPointList.clear();
        proyectionPointList = new ArrayList<>();
        Point lastPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Vector3d p_xyz : point3d) {
            Vector3d r_p_xyz = rotation.rotatePosition(p_xyz);
            Point p = new Point((int) (r_p_xyz.x * scale), (int) -(r_p_xyz.y * scale));
            if ((p.x != lastPoint.x) || (p.y != lastPoint.y)) {
                proyectionPointList.add(p);
                lastPoint = p;
            }
        }
    }
}
