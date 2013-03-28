/**
 * 
 */

package math.geom3d.curve;

import java.util.Collection;

import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.transform.AffineTransform3D;

/**
 * Interface for 3D space curve. Curve can be continuous, or a set of continuous
 * curves.
 * 
 * @author dlegland
 */
public interface Curve3D extends Shape3D {

    /**
     * Get value of parameter t for the first point of the curve. It can be
     * -Infinity, in this case the piece of curve is not bounded.
     */
    public abstract double getT0();

    /**
     * Get value of parameter t for the last point of the curve. It can be
     * +Infinity, in this case the piece of curve is not bounded.
     */
    public abstract double getT1();

    /**
     * Gets the point from a parametric representation of the curve. If the
     * parameter lies outside the definition range, the parameter corresponding
     * to the closest bound is used instead. This method can be used to draw an
     * approximated outline of a curve, by selecting multiple values for t and
     * drawing lines between them.
     */
    public abstract Point3D getPoint(double t);

    /**
     * Same as getPoint(t), but gives the point as a parameter. This avoids
     * repetitive memory allocations.
     */
    public abstract Point3D getPoint(double t, Point3D point);

    /**
     * Get the first point of the curve. It must returns the same result as
     * <code>getPoint(getT0())</code>.
     * 
     * @return the first point of the curve
     */
    public abstract Point3D getFirstPoint();

    /**
     * Get the last point of the curve. It must returns the same result as
     * <code>getPoint(getT1())</code>.
     * 
     * @return the last point of the curve.
     */
    public abstract Point3D getLastPoint();

    /**
     * Returns a set of singular points, i. e. which do not locally admit
     * derivative.
     * 
     * @return a collection of Point3D.
     */
    public abstract Collection<Point3D> getSingularPoints();

    /**
     * Get position of the point on the curve. If the point does not belong to
     * the curve, return Double.NaN.
     * 
     * @param point a point belonging to the curve
     * @return the position of the point on the curve
     */
    public abstract double getPosition(Point3D point);

    /**
     * Returns the position of the closest orthogonal projection of the point on
     * the curve, or of the closest singular point. This function should always
     * returns a valid value.
     * 
     * @param point a point to project
     * @return the position of the closest orthogonal projection
     */
    public abstract double project(Point3D point);

    /**
     * Returns the curve with same trace on the plane with parametrization in
     * reverse order.
     */
    public abstract Curve3D getReverseCurve();

    /**
     * Returns the collection of continuous curves which constitute this curve.
     * 
     * @return a collection of continuous curves.
     */
    public abstract Collection<? extends ContinuousCurve3D> getContinuousCurves();

    /**
     * Returns a portion of the original curve, delimited by two positions on
     * the curve.
     * 
     * @param t0 position of the start of the sub-curve
     * @param t1 position of the end of the sub-curve
     * @return the portion of original curve comprised between t0 and t1.
     */
    public abstract Curve3D getSubCurve(double t0, double t1);

    /**
     * Transforms the curve by an affine transform. The result is an instance of
     * Curve3D.
     */
    public abstract Curve3D transform(AffineTransform3D trans);

    // /**
    // * When a curve is clipped, the result is a set of curves.
    // */
    // public abstract CurveSet2D<? extends Curve3D> clip(Box2D box);
}
