package orbitEngine;

import graphEngine.GraphConstellation;
import java.awt.Color;
import java.util.logging.Logger;
import static userInterface.Parameter.bodyList;

public class Constellation {

    public static final int ASTRO_STRING_FIELDS = 12;
    static final double C = 299792458;
    public static final double G_UNIVERSAL = 6.6740831e-11; // m^3/(Kg*s^2)
    static final double C2 = C * C;
    static final Logger LOG = Logger.getLogger(Constellation.class.getName());

    // Once we'll have all data loaded we'll convert o array and implement 2 copies to store old and new position each step
    private Body[] body;
    private Body[] bodyBackup;
    static double[][] dist;
    private static double[][] dist3;
    private static double[][] dist_x;
    private static double[][] dist_y;
    private static double[][] dist_z;
    private double gx;
    private double gy;
    private double gz;

    private GraphConstellation graphConstellation;
    private Route route = null;

    Constellation() {
        int n = bodyList.size();
        // Now we'll generate array copies (faster) of the ArrayList and left the ArrayList to let us recover initial positions
        body = new Body[n];
        int i = 0;
        for (Body b : bodyList) {
            body[i++] = (Body) b.clone();
        }
        Body.nextIndex = body.length;
        resizeDistanceArrays(n);
        // Calculate the initial gravity of the system
        initGravity();
        saveConstellation();
    }

    /**
     * Save the constellation when a simulation is active before a launch to let us save calculus
     */
    void saveConstellation() {
        int n = bodyList.size();
        bodyBackup = new Body[n];
        for (int i = 0; i < n; i++) {
            bodyBackup[i] = (Body) body[i].clone();
        }
    }

    void recoverConstellation() {
        int n = bodyList.size();
        body = new Body[n];
        for (int i = 0; i < n; i++) {
            body[i] = (Body) bodyBackup[i].clone();
        }

        Body.nextIndex = body.length;
        resizeDistanceArrays(n);
        // Calculate the initial gravity of the system
        initGravity();
    }

    void resetGrConstellation() {
        graphConstellation.initConstellation(body);
    }

    private void resizeDistanceArrays(int n) {
        dist = new double[n][n];
        dist3 = new double[n][n];
        dist_x = new double[n][n];
        dist_y = new double[n][n];
        dist_z = new double[n][n];
    }

    public void link(GraphConstellation graphConstellation) {
        this.graphConstellation = graphConstellation;
        // Prepare the graphical info
        resetGrConstellation();
    }

    private void mergeBodies(Body b1, Body b2) {
        Body[] bodyTmp = new Body[body.length - 1];
        int j = 0;
        for (int i = 0; i < body.length; i++) {
            if ((body[i] != b1) && (body[i] != b2)) {
                bodyTmp[j++] = body[i];
            }
        }
        // The Body1 must be the biggest
        if (b2.mass > b1.mass) {
            Body tmp = b1;
            b1 = b2;
            b2 = tmp;
        }
        String name = b1.name + "-" + b2.name;
        double mass = b1.mass + b2.mass;
        double radius = Math.cbrt(Math.pow(b1.radius, 3) + Math.pow(b2.radius, 3));
        // The new barycentre r1 = d*m2/(m1+m2)
        double x = b1.x + ((b2.x - b1.x) * b2.mass / mass);
        double y = b1.y + ((b2.y - b1.y) * b2.mass / mass);
        double z = b1.z + ((b2.z - b1.z) * b2.mass / mass);
        // The new speed vf = [ m1 v1 + m2 v2 ] / (m1 + m2)
        // [Assumption:the collision is inelastic and direct without modification in rotation of th]
        double vx = ((b1.mass * b1.vx) + (b2.mass * b2.vx)) / mass;
        double vy = ((b1.mass * b1.vy) + (b2.mass * b2.vy)) / mass;
        double vz = ((b1.mass * b1.vz) + (b2.mass * b2.vz)) / mass;
        // Kinetic energy loss Ecf - (Eci1+Eci2)= 1/2*((m1+m2)*vf^2-(m1*vi1^2+m2*vi2^2)
        int newRColor = Math.min(b1.color.getRed() + b2.color.getRed(), 255);
        int newGColor = Math.min(b1.color.getGreen() + b2.color.getGreen(), 255);
        int newBColor = Math.min(b1.color.getBlue() + b2.color.getBlue(), 255);
        Color astroColor = new Color(newRColor, newGColor, newBColor);
        bodyTmp[j] = new Body(name, mass, radius, x, y, z, vx, vy, vz, astroColor);
        double kineticLost = bodyTmp[j].kinetic() - b1.kinetic() - b2.kinetic();
        //System.out.printf("Kinetic lost on %s generation: %e\n", name, kineticLost);
        if (route != null) {
            route.mergeBodies(b1, b2, kineticLost);
        }
        body = bodyTmp;
        resizeDistanceArrays(body.length);
        graphConstellation.reindexConstellation(body);
    }

