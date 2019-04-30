package graphEngine;

import java.awt.Point;
import java.util.ArrayList;
import orbitEngine.Body;
import orbitEngine.Vector3d;
import userInterface.Parameter;

/**
 * Represents the essential information that characterize a graphical orbit.
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class GraphOrbit {

    /**
     * List of projection orbit points.
     */
    ArrayList<Point> projectionPointList = new ArrayList<>();
    /**
     * List of 3D projection orbit points.
     */
    private ArrayList<Vector3d> point3d = new ArrayList<>();
    /**
     * Rotation engine.
     */
    private GraphRotation rotation = null;
    /**
     * Point to memorize the last point to avoid repaint the same pixel.
     */
    private Point pOld = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

    /**
     * Create a new graphic orbit.
     *
     * @param rotation rotation engine link.
     */
    GraphOrbit(GraphRotation rotation) {
        this.rotation = rotation;
    }

    /**
     * Clear all graphical orbit points.
     */
    synchronized void clearOrbitPoints() {
        point3d = new ArrayList<>();
        projectionPointList = new ArrayList<>();
    }

    /**
     * Add a new graphic orbit point.
     *
     * @param scale      scale of the graphic.
     * @param body       is the body used as source of coordinates.
     * @param bodyCenter is the optionall body used as center of coordinates.
     */
    synchronized void addOrbitPoint(double scale, Body body, Body bodyCenter) {
        // If the maximum of 3d-point per orbit has been reached remove the oldest point.
        if (point3d.size() > Parameter.MAX_ORBIT_POINTS) {
            point3d.remove(0);
        }
        // If the maximum of 2d-projection point per orbit has been reached remove the oldest point.
        if (projectionPointList.size() > Parameter.MAX_ORBIT_POINTS) {
            projectionPointList.remove(0);
        }
        // Get the new coordinates from the physical body and add them to the list 3d and 2d.
        Vector3d p_xyz = new Vector3d();
        if (bodyCenter == null) {
            p_xyz.x = body.x;
            p_xyz.y = body.y;
            p_xyz.z = body.z;
        } else {
            p_xyz.x = body.x - bodyCenter.x;
            p_xyz.y = body.y - bodyCenter.y;
            p_xyz.z = body.z - bodyCenter.z;
        }
        // Add the coordinates the list 3d.
        Vector3d r_p_xyz = rotation.rotatePosition(p_xyz);
        Point p = new Point((int) (r_p_xyz.x * scale), (int) -(r_p_xyz.y * scale));
        point3d.add(p_xyz);
        // Add the coordinates the list 2d if the pixel is blank.
        if ((p.x != pOld.x) || (p.y != pOld.y)) {
            projectionPointList.add(p);
            pOld = p;
        }
    }

    /**
     * Re-scale and rotate the graphical orbit lists 3d and 2d projection.
     *
     * @param scale scale of the graphic.
     */
    synchronized void
            rescaleAndRotateOrbit(double scale
            ) {
        projectionPointList.clear();
        projectionPointList = new ArrayList<>();
        Point lastPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Vector3d p_xyz : point3d) {
            Vector3d r_p_xyz = rotation.rotatePosition(p_xyz);
            Point p = new Point((int) (r_p_xyz.x * scale), (int) -(r_p_xyz.y * scale));
            if ((p.x != lastPoint.x) || (p.y != lastPoint.y)) {
                projectionPointList.add(p);
                lastPoint = p;
            }
        }
    }
}
