package orbitEngine;

import java.awt.Color;

/**
 * Represents the essential information that characterize a body
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class Body extends Vector3d implements Cloneable {

    /**
     * Next index for a new body. Useful when some bodies merges in a new one and we need hide merged bodies and create a new one.
     */
    static int nextIndex = 0;
    /**
     * Index of the body used to let us indexed access.
     */
    private int index;
    /**
     * Name of the body e.g: "Earth".
     */
    String name;
    /**
     * Mass of the body in Kg.
     */
    double mass;
    /**
     * Gravitational constant G. Units m^3/(Kg*s^2).
     */
    private static final double G_UNIVERSAL = 6.6740831e-11;
    /**
     * Mass of the body in Kg multiplied by Gravitational constant G. Units m^3/s^2.
     */
    double g_mass;
    /**
     * Average radius of the body. Units m.
     */
    double radius;
    /**
     * Color user to represent this body on screen.
     */
    Color color;

    /**
     * Axis speed: v(t)= d(r(t))/dt
     */
    double vx;
    /**
     * Axis speed: v(t)= d(r(t))/dt
     */
    double vy;
    /**
     * Axis speed: v(t)= d(r(t))/dt
     */
    double vz;

    /**
     * Gravity acceleration by axis: g(t)= d(v(t))/dt = d2(r(t))/dt
     */
    double gx;
    /**
     * Gravity acceleration by axis: g(t)= d(v(t))/dt = d2(r(t))/dt
     */
    double gy;
    /**
     * Gravity acceleration by axis: g(t)= d(v(t))/dt = d2(r(t))/dt
     */
    double gz;

    /**
     * Create a new standard body.
     *
     * @param name Name of the body.
     * @param radius Average radius of the body. Unit m.
     * @param mass Body mass. Unit Kg.
     * @param x Position in axis x referenced to the Solar System barycenter. Unit m.
     * @param y Position in axis y referenced to the Solar System barycenter. Unit m.
     * @param z Position in axis z referenced to the Solar System barycenter. Unit m.
     * @param vx Speed in axis x. Unit m/s.
     * @param vy Speed in axis y. Unit m/s.
     * @param vz Speed in axis z. Unit m/s.
     * @param astroColor Color user to represent the body.
     */
    public Body(String name, double mass, double radius, double x, double y, double z, double vx, double vy, double vz, Color astroColor) {
        this.name = name;
        this.mass = mass;
        this.g_mass = mass * G_UNIVERSAL;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.color = astroColor;
        this.index = nextIndex;
        nextIndex++;
    }

    /**
     * Prepare an unallocated body. Necessary to add rockets to the constellation.
     *
     * @param name Name of the body.
     * @param radius Average radius of the body. Unit m.
     * @param mass Body mass. Unit Kg.
     * @param astroColor Color user to represent the body.
     */
    Body(String name, double mass, double radius, Color astroColor) {
        this(name, mass, radius, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, astroColor);
        nextIndex--;
    }

    /**
     * Clone bodies to save and restore them in calculus avoiding restart constellation from initial position.
     *
     * @return a clone copy of the body.
     */
    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Kinetic energy of a body.
     *
     * @return Energy in Joule.
     */
    public double kinetic() {
        // Kinetic energy = 1/2*m*v^2 = 1/2*m*sqr(vx^2+vy^2+vz^2)^2 = 1/2*m*vx^2+vy^2+vz^2)
        return ((mass * (vx * vx + vy * vy + vz * vz)) / 2.0);  // Joules
    }

    /**
     * Load the position in axis (x,y,z) referenced to the Solar System barycenter.
     *
     * @param v3d new position in meters.
     */
    public void loadPosition(Vector3d v3d) {
        this.x = v3d.x;
        this.y = v3d.y;
        this.z = v3d.z;
    }

    /**
     * Load the speed in axis (x,y,z).
     *
     * @param v3d new speed in m/s.
     */
    public void loadSpeed(Vector3d v3d) {
        this.vx = v3d.x;
        this.vy = v3d.y;
        this.vz = v3d.z;
    }

    /**
     * Load the gravity in axis (x,y,z).
     *
     * @param gx x axis gravity in m/s^2.
     * @param gy y axis gravity in m/s^2.
     * @param gz z axis gravity in m/s^2.
     */
    public void loadGravity(double gx, double gy, double gz) {
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
    }

    /**
     * Give the name of the project
     *
     * @return name of this body
     */
    public String getName() {
        return name;
    }

    /**
     * Give the radius of the body
     *
     * @return radius in meters
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Give the color used to represent the body.
     *
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Give the index of the body used to let us indexed access. Useful when some bodies merges in a new one and we need hide merged bodies
     * and create a new one.
     *
     * @return index of the body
     */
    public int getIndex() {
        return index;
    }

    /**
     * Give the next index for a new body. Useful when some bodies merges in a new one and we need hide merged bodies and create a new one.
     *
     * @return index of the next new body
     */
    public static int getNextIndex() {
        return nextIndex;
    }
}
