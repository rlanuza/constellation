/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orbitEngine;

/**
 *
 * @author Roberto
 */
public class Body {

    float mass;
    float diameter;
    int elements;

    // Position by axis r(t)
    float x;
    float y;
    float z;

    // Speed by axis: v(t)= d(r(t))/dt 
    float vx;
    float vy;
    float vz;

    // Gravity acceleration by axis: a(t)= d(v(t))/dt = d2(r(t))/dt
    float ax[] = new float[elements];
    float ay[] = new float[elements];
    float az[] = new float[elements];

    // Jerk by axis: j(t)=d(a(t))/dt = d2(v(t))/dt = d3(r(t))/dt
    float jx[] = new float[elements];
    float jy[] = new float[elements];
    float jz[] = new float[elements];

    public Body(float mass, float diameter, int elements, float x, float y, float z, float vx, float vy, float vz) {
        this.mass = mass;
        this.diameter = diameter;
        this.elements = elements;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }

    public Body(float[] ax, float[] ay, float[] az) {
        this.ax = ax;
        this.ay = ay;
        this.az = az;
    }

}
