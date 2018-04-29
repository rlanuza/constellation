package orbitEngine;

public class Vector3d {

    // Vector3d by axis r(t)
    public double x;
    public double y;
    public double z;

    public Vector3d() {
    }

    /**
     * Clone the position of a Body
     */
    Vector3d(Vector3d that) {
        this.x = that.x;
        this.y = that.y;
        this.z = that.z;
    }

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
     * Returns the difference between this vector and the specified vector.
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
     * @param that the Vector3d to add to this Vector3d
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
     * Returns the magnitude of this vector.
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
     * @throws IllegalArgumentException if the dimensions of the two vectors are not equal
     */
    public double distanceTo(Vector3d that) {
        return this.minus(that).magnitude();
    }

}
