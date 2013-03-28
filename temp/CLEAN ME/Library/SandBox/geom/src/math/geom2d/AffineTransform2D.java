/* File AffineTransform2D.java 
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

// Imports
import math.geom2d.Shape2D;
import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.transform.Bijection2D;

/**
 * Base class for generic affine transforms in the plane. They include
 * rotations, translations, shears, homotheties, and combinations of these. Such
 * transformations can be constructed by using coefficients specification, or by
 * creating specialized instances, by using static methods.
 * <p>
 */
public class AffineTransform2D implements Bijection2D, Cloneable {

    // coefficients for x coordinate.
    protected double m00, m01, m02;

    // coefficients for y coordinate.
    protected double m10, m11, m12;

    // ===================================================================
    // static methods

    public final static AffineTransform2D createGlideReflection(
            LinearShape2D line, double distance) {
        Vector2D vector = line.getVector().getNormalizedVector();
        Point2D origin = line.getOrigin();
        double dx = vector.getX();
        double dy = vector.getY();
        double x0 = origin.getX();
        double y0 = origin.getY();
        double delta = dx*dx+dy*dy;

        double tx = vector.getX()*distance;
        double ty = vector.getY()*distance;

        return new AffineTransform2D((dx*dx-dy*dy)/delta, 2*dx*dy/delta, 2*dy
                *(dy*x0-dx*y0)/delta+tx, 2*dx*dy/delta, (dy*dy-dx*dx)/delta, 2
                *dx*(dx*y0-dy*x0)/delta+ty);
    }

    public final static AffineTransform2D createHomothecy(Point2D center,
            double k) {
        return createScaling(center, k, k);
    }

    public final static AffineTransform2D createLineReflection(
            LinearShape2D line) {
        Vector2D vector = line.getVector();
        Point2D origin = line.getOrigin();
        double dx = vector.getX();
        double dy = vector.getY();
        double x0 = origin.getX();
        double y0 = origin.getY();
        double delta = dx*dx+dy*dy;

        return new AffineTransform2D((dx*dx-dy*dy)/delta, 2*dx*dy/delta, 2*dy
                *(dy*x0-dx*y0)/delta, 2*dx*dy/delta, (dy*dy-dx*dx)/delta, 2*dx
                *(dx*y0-dy*x0)/delta);
    }

    /**
     * Return a point reflection centered on a point.
     * 
     * @param center the center of the reflection
     * @return an instance of AffineTransform2D representing a point reflection
     */
    public final static AffineTransform2D createPointReflection(Point2D center) {
        return AffineTransform2D.createScaling(center, -1, -1);
    }

    public final static AffineTransform2D createQuadrantRotation(int numQuadrant) {
        int n = ((numQuadrant%4)+4)%4;
        switch (n) {
        case 0:
            return new AffineTransform2D(1, 0, 0, 0, 1, 0);
        case 1:
            return new AffineTransform2D(0, -1, 0, 1, 0, 0);
        case 2:
            return new AffineTransform2D(-1, 0, 0, 0, -1, 0);
        case 3:
            return new AffineTransform2D(0, 1, 0, -1, 0, 0);
        default:
            return new AffineTransform2D(1, 0, 0, 0, 1, 0);
        }
    }

    /**
     * Return a rotation around the origin, with angle in radians.
     */
    public final static AffineTransform2D createRotation(double angle) {
        return AffineTransform2D.createRotation(0, 0, angle);
    }

    /**
     * Return a rotation around the specified point, with angle in radians.
     */
    public final static AffineTransform2D createRotation(Point2D center,
            double angle) {
        return AffineTransform2D.createRotation(center.getX(), center.getY(),
                angle);
    }

    /**
     * Return a rotation around the specified point, with angle in radians. If
     * the angular distance of the angle with a multiple of PI/2 is lower than
     * the threshold Shape2D.ACCURACY, the method assumes equality.
     */
    public final static AffineTransform2D createRotation(double cx, double cy,
            double angle) {
        angle = Angle2D.formatAngle(angle);

        // coefficients of parameters m00, m01, m10 and m11.
        double cot = 1, sit = 0;

        // special processing to detect angle close to multiple of PI/2.
        int k = (int) Math.round(angle*2/Math.PI);
        if (Math.abs(k*Math.PI/2-angle)<Shape2D.ACCURACY) {
            assert k>=0 : "k should be positive";
            assert k<5 : "k should be between 0 and 4";
            switch (k) {
            case 0:
                cot = 1;
                sit = 0;
                break;
            case 1:
                cot = 0;
                sit = 1;
                break;
            case 2:
                cot = -1;
                sit = 0;
                break;
            case 3:
                cot = 0;
                sit = -1;
                break;
            case 4:
                cot = 1;
                sit = 0;
                break;
            }
        } else {
            cot = Math.cos(angle);
            sit = Math.sin(angle);
        }

        // init coef of the new AffineTransform.
        return new AffineTransform2D(cot, -sit, (1-cot)*cx+sit*cy, sit, cot,
                (1-cot)*cy-sit*cx);
    }

