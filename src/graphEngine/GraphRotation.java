package graphEngine;

import orbitEngine.Vector3d;

public class GraphRotation {

    // Rotation
    private int x_steps = 0;
    private int y_steps = 0;
    private int z_steps = 0;
    private double stepRadians = Math.PI / 18.0;
    private int stepGrades = (int) (2 * Math.PI / stepRadians);
    // Precalculed coefficients
    private double sin_x_rot;
    private double cos_x_rot;
    private double sin_y_rot;
    private double cos_y_rot;
    private double sin_z_rot;
    private double cos_z_rot;

    public void resetCoeficients() {
        x_steps = 0;
        y_steps = 0;
        z_steps = 0;
        updateCoeficients(0, 0, 0);
    }

    public void updateCoeficients(int delta_xRot, int delta_yRot, int delta_zRot) {
        x_steps += delta_xRot;
        if (x_steps == 36) {
            x_steps = 0;
        }
        if (x_steps == -1) {
            x_steps = 35;
        }
        y_steps += delta_yRot;
        if (y_steps == 36) {
            y_steps = 0;
        }
        if (y_steps == -1) {
            y_steps = 35;
        }
        z_steps += delta_zRot;
        if (z_steps == 36) {
            z_steps = 0;
        }
        if (z_steps == -1) {
            z_steps = 35;
        }
        double x_rot = x_steps * stepRadians;
        double y_rot = y_steps * stepRadians;
        double z_rot = z_steps * stepRadians;
        sin_x_rot = Math.sin(x_rot);
        cos_x_rot = Math.cos(x_rot);
        sin_y_rot = Math.sin(y_rot);
        cos_y_rot = Math.cos(y_rot);
        sin_z_rot = Math.sin(z_rot);
        cos_z_rot = Math.cos(z_rot);
    }

    public Vector3d rotatePosition(Vector3d pos) {
        Vector3d newPos = pos;
        if (x_steps != 0) {
            newPos = x_rotation(newPos);
        }
        if (y_steps != 0) {
            newPos = y_rotation(newPos);
        }
        if (z_steps != 0) {
            newPos = z_rotation(newPos);
        }
        return newPos;
    }

    private Vector3d x_rotation(Vector3d pos) {
        Vector3d pos2 = new Vector3d();
        // Axis X
        // y' = y*cos q - z*sin q
        // z' = y*sin q + z*cos q
        // x' = x
        pos2.y = pos.y * cos_x_rot - pos.z * sin_x_rot;
        pos2.z = pos.y * sin_x_rot + pos.z * cos_x_rot;
        pos2.x = pos.x;
        return pos2;
    }

    private Vector3d y_rotation(Vector3d pos) {
        Vector3d pos2 = new Vector3d();
        // Axis Y
        //z' = z*cos q - x*sin q
        //x' = z*sin q + x*cos q
        //y' = y
        pos2.z = pos.z * cos_y_rot - pos.x * sin_y_rot;
        pos2.x = pos.z * sin_y_rot + pos.x * cos_y_rot;
        pos2.y = pos.y;
        return pos2;
    }

    private Vector3d z_rotation(Vector3d pos) {
        Vector3d pos2 = new Vector3d();
        //x' = x*cos q - y*sin q
        //y' = x*sin q + y*cos q
        //z' = z
        pos2.x = pos.x * cos_z_rot - pos.y * sin_z_rot;
        pos2.y = pos.x * sin_z_rot + pos.y * cos_z_rot;
        pos2.z = pos.z;
        return pos2;
    }

    String getRotationString() {
        int pitch = x_steps * 10;
        int yaw = y_steps * 10;
        int roll = z_steps * 10;
        return String.format("Pitch: %dº, Yaw: %dº, Roll: %dº", pitch, yaw, roll);
    }
}
