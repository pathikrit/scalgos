/* File Point2D.java 
 *
 * Project : Java Geometry Library
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

// package

package math.geom2d;

import math.geom2d.AffineTransform2D;

// Imports

/**
 * A vector in the 2D plane. Provides methods to compute cross product and dot
 * product, addition and subtraction of vectors.
 */
public class Vector2D implements Cloneable {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    protected double x = 1;
    protected double y = 0;

    // ===================================================================
    // constructors

    /** construct a new Vectors initialized with x=1 and y=0. */
    public Vector2D() {
        this(1, 0);
    }

    /**
     * construct a new vector between origin and the point.
     */
    public Vector2D(java.awt.geom.Point2D point) {
        this(point.getX(), point.getY());
    }

    /**
     * construct a new vector between two points
     */
    public Vector2D(java.awt.geom.Point2D point1, java.awt.geom.Point2D point2) {
        this(point2.getX()-point1.getX(), point2.getY()-point1.getY());
    }

    /** constructor with given position. */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // ===================================================================
    // static functions

    public final static Vector2D createPolar(double rho, double theta) {
        return new Vector2D(rho*Math.cos(theta), rho*Math.sin(theta));
    }

    /**
     * Get the dot product of the two vectors, defined by :
     * <p>
     * <code> dx1*dy2 + dx2*dy1</code>
     * <p>
     * Dot product is zero if the vectors defined by the 2 vectors are
     * orthogonal. It is positive if vectors are in the same direction, and
     * negative if they are in opposite direction.
     */
    public final static double dot(Vector2D v1, Vector2D v2) {
        return v1.getX()*v2.getX()+v1.getY()*v2.getY();
    }

    /**
     * Get the cross product of the two vectors, defined by :
     * <p>
     * <code> dx1*dy2 - dx2*dy1</code>
     * <p>
     * cross product is zero for colinear vectors. It is positive if angle
     * between vector 1 and vector 2 is comprised between 0 and PI, and negative
     * otherwise.
     */
    public final static double cross(Vector2D v1, Vector2D v2) {
        return v1.getX()*v2.getY()-v2.getX()*v1.getY();
    }

    /**
     * Tests if the two vectors are colinear
     * 
     * @return true if the vectors are colinear
     */
    public final static boolean isColinear(Vector2D v1, Vector2D v2) {
        v1 = v1.getNormalizedVector();
        v2 = v2.getNormalizedVector();
        return Math.abs(v1.getX()*v2.getY()-v1.getY()*v2.getX())<Shape2D.ACCURACY;
    }

    /**
     * Tests if the two vectors are orthogonal
     * 
     * @return true if the vectors are orthogonal
     */
    public final static boolean isOrthogonal(Vector2D v1, Vector2D v2) {
        v1 = v1.getNormalizedVector();
        v2 = v2.getNormalizedVector();
        return Math.abs(v1.getX()*v2.getX()+v1.getY()*v2.getY())<Shape2D.ACCURACY;
    }

    // ===================================================================
    // accessors

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // ===================================================================
    // modifiers

    /**
     * @deprecated vectors will become imutable in a future release
     */
    @Deprecated
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @deprecated vectors will become imutable in a future release
     */
    @Deprecated
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @deprecated vectors will become imutable in a future release
     */
    @Deprecated
    public void setVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set location specified as polar coordinate : distance from origin + angle
     * with horizontal.
     * @deprecated vectors will become imutable in a future release
     */
    @Deprecated
    public void setAsPolar(double rho, double theta) {
        x = rho*Math.cos(theta);
        y = rho*Math.sin(theta);
    }

    /**
     * Returns the opposite vector v2 of this, such that the sum of this and v2
     * equals the null vector.
     * 
     * @return the vector opposite to <code>this</code>.
     */
    public Vector2D getOpposite() {
        return new Vector2D(-x, -y);
    }

    /**
     * Computes the norm of the vector
     * 
     * @return the euclidean norm of the vector
     */
    public double getNorm() {
        return Math.hypot(x, y);
    }

    /**
     * Returns the angle with the horizontal axis, in radians.
     * 
     * @return the horizontal angle of the vector
     */
    public double getAngle() {
        return Angle2D.getHorizontalAngle(this);
    }

    /**
     * Normalizes the vector, such that its norms becomes 1.
     */
    public void normalize() {
        double r = Math.hypot(this.x, this.y);
        this.x = this.x/r;
        this.y = this.y/r;
    }

    /**
     * Returns the vector with same direction as this one, but with norm equal
     * to 1.
     */
    public Vector2D getNormalizedVector() {
        double r = Math.hypot(this.x, this.y);
        return new Vector2D(this.x/r, this.y/r);
    }

    // ===================================================================
    // compare with other vectors

    /**
     * test if the two vectors are colinear
     * 
     * @return true if the vectors are colinear
     */
    public boolean isColinear(Vector2D v) {
        return Vector2D.isColinear(this, v);
    }

    /**
     * test if the two vectors are orthogonal
     * 
     * @return true if the vectors are orthogonal
     */
    public boolean isOrthogonal(Vector2D v) {
        return Vector2D.isOrthogonal(this, v);
    }

    // ===================================================================
    // operations between vectors

    /**
     * Get the dot product with point <code>p</code>. Dot product id defined
     * by :
     * <p>
     * <code> x1*y2 + x2*y1</code>
     * <p>
     * Dot product is zero if the vectors defined by the 2 points are
     * orthogonal. It is positive if vectors are in the same direction, and
     * negative if they are in opposite direction.
     */
    public double dot(Vector2D v) {
        return x*v.getX()+y*v.getY();
    }

    /**
     * Get the cross product with point <code>p</code>. Cross product is
     * defined by :
     * <p>
     * <code> x1*y2 - x2*y1</code>
     * <p>
     * cross product is zero for colinear vector. It is positive if angle
     * between vector 1 and vector 2 is comprised between 0 and PI, and negative
     * otherwise.
     */
    public double cross(Vector2D v) {
        return x*v.getY()-v.getX()*y;
    }

    /**
     * Returns the sum of current vector with vector given as parameter. Inner
     * fields are not modified.
     */
    public Vector2D plus(Vector2D v) {
        return new Vector2D(x+v.getX(), y+v.getY());
    }

    /**
     * Returns the subtraction of current vector with vector given as
     * parameter. Inner fields are not modified.
     */
    public Vector2D minus(Vector2D v) {
        return new Vector2D(x-v.getX(), y-v.getY());
    }

    /**
     * Multiplies the vector by a scalar amount. Inner fields are not 
     * @param k the scale factor
     * @return the scaled vector
     * @since 0.7.0
     */
    public Vector2D times(double k) {
        return new Vector2D(this.x*k, this.y*k);
    }
    
    /**
     * Transform the vector, by using only the first 4 parameters of the
     * transform. Translation of a vector returns the same vector.
     * 
     * @param trans an affine transform
     * @return the transformed vector.
     */
    public Vector2D transform(AffineTransform2D trans) {
        double[] tab = trans.getCoefficients();
        return new Vector2D(x*tab[0]+y*tab[1], x*tab[3]+y*tab[4]);
    }

    /**
     * Test whether this object is the same as another vector.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2D))
            return false;
        Vector2D v = (Vector2D) obj;
        return (Math.abs(v.getX()-x)<Shape2D.ACCURACY&&Math.abs(v.getY()-y)<Shape2D.ACCURACY);
    }
    
    @Override
    public Vector2D clone() {
        return new Vector2D(x, y);
    }
}