    /**
     * Return a scaling by the given coefficients, centered on the origin.
     */
    public final static AffineTransform2D createScaling(double sx, double sy) {
        return AffineTransform2D.createScaling(new Point2D(0, 0), sx, sy);
    }

    /**
     * Return a scaling by the given coefficients, centered on the given point.
     */
    public final static AffineTransform2D createScaling(Point2D center,
            double sx, double sy) {
        return new AffineTransform2D(sx, 0, (1-sx)*center.getX(), 0, sy, (1-sy)
                *center.getY());
    }

    /**
     * Creates a Shear transform, using the classical Java notation.
     * 
     * @param shx shear in x-axis
     * @param shy shear in y-axis
     * @return a shear transform
     */
    public final static AffineTransform2D createShear(double shx, double shy) {
        return new AffineTransform2D(1, shx, 0, shy, 1, 0);
    }

    /**
     * Return a translation by the given vector.
     */
    public final static AffineTransform2D createTranslation(Vector2D vect) {
        return new AffineTransform2D(1, 0, vect.getX(), 0, 1, vect.getY());
    }

    /**
     * Return a translation by the given vector.
     */
    public final static AffineTransform2D createTranslation(double dx, double dy) {
        return new AffineTransform2D(1, 0, dx, 0, 1, dy);
    }

    // ===================================================================
    // methods to identify transforms

    /**
     * Checks if the given transform is the identity transform.
     */
    public final static boolean isIdentity(AffineTransform2D trans) {
        double[] coefs = trans.getCoefficients();
        if (Math.abs(coefs[0]-1)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(coefs[1])>Shape2D.ACCURACY)
            return false;
        if (Math.abs(coefs[2])>Shape2D.ACCURACY)
            return false;
        if (Math.abs(coefs[3])>Shape2D.ACCURACY)
            return false;
        if (Math.abs(coefs[4]-1)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(coefs[5])>Shape2D.ACCURACY)
            return false;
        return true;
    }

    /**
     * Checks if the transform is direct, i.e. it preserves the orientation of
     * transformed shapes.
     * 
     * @return true if transform is direct.
     */
    public final static boolean isDirect(AffineTransform2D trans) {
        double[][] mat = trans.getAffineMatrix();
        return mat[0][0]*mat[1][1]-mat[0][1]*mat[1][0]>0;
    }

    /**
     * Checks if the transform is an isometry, i.e. a compound of translation,
     * rotation and reflection. Isometry keeps area of shapes unchanged, but can
     * change orientation (directed or undirected).
     * 
     * @return true in case of isometry.
     */
    public final static boolean isIsometry(AffineTransform2D trans) {
        // extract matrix coefficients
        double[][] mat = trans.getAffineMatrix();
        double a = mat[0][0];
        double b = mat[0][1];
        double d = mat[1][0];
        double e = mat[1][1];

        // peforms some tests
        if (Math.abs(a*a+d*d-1)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(b*b+e*e-1)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(a*b+d*e)>Shape2D.ACCURACY)
            return false;

        // if all tests passed, return true;
        return true;
    }

    /**
     * Checks if the transform is a motion, i.e. a compound of translations and
     * rotation. Motion remains area and orientation (directed or undirected) of
     * shapes unchanged.
     * 
     * @return true in case of motion.
     */
    public final static boolean isMotion(AffineTransform2D trans) {
        double[][] mat = trans.getAffineMatrix();
        double det = mat[0][0]*mat[1][1]-mat[0][1]*mat[1][0];
        return Math.abs(det-1)<Shape2D.ACCURACY;
    }

    /**
     * Checks if the transform is an similarity, i.e. transformation which keeps
     * unchanged the global shape, up to a scaling factor.
     * 
     * @return true in case of similarity.
     */
    public final static boolean isSimilarity(AffineTransform2D trans) {
        double[][] mat = trans.getAffineMatrix();
        // isolate linear part of the transform
        double a = mat[0][0];
        double b = mat[1][0];
        double c = mat[0][1];
        double d = mat[1][1];

        // determinant
        double k2 = Math.abs(a*d-b*c);

        // test each condition
        if (Math.abs(a*a+b*b-k2)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(c*c+d*d-k2)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(a*a+c*c-k2)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(b*b+d*d-k2)>Shape2D.ACCURACY)
            return false;

        // if each test passed, return true
        return true;
    }

