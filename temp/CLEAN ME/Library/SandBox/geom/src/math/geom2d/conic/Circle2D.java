/* file : Circle2D.java
 * 
 * Project : geometry
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
 * 
 * Created on 30 avr. 2006
 *
 */

package math.geom2d.conic;

import java.awt.Graphics2D;
import java.util.*;

import math.geom2d.Angle2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Vector2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.SmoothOrientedCurve2D;
import math.geom2d.line.AbstractLine2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.line.StraightLine2D;

/**
 * A circle in the plane, defined as the set of points located at an equal
 * distance from the circle center. A circle is a particular ellipse, with first
 * and second axis length equal.
 * 
 * @author dlegland
 */
public class Circle2D extends Ellipse2D implements Cloneable {

    /** the radius of the circle. */
    protected double r = 0;

    // ===================================================================
    // Static methods

    public final static Collection<Point2D> getIntersections(Circle2D circle1,
            Circle2D circle2) {
        ArrayList<Point2D> intersections = new ArrayList<Point2D>(2);

        Point2D center1 = circle1.getCenter();
        Point2D center2 = circle2.getCenter();
        double r1 = circle1.getRadius();
        double r2 = circle2.getRadius();

        double d = Point2D.getDistance(center1, center2);

        // case of no intersection
        if (d<Math.abs(r1-r2)|d>(r1+r2))
            return intersections;

        // Angle of line from center1 to center2
        double angle = Angle2D.getHorizontalAngle(center1, center2);

        // position of intermediate point
        double d1 = d/2+(r1*r1-r2*r2)/(2*d);
        Point2D tmp = Point2D.createPolar(center1, d1, angle);

        // Add the 2 intersection points
        double h = Math.sqrt(r1*r1-d1*d1);
        intersections.add(Point2D.createPolar(tmp, h, angle+Math.PI/2));
        intersections.add(Point2D.createPolar(tmp, h, angle-Math.PI/2));

        return intersections;
    }

    /**
     * Creates a circle containing 3 points.
     */
    public final static Circle2D create(Point2D p1, Point2D p2, Point2D p3) {
        StraightLine2D line12 = StraightLine2D.createMedian(p1, p2);
        StraightLine2D line23 = StraightLine2D.createMedian(p2, p3);

        if (AbstractLine2D.isParallel(line12, line23))
            return null;

        Point2D center = AbstractLine2D.getIntersection(line12, line23);
        double radius = Point2D.getDistance(center, p2);

        return new Circle2D(center, radius);
    }

    // ===================================================================
    // Constructors

    /** Empty constructor: center 0,0 and radius 0. */
    public Circle2D() {
        this(0, 0, 0, true);
    }

    /** Create a new circle with specified point center and radius */
    public Circle2D(Point2D center, double radius) {
        this(center.getX(), center.getY(), radius, true);
    }

    /** Create a new circle with specified center, radius and orientation */
    public Circle2D(Point2D center, double radius, boolean direct) {
        this(center.getX(), center.getY(), radius, direct);
    }

    /** Create a new circle with specified center and radius */
    public Circle2D(double xcenter, double ycenter, double radius) {
        this(xcenter, ycenter, radius, true);
    }

    /** Create a new circle with specified center, radius and orientation. */
    public Circle2D(double xcenter, double ycenter, double radius,
            boolean direct) {
        super(xcenter, ycenter, radius, radius, 0, direct);
        this.r = radius;
    }

    // ===================================================================
    // methods specific to class Circle2D

    /** Returns perimeter of the circle (equal to 2*PI*radius). */
    public double getLength() {
        return r*Math.PI*2;
    }

    public double getRadius() {
        return r;
    }

    /**
     * @deprecated conics will become immutable in a future release
     */
    @Deprecated
    public void setRadius(double radius) {
        this.r = radius;
        this.r1 = this.r2 = radius;
    }

    /**
     * @deprecated conics will become immutable in a future release
     */
    @Deprecated
    public void setCircle(double xc, double yc, double r) {
        this.xc = xc;
        this.yc = yc;
        this.r = r;
        this.r1 = r;
        this.r2 = r;
    }

    /**
     * @deprecated conics will become immutable in a future release
     */
    @Deprecated
    public void setCircle(Point2D center, double r) {
        this.xc = center.getX();
        this.yc = center.getY();
        this.r = r;
        this.r1 = r;
        this.r2 = r;
    }

    /**
     * Return the parallel circle located at a distance d from this circle. For
     * direct circle, distance is positive outside of the circle, and negative
     * inside
     */
    @Override
    public Circle2D getParallel(double d) {
        return new Circle2D(xc, yc, Math.abs(r1+d), direct);
    }

    // ===================================================================
    // methods of Conic2D

