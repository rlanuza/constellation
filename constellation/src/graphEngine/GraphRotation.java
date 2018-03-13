package graphEngine;

import orbitEngine.Position;

public class GraphRotation {

    // Rotation
    double x_rot;
    double y_rot;
    double z_rot;
    // Precalculed coefficients
    double sin_x_rot;
    double cos_x_rot;
    double sin_y_rot;
    double cos_y_rot;
    double sin_z_rot;
    double cos_z_rot;

    public void addRotation(double xRot, double yRot, double zRot) {
        x_rot = xRot;
        y_rot = yRot;
        z_rot = zRot;
        sin_x_rot = Math.sin(xRot);
        cos_x_rot = Math.cos(xRot);
        sin_y_rot = Math.sin(yRot);
        cos_y_rot = Math.cos(yRot);
        sin_z_rot = Math.sin(zRot);
        cos_z_rot = Math.cos(zRot);
    }

    public Position x_rotation(Position pos) {
        Position pos2 = new Position();
        // Axis X
        // y' = y*cos q - z*sin q
        // z' = y*sin q + z*cos q
        // x' = x
        pos2.y = pos.y * cos_x_rot - pos.z * sin_x_rot;
        pos2.z = pos.y * sin_x_rot + pos.z * cos_x_rot;
        pos2.x = pos.x;
        return pos2;
    }

    public Position y_rotation(Position pos) {
        Position pos2 = new Position();
        // Axis Y
        //z' = z*cos q - x*sin q
        //x' = z*sin q + x*cos q
        //y' = y
        pos2.z = pos.z * cos_y_rot - pos.x * sin_y_rot;
        pos2.x = pos.z * sin_y_rot + pos.x * cos_y_rot;
        pos2.y = pos.y;
        return pos2;
    }

    public Position z_rotation(Position pos) {
        Position pos2 = new Position();
        //x' = x*cos q - y*sin q
        //y' = x*sin q + y*cos q
        //z' = z
        pos2.x = pos.x * cos_z_rot - pos.y * sin_z_rot;
        pos2.y = pos.x * sin_z_rot + pos.y * cos_z_rot;
        pos2.z = pos.z;
        return pos2;
    }
}