    // ===================================================================
    // Constructors

    /** Main constructor */
    public AffineTransform2D() {
        // init to identity matrix
        m00 = m11 = 1;
        m01 = m10 = 0;
        m02 = m12 = 0;
    }

    /** constructor by copy of an existing transform */
    public AffineTransform2D(AffineTransform2D trans) {
        double[][] mat = trans.getAffineMatrix();
        this.m00 = mat[0][0];
        this.m01 = mat[0][1];
        this.m02 = mat[0][2];
        this.m10 = mat[1][0];
        this.m11 = mat[1][1];
        this.m12 = mat[1][2];
    }

    public AffineTransform2D(double[] coefs) {
        if (coefs.length==4) {
            m00 = coefs[0];
            m01 = coefs[1];
            m10 = coefs[2];
            m11 = coefs[3];
        } else {
            m00 = coefs[0];
            m01 = coefs[1];
            m02 = coefs[2];
            m10 = coefs[3];
            m11 = coefs[4];
            m12 = coefs[5];
        }
    }

    public AffineTransform2D(double xx, double yx, double tx, double xy,
            double yy, double ty) {
        m00 = xx;
        m01 = yx;
        m02 = tx;
        m10 = xy;
        m11 = yy;
        m12 = ty;
    }

    // ===================================================================
    // methods specific to AffineTransform2D class

    /**
     * Returns coefficients of the transform in a linear array of 6 double.
     */
    public double[] getCoefficients() {
        double[] tab = { m00, m01, m02, m10, m11, m12 };
        return tab;
    }

    /**
     * Returns the 3x3 square matrix representing the transform.
     * 
     * @return the 3x3 affine transform representing the matrix
     */
    public double[][] getAffineMatrix() {
        double[][] tab = new double[][] { new double[] { m00, m01, m02 },
                new double[] { m10, m11, m12 }, new double[] { 0, 0, 1 } };
        return tab;
    }

    /**
     * Return the affine transform created by applying first the affine
     * transform given by <code>that</code>, then this affine transform.
     * 
     * @deprecated replaced by concatenate() method (0.6.3)
     * @param that the transform to apply first
     * @return the composition this * that
     */
    @Deprecated
    public AffineTransform2D compose(AffineTransform2D that) {
        double[][] m2 = that.getAffineMatrix();
        double n00 = this.m00*m2[0][0]+this.m01*m2[1][0];
        double n01 = this.m00*m2[0][1]+this.m01*m2[1][1];
        double n02 = this.m00*m2[0][2]+this.m01*m2[1][2]+this.m02;
        double n10 = this.m10*m2[0][0]+this.m11*m2[1][0];
        double n11 = this.m10*m2[0][1]+this.m11*m2[1][1];
        double n12 = this.m10*m2[0][2]+this.m11*m2[1][2]+this.m12;
        return new AffineTransform2D(n00, n01, n02, n10, n11, n12);
    }

    /**
     * Return the affine transform created by applying first the affine
     * transform given by <code>that</code>, then this affine transform. This
     * the equivalent method of the 'concatenate' method in
     * java.awt.geom.AffineTransform.
     * 
     * @param that the transform to apply first
     * @return the composition this * that
     * @since 0.6.3
     */
    public AffineTransform2D concatenate(AffineTransform2D that) {
        double[][] m2 = that.getAffineMatrix();
        double n00 = this.m00*m2[0][0]+this.m01*m2[1][0];
        double n01 = this.m00*m2[0][1]+this.m01*m2[1][1];
        double n02 = this.m00*m2[0][2]+this.m01*m2[1][2]+this.m02;
        double n10 = this.m10*m2[0][0]+this.m11*m2[1][0];
        double n11 = this.m10*m2[0][1]+this.m11*m2[1][1];
        double n12 = this.m10*m2[0][2]+this.m11*m2[1][2]+this.m12;
        return new AffineTransform2D(n00, n01, n02, n10, n11, n12);
    }