    // Calculate diatances and give a factor to reduce the delta time to get best accuracy on short distances
    void calculateDistances() {
        double dx, dy, dz, d2, d;
        for (int i = 0; i < body.length; i++) {
            for (int j = i + 1; j < body.length; j++) {
                dx = body[j].x - body[i].x;
                dy = body[j].y - body[i].y;
                dz = body[j].z - body[i].z;
                d2 = dx * dx + dy * dy + dz * dz;
                d = Math.sqrt(d2);
                dist[i][j] = d;
                dist3[i][j] = d * d2;
                dist_x[i][j] = dx;
                dist_y[i][j] = dy;
                dist_z[i][j] = dz;
                // Merge bodies here if they are near
                double distCollision = body[i].radius + body[j].radius;
                if (d < distCollision) {
                    mergeBodies(body[i], body[j]);
                    calculateDistances();
                    return;
                }
            }
        }
    }

    void calculateGravity(int i) {
        // F=G*(m1*m2)/d^2; g1=F/m1;  g2=F/m2;
        //   where: G is gravitation constant, F is force between the masses
        //          m1 & m2 are mass 1 and mass 2
        //          g1 & g2 gravity acceleration in mass 1 and mass 2
        // g2 = G*m1/d^2; g2x = g2*dx/d = G*m1*dx/d^3;
        //   where: dx is distance in x-axis and d is distance
        //          g1x & g2x gravity acceleration in axis-x in mass 1 and mass 2
        // gx(i) = SUM[j=0..n, except j==i]((dx(i,j)*(Gm(j)/d(i,j)^3));
        // gy(i) = SUM[j=0..n, except j==i]((dy(i,j)*(Gm(j)/d(i,j)^3));
        // gz(i) = SUM[j=0..n, except j==i]((dz(i,j)*(Gm(j)/d(i,j)^3));
        gx = 0;
        gy = 0;
        gz = 0;
        for (int j = 0; j < i; j++) {
            double gm_d3 = body[j].g_mass / dist3[j][i];
            gx -= dist_x[j][i] * gm_d3;
            gy -= dist_y[j][i] * gm_d3;
            gz -= dist_z[j][i] * gm_d3;
        }
        for (int j = i + 1; j < body.length; j++) {
            double gm_d3 = body[j].g_mass / dist3[i][j];
            gx += dist_x[i][j] * gm_d3;
            gy += dist_y[i][j] * gm_d3;
            gz += dist_z[i][j] * gm_d3;
        }
    }

