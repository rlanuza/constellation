package orbitEngine;

import java.awt.Color;
import static orbitEngine.Constellation.G_UNIVERSAL;

/**
 *
 * @author Roberto
 */
public class Body extends Position implements Cloneable {

    static int nextIndex = 0;
    private int index;
    String name;
    double mass;
    double g_mass;
    double radius;
    Color color;

    // Speed by axis: v(t)= d(r(t))/dt
    public double vx;
    public double vy;
    public double vz;

    // Gravity acceleration by axis: a(t)= d(v(t))/dt = d2(r(t))/dt
    double gx;
    double gy;
    double gz;

    // Energy on new bodies creation in Joule
    // @Todo split translational kinetic energy and rotational energy or angular kinetic energy i
    public double kinetic() {
        // Kinetic energy = 1/2*m*v^2 = 1/2*m*sqr(vx^2+vy^2+vz^2)^2 = 1/2*m*vx^2+vy^2+vz^2)
        return ((mass * (vx * vx + vy * vy + vz * vz)) / 2);  // Joules
    }

    /**
     * Constructor necessary to add bodies
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
     * Constructor necessary to add rockets
     */
    Body(String name, double mass, double radius, Color astroColor) {
        this(name, mass, radius, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, astroColor);
        nextIndex--;
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addGravity(double gx, double gy, double gz) {
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
    }

    public String getName() {
        return name;
    }

    public double getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }

    public static int getNextIndex() {
        return nextIndex;
    }
}