    @Override
    public Type getConicType() {
        return Conic2D.Type.CIRCLE;
    }

    @Override
    public boolean isCircle() {
        return true;
    }

    /**
     * Returns cartesian equation of the circle:
     * <p>
     * <code>(x-xc)^2 + (y-yc)^2 = r^2</code>, giving:
     * <p>
     * <code>x^2 + 0*x*y + y^2 -2*xc*x -2*yc*y + xc*xc+yc*yc-r*r = 0</code>.
     */
    @Override
    public double[] getConicCoefficients() {
        return new double[] { 1, 0, 1, -2*xc, -2*yc, xc*xc+yc*yc-r*r };
    }

    /**
     * Return 0, which is the eccentricity of a circle by definition.
     */
    @Override
    public double getEccentricity() {
        return 0;
    }

    /**
     * Return the first focus, whihc for a circle is the same point as the
     * center.
     */
    @Override
    public Point2D getFocus1() {
        return new Point2D(xc, yc);
    }

    /**
     * Return the second focus, which for a circle is the same point as the
     * center.
     */
    @Override
    public Point2D getFocus2() {
        return new Point2D(xc, yc);
    }

    // ===================================================================
    // methods of SmoothCurve2D interface

    @Override
    public Vector2D getTangent(double t) {
        if (!direct)
            t = -t;
        double cot  = Math.cos(theta);
        double sit  = Math.sin(theta);
        double cost = Math.cos(t);
        double sint = Math.sin(t);

        if (direct)
            return new Vector2D(
                    -r*sint*cot-r*cost*sit, 
                    -r*sint*sit+r*cost*cot);
        else
            return new Vector2D(
                    r*sint*cot+r*cost*sit,
                    r*sint*sit-r*cost*cot);
    }

    // ===================================================================
    // methods of ContinuousCurve2D interface

    // ===================================================================
    // methods of OrientedCurve2D interface

    /**
     * Test whether the point is inside the circle. The test is performed by
     * translating the point, and re-scaling it such that its coordinates are
     * expressed in unit circle basis.
     */
    @Override
    public boolean isInside(java.awt.geom.Point2D point) {
        double xp = (point.getX()-this.xc)/this.r;
        double yp = (point.getY()-this.yc)/this.r;
        return (xp*xp+yp*yp<1)^!direct;
    }

    @Override
    public double getSignedDistance(java.awt.geom.Point2D point) {
        return getSignedDistance(point.getX(), point.getY());
    }

    @Override
    public double getSignedDistance(double x, double y) {
        if (direct)
            return Point2D.getDistance(xc, yc, x, y)-r;
        else
            return r-Point2D.getDistance(xc, yc, x, y);
    }

    // ===================================================================
    // methods of Curve2D interface

    /**
     * Get the position of the curve from internal parametric representation,
     * depending on the parameter t. This parameter is between the two limits 0
     * and 2*Math.PI.
     */
    @Override
    public Point2D getPoint(double t) {
        double angle = theta+t;
        if (!direct)
            angle = theta-t;
        return new Point2D(xc+r*Math.cos(angle), yc+r*Math.sin(angle));
    }

    /**
     * Get the first point of the circle, which is the same as the last point.
     * 
     * @return the first point of the curve
     */
    @Override
    public Point2D getFirstPoint() {
        return new Point2D(xc+r*Math.cos(theta), yc+r*Math.sin(theta));
    }

    /**
     * Get the last point of the circle, which is the same as the first point.
     * 
     * @return the last point of the curve.
     */
    @Override
    public Point2D getLastPoint() {
        return new Point2D(xc+r*Math.cos(theta), yc+r*Math.sin(theta));
    }

    @Override
    public double getPosition(java.awt.geom.Point2D point) {
        double angle = 
            Angle2D.getHorizontalAngle(xc, yc, point.getX(), point.getY());
        if (direct)
            return Angle2D.formatAngle(angle-theta);
        else
            return Angle2D.formatAngle(theta-angle);
    }

    /**
     * Returns the circle with same center and same radius, but with the other
     * orientation.
     */
    @Override
    public Circle2D getReverseCurve() {
        return new Circle2D(this.getCenter().getX(), this.getCenter().getY(),
                this.getRadius(), !this.direct);
    }

    /**
     * Returns a new CircleArc2D. t0 and t1 are position on circle.
     */
    @Override
    public CircleArc2D getSubCurve(double t0, double t1) {
        double startAngle, extent;
        if (this.direct) {
            startAngle = t0;
            extent = Angle2D.formatAngle(t1-t0);
        } else {
            extent = -Angle2D.formatAngle(t1-t0);
            startAngle = Angle2D.formatAngle(-t0);
        }
        return new CircleArc2D(this, startAngle, extent);
    }

    // ===================================================================
    // methods of Shape2D interface