    void calculateGravity_Schwarzschild(int i) {
        //   where: G is gravitation constant, F is force between the masses
        //          m1 & m2 are mass 1 and mass 2
        //          g1 & g2 gravity acceleration in mass 1 and mass 2
        //          c the speed of the light
        // g1 = (G*m2/d^2)*(1+(G*m2)/(d*c^2)); g1x = g1*dx/d = (G*m2*dx/d^3)*(1+(G*m2)/(d*c^2));
        //   where: G is gravitation constant, c the speed of the light
        //          dx is distance in x-axis and d is distance
        //          g1x & g2x gravity acceleration in axis-x in mass 1 and mass 2
        //
        // gx(i) = SUM[j=0..n, except j==i]((dx(i,j)*(Gm(j)/d(i,j)^3)*(1+(Gm(j))/(d(i,j)*C2)));
        // gy(i) = SUM[j=0..n, except j==i]((dy(i,j)*(Gm(j)/d(i,j)^3)*(1+(Gm(j))/(d(i,j)*C2)));
        // gz(i) = SUM[j=0..n, except j==i]((dz(i,j)*(Gm(j)/d(i,j)^3)*(1+(Gm(j))/(d(i,j)*C2)));
        gx = 0;
        gy = 0;
        gz = 0;
        for (int j = 0; j < i; j++) {
            double gm_d3Swc = (body[j].g_mass / dist3[j][i]) * (1 + (body[j].g_mass) / (dist[j][i] * C2));
            gx -= dist_x[j][i] * gm_d3Swc;
            gy -= dist_y[j][i] * gm_d3Swc;
            gz -= dist_z[j][i] * gm_d3Swc;
        }
        for (int j = i + 1; j < body.length; j++) {
            double gm_d3Swc = (body[j].g_mass / dist3[i][j]) * (1 + (body[j].g_mass) / (dist[i][j] * C2));
            gx += dist_x[i][j] * gm_d3Swc;
            gy += dist_y[i][j] * gm_d3Swc;
            gz += dist_z[i][j] * gm_d3Swc;
        }
    }

    void initGravity() {
        calculateDistances();
        for (int i = 0; i < body.length; i++) {
            calculateGravity(i);
            body[i].loadGravity(gx, gy, gz);
        }
    }

    void step_jerk(double deltaTime) {
        double delT_2 = deltaTime / 2;          // deltaTime/2
        double delT2_2 = deltaTime * delT_2;    // deltaTime^2/2
        double delT2_6 = delT2_2 / 3;           // deltaTime^2/6
        calculateDistances();
        for (int i = 0; i < body.length; i++) {
            calculateGravity(i);
            // Uniform jerk movement calculation
            // g(t) =  g0 + j*t; j = (g-g0)/t ~~ (g0-g_1)/t
            // [to optimize calculus approximate delta_g with the last delta_g]
            // v(t) = Integrate(g(t);dt) = v0 + g0*t + 1/2*j*t^2
            // v(t) = v0 + g0*t + 1/2*(g0-g_1)*t
            // s(t) = Integrate(v(t);dt) = s0 + v0*t + 1/2*g0*t^2 + 1/6*j*t^3
            // s(t) = s0 + v0*t + 1/2*g0*t^2 + 1/6*(g0-g_1)*t^2
            // New position s(t)
            body[i].x += body[i].vx * deltaTime + gx * delT2_2 + (gx - body[i].gx) * delT2_6;
            body[i].y += body[i].vy * deltaTime + gy * delT2_2 + (gy - body[i].gy) * delT2_6;
            body[i].z += body[i].vz * deltaTime + gz * delT2_2 + (gz - body[i].gz) * delT2_6;
            // New speed v(t)
            body[i].vx += gx * deltaTime + (gx - body[i].gx) * delT_2;
            body[i].vy += gy * deltaTime + (gy - body[i].gy) * delT_2;
            body[i].vz += gz * deltaTime + (gz - body[i].gz) * delT_2;
            body[i].loadGravity(gx, gy, gz);
        }
    }

    void step_basic(double deltaTime) {
        double delT_2 = deltaTime / 2;          // deltaTime/2
        double delT2_2 = deltaTime * delT_2;    // deltaTime^2/2
        calculateDistances();
        for (int i = 0; i < body.length; i++) {
            calculateGravity(i);
            // Uniform accelerated movement calculation
            // g(t) =  g0
            // v(t) = Integrate(g(t);dt) = v0 + g0*t
            // s(t) = Integrate(v(t);dt) = s0 + v0*t + 1/2*g0*t^2
            // New position s(t)
            body[i].x += body[i].vx * deltaTime + gx * delT2_2;
            body[i].y += body[i].vy * deltaTime + gy * delT2_2;
            body[i].z += body[i].vz * deltaTime + gz * delT2_2;
            // New speed v(t)
            body[i].vx += gx * deltaTime;
            body[i].vy += gy * deltaTime;
            body[i].vz += gz * deltaTime;
            //body[i].loadGravity(gx, gy, gz);
        }
    }

