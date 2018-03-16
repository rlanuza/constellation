package orbitEngine;

import java.awt.Color;
import static orbitEngine.Constellation.G_UNIVERSAL;

/**
 *
 * @author Roberto
 */
public class Body extends Position {

    String name;
    double mass;
    double g_mass;
    double radius;
    Color color;

    // Speed by axis: v(t)= d(r(t))/dt
    double vx;
    double vy;
    double vz;

    // Gravity acceleration by axis: a(t)= d(v(t))/dt = d2(r(t))/dt
    double gx;
    double gy;
    double gz;

    public Body(String name, double mass, double radius, double x, double y, double z, double vx, double vy, double vz) {
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
        int col = (int) Math.log10(mass) & 255;
        this.color = new Color((col * 8) & 255, (col * 4) & 255, (col * 2) & 255);
        //this.color = new Color(100, 100, 100);
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
}