    @Override
    public double getDistance(java.awt.geom.Point2D point) {
        return Math.abs(Point2D.getDistance(xc, yc, point.getX(),
                point.getY())
                -r);
    }

    @Override
    public double getDistance(double x, double y) {
        return Math.abs(Point2D.getDistance(xc, yc, x, y)-r);
    }

    /**
     * Compute intersections of the circle with a line. Return an array of
     * Point2D, of size 0, 1 or 2 depending on the distance between circle and
     * line. If there are 2 intersections points, the first one in the array is
     * the first one on the line.
     */
    @Override
    public Collection<Point2D> getIntersections(LinearShape2D line) {
        // initialize array of points
        ArrayList<Point2D> intersections = new ArrayList<Point2D>();

        // Compute line perpendicular to the test line, and going through the
        // circle center
        Point2D center = new Point2D(xc, yc);
        StraightLine2D perp = StraightLine2D.createPerpendicular(line, center);

        // Compute distance between line and circle center
        Point2D inter = perp.getIntersection(new StraightLine2D(line));
        double dist = inter.getDistance(xc, yc);

        // if the distance is the radius of the circle, return the
        // intersection point
        if (Math.abs(dist-r)<Shape2D.ACCURACY) {
            if (line.contains(inter))
                intersections.add(inter);
            return intersections;
        }

        // compute angle of the line, and distance between 'inter' point and
        // each intersection point
        double angle = line.getHorizontalAngle();
        double d2 = Math.sqrt(r*r-dist*dist);

        // Compute position and angle of intersection points
        Point2D p1 = Point2D.createPolar(inter, d2, angle+Math.PI);
        Point2D p2 = Point2D.createPolar(inter, d2, angle);

        if (line.contains(p1))
            intersections.add(p1);
        if (line.contains(p2))
            intersections.add(p2);

        return intersections;
    }

    /**
     * Clip the circle by a box. The result is an instance of CurveSet2D<SmoothOrientedCurve2D>,
     * which contains only instances of CircleArc2D or Circle2D. If the circle
     * is not clipped, the result is an instance of CurveSet2D<SmoothOrientedCurve2D>
     * which contains 0 curves.
     */
    @Override
    public CurveSet2D<? extends SmoothOrientedCurve2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<SmoothCurve2D> set = Curve2DUtils.clipSmoothCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<SmoothOrientedCurve2D> result = new CurveSet2D<SmoothOrientedCurve2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof CircleArc2D)
                result.addCurve((CircleArc2D) curve);
            if (curve instanceof Circle2D)
                result.addCurve((Circle2D) curve);
        }
        return result;
    }

    // ===================================================================
    // methods of Shape interface

    /**
     * Return true if the point (x, y) lies exactly on the circle.
     */
    @Override
    public boolean contains(double x, double y) {
        return Math.abs(getDistance(x, y))<=Shape2D.ACCURACY;
    }

    @Override
    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        double cost, sint;

        if (direct)
            for (double t = .1; t<Math.PI*2; t += .1) {
                cost = Math.cos(t);
                sint = Math.sin(t);
                path.lineTo(
                        (float) (xc+r*cost*cot-r*sint*sit),
                        (float) (yc+r*cost*sit+r*sint*cot));
            }
        else
            for (double t = .1; t<Math.PI*2; t += .1) {
                cost = Math.cos(t);
                sint = Math.sin(t);
                path.lineTo(
                        (float) (xc+r*cost*cot+r*sint*sit),
                        (float) (yc+r*cost*sit-r*sint*cot));
            }

        // line to first point
        path.lineTo((float) (xc+r*cot), (float) (yc+r*sit));

        return path;
    }

    @Override
    public void draw(Graphics2D g2) {
        java.awt.geom.Ellipse2D.Double ellipse = 
            new java.awt.geom.Ellipse2D.Double(xc-r, yc-r, 2*r, 2*r);
        g2.draw(ellipse);
    }

    // ===================================================================
    // methods of Object interface

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ellipse2D))
            return false;

        if (obj instanceof Circle2D) {
            Circle2D circle = (Circle2D) obj;

            if (Math.abs(circle.xc-xc)>Shape2D.ACCURACY)
                return false;
            if (Math.abs(circle.yc-yc)>Shape2D.ACCURACY)
                return false;
            if (Math.abs(circle.r-r)>Shape2D.ACCURACY)
                return false;
            if (circle.direct!=direct)
                return false;
            return true;
        }
        return super.equals(obj);
    }

    @Override
    public Circle2D clone() {
        return new Circle2D(xc, yc, r, direct);
    }
    
    @Override
    public String toString() {
        return String.format(Locale.US, 
                "Circle2D (%7.2f, %7.2f, %7.2f)", xc, yc, r);
    }
}