    /**
     * Return the affine transform created by applying first this affine
     * transform, then the affine transform given by <code>that</code>. This
     * the equivalent method of the 'preConcatenate' method in
     * java.awt.geom.AffineTransform. <code><pre>
     * shape = shape.transform(T1.chain(T2).chain(T3));
     * </pre></code> is equivalent to the sequence: <code><pre>
     * shape = shape.transform(T1);
     * shape = shape.transform(T2);
     * shape = shape.transform(T3);
     * </pre></code>
     * 
     * @param that the transform to apply in a second step
     * @return the composition that * this
     * @since 0.6.3
     */
    public AffineTransform2D chain(AffineTransform2D that) {
        double[][] m2 = that.getAffineMatrix();
        return new AffineTransform2D(
                m2[0][0]*this.m00+m2[0][1]*this.m10,
                m2[0][0]*this.m01+m2[0][1]*this.m11,
                m2[0][0]*this.m02+m2[0][1]*this.m12+m2[0][2],
                m2[1][0]*this.m00+m2[1][1]*this.m10, 
                m2[1][0]*this.m01+m2[1][1]*this.m11,
                m2[1][0]*this.m02+m2[1][1]*this.m12+m2[1][2]);
    }

    /**
     * Return the affine transform created by applying first this affine
     * transform, then the affine transform given by <code>that</code>. This
     * the equivalent method of the 'preConcatenate' method in
     * java.awt.geom.AffineTransform.
     * 
     * @param that the transform to apply in a second step
     * @return the composition that * this
     * @since 0.6.3
     */
    public AffineTransform2D preConcatenate(AffineTransform2D that) {
        return this.chain(that);
    }

    // ===================================================================
    // methods testing type of transform

    public boolean isSimilarity() {
        return AffineTransform2D.isSimilarity(this);
    }

    public boolean isMotion() {
        return AffineTransform2D.isMotion(this);
    }

    public boolean isIsometry() {
        return AffineTransform2D.isIsometry(this);
    }

    public boolean isDirect() {
        return AffineTransform2D.isDirect(this);
    }

    public boolean isIdentity() {
        return AffineTransform2D.isIdentity(this);
    }

    // ===================================================================
    // implementations of Bijection2D methods

    /**
     * Return the inverse transform. If the transform is not invertible, throws
     * a new NonInvertibleTransformException.
     * 
     * @since 0.6.3
     */
    public AffineTransform2D invert() {
        double det = m00*m11-m10*m01;

        if (Math.abs(det)<Shape2D.ACCURACY)
            throw new NonInvertibleTransformException();

        return new AffineTransform2D(
                m11/det, -m01/det, (m01*m12-m02*m11)/det,
                -m10/det, m00/det, (m02*m10-m00*m12)/det);
    }

    /**
     * Return the inverse transform. If the transform is not invertible, throws
     * a new NonInvertibleTransformException.
     * 
     * @deprecated use invert() method instead (0.6.3)
     */
    @Deprecated
    public AffineTransform2D getInverseTransform() {
        return this.invert();
    }

    // ===================================================================
    // implementations of Transform2D methods

    public Point2D[] transform(java.awt.geom.Point2D[] src, Point2D[] dst) {
        if (dst==null)
            dst = new Point2D[src.length];
        if (dst[0]==null)
            for (int i = 0; i<src.length; i++)
                dst[i] = new Point2D();

        double coef[] = getCoefficients();

        for (int i = 0; i<src.length; i++)
            dst[i].setLocation(new Point2D(
                    src[i].getX()*coef[0]+src[i].getY()*coef[1]+coef[2],
                    src[i].getX()*coef[3]+src[i].getY()*coef[4]+coef[5]));
        return dst;
    }

    public Point2D transform(java.awt.geom.Point2D src) {
        double coef[] = this.getCoefficients();
        Point2D dst = new Point2D(
                src.getX()*coef[0]+src.getY()*coef[1]+coef[2], 
                src.getX()*coef[3]+src.getY()*coef[4]+coef[5]);
        return dst;
    }

    /**
     * deprecated use transform() instead. (0.7.0)
     */
    @Deprecated
    public Point2D transform(java.awt.geom.Point2D src, Point2D dst) {
        double coef[] = getCoefficients();
        if (dst==null)
            dst = new Point2D();
        dst.setLocation(
                src.getX()*coef[0]+src.getY()*coef[1]+coef[2], 
                src.getX()*coef[3]+src.getY()*coef[4]+coef[5]);
        return dst;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AffineTransform2D))
            return false;

        double[] tab1 = this.getCoefficients();
        double[] tab2 = ((AffineTransform2D) obj).getCoefficients();

        for (int i = 0; i<6; i++)
            if (Math.abs(tab1[i]-tab2[i])>Shape2D.ACCURACY)
                return false;

        return true;
    }
    
    @Override
    public AffineTransform2D clone() {
        return new AffineTransform2D(m00, m01, m02, m10, m11, m12);
    }
}