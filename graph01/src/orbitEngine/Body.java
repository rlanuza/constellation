package orbitEngine;

/**
 *
 * @author Roberto
 */
public class Body {

    float mass;
    float diameter;
    String name;

    // Position by axis r(t)
    float x;
    float y;
    float z;

    // Speed by axis: v(t)= d(r(t))/dt 
    float vx;
    float vy;
    float vz;

    // Gravity acceleration by axis: a(t)= d(v(t))/dt = d2(r(t))/dt
    float ax[];
    float ay[];
    float az[];

    // Jerk by axis: j(t)=d(a(t))/dt = d2(v(t))/dt = d3(r(t))/dt
    float jx[];
    float jy[];
    float jz[];

    public Body(String name, float mass, float diameter, float x, float y, float z, float vx, float vy, float vz) {
        this.name = name;
        this.mass = mass;
        this.diameter = diameter;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }

    public void addGravity(float[] ax, float[] ay, float[] az) {
        this.ax = ax;
        this.ay = ay;
        this.az = az;
    }

    public void addJerk(float[] jx, float[] jy, float[] jz) {
        this.jx = jx;
        this.jy = jy;
        this.jz = jz;
    }
}
