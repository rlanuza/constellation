/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

import graphEngine.GraphConstellation;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author Roberto
 */
public class Constellation {

    static final int ASTRO_STRING_FIELDS = 9;
    static final double C = 299792458;
    static final double C2 = C * C;
    final Logger LOG = Logger.getLogger(Constellation.class.getName());
    // To let us load sequentially from file and then calculate the size
    static ArrayList<Body> bodyList = new ArrayList<Body>();

    // Once we'll have all data loaded we'll convert o array and implement 2 copies to store old and new position each step
    static Body[] body;
    static double[][] dist;
    static double[][] dist3;
    static double[][] dist_x;
    static double[][] dist_y;
    static double[][] dist_z;
    double gx;
    double gy;
    double gz;

    GraphConstellation grConstellation;

    Constellation(GraphConstellation gconstell) {
        grConstellation = gconstell;
    }

    public boolean loadConstellation(String constelationStr) {
        // Load all constellation data from file
        String[] lines = constelationStr.split("\\r?\\n|\\r");;
        for (String line : lines) {
            if (line.startsWith("#")) {
                //LOG.info(String.format("Comment: %s", line));
            } else {
                //LOG.info(String.format("Body:    %s", line));
                String[] datas = line.split(",");
                if (datas.length == ASTRO_STRING_FIELDS) {
                    String name = datas[0].trim();
                    double mass = Double.valueOf(datas[1].trim());
                    double diameter = Double.valueOf(datas[2].trim());
                    double x = Double.valueOf(datas[3].trim());
                    double y = Double.valueOf(datas[4].trim());
                    double z = Double.valueOf(datas[5].trim());
                    double vx = Double.valueOf(datas[6].trim());
                    double vy = Double.valueOf(datas[7].trim());
                    double vz = Double.valueOf(datas[8].trim());
                    Body new_body = new Body(name, mass, diameter, x, y, z, vx, vy, vz);
                    bodyList.add(new_body);
                } else {
                    LOG.warning(String.format("loadConstellation: The number of parameters is not: (%d)", ASTRO_STRING_FIELDS));
                    return false;
                }
            }
        }
        // Now we'll generate array copies (faster) of the ArrayList
        body = bodyList.toArray(new Body[bodyList.size()]);
        dist = new double[bodyList.size()][bodyList.size()];
        dist3 = new double[bodyList.size()][bodyList.size()];
        dist_x = new double[bodyList.size()][bodyList.size()];
        dist_y = new double[bodyList.size()][bodyList.size()];
        dist_z = new double[bodyList.size()][bodyList.size()];

        // Prepare the graph info
        grConstellation.initConstellation(body);

        // Calculate the initial gravity of the system
        initGravity();
        return true;
    }

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
                /*@Todo merge bodies here if they are near */
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
            /*@Todo Here I probably lost signs, please check */
            gx -= dist_x[j][i] * gm_d3;
            gy -= dist_y[j][i] * gm_d3;
            gz -= dist_z[j][i] * gm_d3;
        }
        for (int j = i + 1; j < body.length; j++) {
            double gm_d3 = body[j].g_mass / dist3[i][j];
            /*@Todo Here I probably lost signs, please check */
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
            /*@Todo Here I probably lost signs, please check */
            gx -= dist_x[j][i] * gm_d3Swc;
            gy -= dist_y[j][i] * gm_d3Swc;
            gz -= dist_z[j][i] * gm_d3Swc;
        }
        for (int j = i + 1; j < body.length; j++) {
            double gm_d3Swc = (body[j].g_mass / dist3[i][j]) * (1 + (body[j].g_mass) / (dist[j][i] * C2));
            /*@Todo Here I probably lost signs, please check */
            gx += dist_x[i][j] * gm_d3Swc;
            gy += dist_y[i][j] * gm_d3Swc;
            gz += dist_z[i][j] * gm_d3Swc;
        }
    }

    void initGravity() {
        calculateDistances();
        for (int i = 0; i < body.length; i++) {
            calculateGravity(i);
            body[i].addGravity(gx, gy, gx);
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
            body[i].vx += gy * deltaTime + (gy - body[i].gy) * delT_2;
            body[i].vx += gz * deltaTime + (gz - body[i].gz) * delT_2;

            body[i].addGravity(gx, gy, gx);
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
            body[i].vx += gy * deltaTime;
            body[i].vx += gz * deltaTime;

            body[i].addGravity(gx, gy, gx);
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
            body[i].vx += gy * deltaTime + (gy - body[i].gy) * delT_2;
            body[i].vx += gz * deltaTime + (gz - body[i].gz) * delT_2;

            body[i].addGravity(gx, gy, gx);
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
            body[i].vx += gy * deltaTime;
            body[i].vx += gz * deltaTime;

            body[i].addGravity(gx, gy, gx);
        }
    }

    void pushToGraphic() {
        grConstellation.updateConstellation(body);
    }
}
