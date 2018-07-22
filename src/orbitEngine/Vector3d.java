package orbitEngine;

/**
 * Represents the essential information methods that characterize a vector 3d values and operations.
 *
 * @author Roberto Lanuza rolf2000@gmail.com
 * @version 1.0
 */
public class Vector3d {

    /**
     * Axis position: x. Unit m.
     */
    public double x;
    /**
     * Axis position: y. Unit m.
     */
    public double y;
    /**
     * Axis position: z. Unit m.
     */
    public double z;

    /**
     * Create a new Vector3d.
     */
    public Vector3d() {
    }

    /**
     * Clone the position of Vector3d or a Body that inherits Vector3d
     *
     * @param that Vector3d to clone position.
     */
    Vector3d(Vector3d that) {
        this.x = that.x;
        this.y = that.y;
        this.z = that.z;
    }

    /**
     * Create a new Vector3d from coordinates
     *
     * @param x x position. Unit m.
     * @param y y position. Unit m.
     * @param z z position. Unit m.
     */
    Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Clear the 3d vector.
     */
    void reset() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }

    /**
     * Returns the sum of this vector3d and the specified vector3d.
     *
     * @param that the Vector3d to add to this Vector3d
     * @return the Vector3d whose value is {@code (this + that)}
     */
    public Vector3d plus(Vector3d that) {
        Vector3d r = new Vector3d();
        r.x = this.x + that.x;
        r.y = this.y + that.y;
        r.z = this.z + that.z;
        return r;
    }

    /**
     * Returns the sum of this vector3d and the specified coordinates.
     *
     * @param x the x to add to this Vector3d
     * @param y the y to add to this Vector3d
     * @param z the z to add to this Vector3d
     * @return the Vector3d whose value is {@code (this + ^(x,y,z))}
     */
    public Vector3d plus(double x, double y, double z) {
        Vector3d r = new Vector3d();
        r.x = this.x + x;
        r.y = this.y + y;
        r.z = this.z + z;
        return r;
    }

    /**
     * Returns the difference between this vector3d and the specified vector3d.
     *
     * @param that the Vector3d to subtract from this Vector3d
     * @return the Vector3d whose value is {@code (this - that)}
     */
    public Vector3d minus(Vector3d that) {
        Vector3d r = new Vector3d();
        r.x = this.x - that.x;
        r.y = this.y - that.y;
        r.z = this.z - that.z;
        return r;
    }

    /**
     * Returns the difference between this vector3d and the specified coordinates.
     *
     * @param x the x to subtract to this Vector3d
     * @param y the y to subtract to this Vector3d
     * @param z the z to subtract to this Vector3d
     * @return the Vector3d whose value is {@code (this - ^(x,y,z))}
     */
    public Vector3d minus(double x, double y, double z) {
        Vector3d r = new Vector3d();
        r.x = this.x - x;
        r.y = this.y - y;
        r.z = this.z - z;
        return r;
    }

    /**
     * Returns the dot product of this vector3d and the specified vector3d.
     *
     * @param that Vector3d
     * @return the Vector3d whose value is {@code (this * that)}
     */
    public double dot(Vector3d that) {
        double r;
        r = this.x * that.x;
        r += this.y * that.y;
        r += this.z * that.z;
        return r;
    }

    /**
     * Returns the scalar product of this vector3d and the specified vector3d.
     *
     * @param alpha coefficient to scale this Vector3d
     * @return the Vector3d whose value is {@code (this * that)}
     */
    public Vector3d scale(double alpha) {
        Vector3d r = new Vector3d();
        r.x = alpha * this.x;
        r.y = alpha * this.y;
        r.z = alpha * this.z;
        return r;
    }

    /**
     * Magnitude of this vector.
     *
     * @return the magnitude of this vector
     */
    public double magnitude() {
        return Math.sqrt(this.dot(this));
    }

    /**
     * Returns the Euclidean distance between this vector and the specified vector.
     *
     * @param that the other vector
     * @return the Euclidean distance between this vector and that vector
     */
    public double distanceTo(Vector3d that) {
        return this.minus(that).magnitude();
    }
}