    void step_jerk_Schwarzschild(double deltaTime) {
        double delT_2 = deltaTime / 2;          // deltaTime/2
        double delT2_2 = deltaTime * delT_2;    // deltaTime^2/2
        double delT2_6 = delT2_2 / 3;           // deltaTime^2/6
        calculateDistances();
        for (int i = 0; i < body.length; i++) {
            calculateGravity_Schwarzschild(i);
            // Uniform jerk movement calculation
            // g(t) =  g0 + j*t; j = (g-g0)/t ~~ (g0-g_1)/t
            // [to optimize calculus approximate delta_g with the last delta_g]
            // v(t) = Integrate(g(t);dt) = v0 + g0*t + 1/2*j*t^2
            // v(t) = v0 + g0*t + 1/2*(g0-g_1)*t
            // s(t) = Integrate(v(t);dt) = s0 + v0*t + 1/2*g0*t^2 + 1/6*j*t^3
            // s(t) = s0 + v0*t + 1/2*g0*t^2 + 1/6*(g0-g_1)*t^2
            // New position s(t)
            body[i].x += body[i].vx * deltaTime + gx * delT2_2 + (gx - body[i].gx) * delT2_6;
            body[i].y += body[i].vy * deltaTime + gy * delT2_2 + (gy - body[i].gy) * delT2_6;
            body[i].z += body[i].vz * deltaTime + gz * delT2_2 + (gz - body[i].gz) * delT2_6;
            // New speed v(t)
            body[i].vx += gx * deltaTime + (gx - body[i].gx) * delT_2;
            body[i].vy += gy * deltaTime + (gy - body[i].gy) * delT_2;
            body[i].vz += gz * deltaTime + (gz - body[i].gz) * delT_2;
            body[i].loadGravity(gx, gy, gz);
        }
    }

    void step_basic_Schwarzschild(double deltaTime) {
        double delT_2 = deltaTime / 2;          // deltaTime/2
        double delT2_2 = deltaTime * delT_2;    // deltaTime^2/2
        calculateDistances();
        for (int i = 0; i < body.length; i++) {
            calculateGravity_Schwarzschild(i);
            // Uniform accelerated movement calculation
            // g(t) =  g0
            // v(t) = Integrate(g(t);dt) = v0 + g0*t
            // s(t) = Integrate(v(t);dt) = s0 + v0*t + 1/2*g0*t^2
            // New position s(t)
            body[i].x += body[i].vx * deltaTime + gx * delT2_2;
            body[i].y += body[i].vy * deltaTime + gy * delT2_2;
            body[i].z += body[i].vz * deltaTime + gz * delT2_2;
            // New speed v(t)
            body[i].vx += gx * deltaTime;
            body[i].vy += gy * deltaTime;
            body[i].vz += gz * deltaTime;
            body[i].loadGravity(gx, gy, gz);
        }
    }

    void pushToGraphic() {
        graphConstellation.updateGrConstellation(body);
    }

    /**
     * @Returns the body with the given name or null
     */
    Body getBody(String bodyName) {
        for (int i = 0; i < body.length; i++) {
            if (body[i].name.equals(bodyName)) {
                return body[i];
            }
        }
        return null;
    }

    /**
     * @Returns the body with the given index
     */
    Body getBody(int index) {
        return body[index];
    }

    void addRocket(Route route) {
        int i;
        this.route = route;
        Body[] bodyTmp = new Body[body.length + 1];
        for (i = 0; i < body.length; i++) {
            bodyTmp[i] = body[i];
        }

        bodyTmp[i] = route.getSpacecraft();
        Body.nextIndex++;
        body = bodyTmp;
        resizeDistanceArrays(body.length);
        graphConstellation.reindexConstellation(body);
        initGravity();
    }
}
