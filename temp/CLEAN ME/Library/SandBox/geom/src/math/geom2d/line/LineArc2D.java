/* file : LineArc2D.java
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
 * Created on 24 déc. 2005
 *
 */

package math.geom2d.line;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.UnboundedShapeException;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.ContinuousOrientedCurve2D;
import math.geom2d.polygon.Polyline2D;

/**
 * LineArc2D is a generic class to represent edges, straight lines, and rays.
 * It is defined like other linear shapes: origin point, and direction vector.
 * Moreover, two internal variables t0 and t1 define the limit of the object
 * (with t0<t1). t0=0 and t1=1: this is an edge. t0=-inf and t1=inf: this is a
 * straight line. t0=0 and t1=inf: this is a ray.
 * 
 * @author dlegland
 */
public class LineArc2D extends AbstractLine2D implements SmoothCurve2D,
        ContinuousOrientedCurve2D, Cloneable {

    protected double t0 = 0;
    protected double t1 = 1;

    // ===================================================================
    // Constructors

    /**
     * @param point1 the point located at t=0
     * @param point2 the point located at t=1
     * @param t0 the lower bound of line arc parameterization
     * @param t1 the upper bound of line arc parameterization
     */
    public LineArc2D(Point2D point1, Point2D point2, double t0, double t1) {
        this(point1.getX(), point1.getY(), point2.getX(), point2.getY(), t0, t1);
    }

    /**
     * Construct a line arc contained in the same straight line as first
     * argument, with bounds of arc given by t0 and t1
     * 
     * @param line an object defining the supporting line
     * @param t0 the lower bound of line arc parameterization
     * @param t1 the upper bound of line arc parameterization
     */
    public LineArc2D(LinearShape2D line, double t0, double t1) {
        super(line);
        this.t0 = t0;
        this.t1 = t1;
    }

    /**
     * Construction by copy of another line arc
     * 
     * @param line the line to copy
     */
    public LineArc2D(LineArc2D line) {
        this(line.x0, line.y0, line.dx, line.dy, line.t0, line.t1);
    }

    /**
     * Construct a line arc by the coordinate of two points and two positions on
     * the line.
     * 
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param dx the x-coordinate of the direction vector
     * @param dy the y-coordinate of the direction vector
     * @param t0 the starting position of the arc
     * @param t1 the ending position of the arc
     */
    public LineArc2D(double x1, double y1, double dx, double dy, double t0,
            double t1) {
        super(x1, y1, dx, dy);
        this.t0 = t0;
        this.t1 = t1;
    }

    // ===================================================================
    // methods specific to LineArc2D

    /**
     * Returns the length of the edge.
     */
    public double getLength() {
        if (t0!=Double.NEGATIVE_INFINITY&&t1!=Double.POSITIVE_INFINITY)
            return getPoint1().getDistance(getPoint2());
        else
            return Double.POSITIVE_INFINITY;
    }

    /**
     * Return the first point of the edge. In the case of a line, or a ray
     * starting from -infinity, throws an UnboundedShapeException.
     * 
     * @return the first point of the arc
     */
    public Point2D getPoint1() {
        if (t0!=Double.NEGATIVE_INFINITY)
            return new Point2D(x0+t0*dx, y0+t0*dy);
        else
            throw new UnboundedShapeException();
    }

    /**
     * Return the last point of the edge. In the case of a line, or a ray ending
     * at infinity, throws an UnboundedShapeException.
     * 
     * @return the last point of the arc.
     */
    public Point2D getPoint2() {
        if (t1!=Double.POSITIVE_INFINITY)
            return new Point2D(x0+t1*dx, y0+t1*dy);
        else
            throw new UnboundedShapeException();
    }

    public double getX1() {
        if (t0!=Double.NEGATIVE_INFINITY)
            return x0+t0*dx;
        else
            return Double.NEGATIVE_INFINITY;
    }

    public double getY1() {
        if (t0!=Double.NEGATIVE_INFINITY)
            return y0+t0*dy;
        else
            return Double.NEGATIVE_INFINITY;
    }

    public double getX2() {
        if (t1!=Double.POSITIVE_INFINITY)
            return x0+t1*dx;
        else
            return Double.POSITIVE_INFINITY;
    }

    public double getY2() {
        if (t1!=Double.POSITIVE_INFINITY)
            return y0+t1*dy;
        else
            return Double.POSITIVE_INFINITY;
    }

    // ===================================================================
    // methods of ContinuousCurve2D interface

    public Polyline2D getAsPolyline(int n) {
        if (!this.isBounded())
            throw new UnboundedShapeException();

        Point2D[] points = new Point2D[n+1];
        double t0 = this.getT0();
        double t1 = this.getT1();
        double dt = (t1-t0)/n;
        for (int i = 0; i<n; i++)
            points[i] = this.getPoint(i*dt+t0);
        return new Polyline2D(points);
    }

    // ===================================================================
    // methods of Curve2D interface

    /**
     * Returns the parameter of the first point of the line arc, 
     * arbitrarily set to 0.
     */
    public double getT0() {
        return t0;
    }

    /**
     * Returns the parameter of the last point of the line arc, 
     * arbitrarily set to 1.
     */
    public double getT1() {
        return t1;
    }

    public Point2D getPoint(double t) {
        if (t<t0)
            t = t0;
        if (t>t1)
            t = t1;

        if (Double.isInfinite(t))
            throw new UnboundedShapeException();
        else
            return new Point2D(x0+dx*t, y0+dy*t);
    }

    /**
     * Return the first point of the edge. In the case of a line, or a ray
     * starting from -infinity, returns Point2D.INFINITY_POINT.
     * 
     * @return the last point of the arc
     */
    public Point2D getFirstPoint() {
        if (!Double.isInfinite(t0))
            return new Point2D(x0+t0*dx, y0+t0*dy);
        else
            throw new UnboundedShapeException();
    }

    /**
     * Return the last point of the edge. In the case of a line, or a ray ending
     * at infinity, returns Point2D.INFINITY_POINT.
     * 
     * @return the last point of the arc
     */
    public Point2D getLastPoint() {
        if (!Double.isInfinite(t1))
            return new Point2D(x0+t1*dx, y0+t1*dy);
        else
            throw new UnboundedShapeException();
    }

    public Collection<Point2D> getSingularPoints() {
        ArrayList<Point2D> list = new ArrayList<Point2D>(2);
        if (t0!=Double.NEGATIVE_INFINITY)
            list.add(this.getFirstPoint());
        if (t1!=Double.POSITIVE_INFINITY)
            list.add(this.getLastPoint());
        return list;
    }

    public boolean isSingular(double pos) {
        if (Math.abs(pos-t0)<Shape2D.ACCURACY)
            return true;
        if (Math.abs(pos-t1)<Shape2D.ACCURACY)
            return true;
        return false;
    }

    @Override
    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    /**
     * Returns the line arc which have the same trace, but has the inverse
     * parametrization.
     */
    public LineArc2D getReverseCurve() {
        return new LineArc2D(x0, y0, -dx, -dy, -t1, -t0);
    }

    /**
     * Returns a new LineArc2D, which is the portion of this LineArc2D delimited
     * by parameters t0 and t1.
     */
    @Override
    public LineArc2D getSubCurve(double t0, double t1) {
        t0 = Math.max(t0, this.getT0());
        t1 = Math.min(t1, this.getT1());
        return new LineArc2D(this, t0, t1);
    }

    // ===================================================================
    // methods of Shape2D interface

    /** return true if both t0 and t1 are different from infinity. */
    public boolean isBounded() {
        if (t1==Double.POSITIVE_INFINITY)
            return false;
        if (t0==Double.NEGATIVE_INFINITY)
            return false;
        return true;
    }

    /**
     * Get the distance of the point (x, y) to this object.
     */
    @Override
    public double getDistance(double x, double y) {
        Point2D proj = super.getProjectedPoint(x, y);
        if (contains(proj))
            return proj.distance(x, y);
        double d1 = Math.sqrt((x0+t0*dx-x)*(x0+t0*dx-x)+(y0+t0*dy-y)
                *(y0+t0*dy-y));
        double d2 = Math.sqrt((x0+t1*dx-x)*(x0+t1*dx-x)+(y0+t1*dy-y)
                *(y0+t1*dy-y));
        return Math.min(d1, d2);
    }

    public Box2D getBoundingBox() {
        return new Box2D(x0+t0*dx, x0+t1*dx, y0+t0*dy, y0+t1*dy);
    }

    // ===================================================================
    // methods of Shape interface

    @Override
    public boolean contains(java.awt.geom.Point2D pt) {
        return contains(pt.getX(), pt.getY());
    }

    public boolean contains(double xp, double yp) {
        if (!super.supportContains(xp, yp))
            return false;

        // compute position on the line
        double t = getPositionOnLine(xp, yp);

        if (t-t0<-ACCURACY)
            return false;
        if (t-t1>ACCURACY)
            return false;

        return true;
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        if (!this.isBounded())
            throw new UnboundedShapeException();
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        path.moveTo((float) (x0+t0*dx), (float) (y0+t0*dy));
        path.lineTo((float) (x0+t1*dx), (float) (y0+t1*dy));
        return path;
    }

    /**
     * Appends a line to the current path. If t0 or t1 is infinite, throws a new
     * UnboundedShapeException.
     * 
     * @param path the path to modify
     * @return the modified path
     */
    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        if (!this.isBounded())
            throw new UnboundedShapeException();
        if (t0==Double.NEGATIVE_INFINITY)
            return path;
        if (t1==Double.POSITIVE_INFINITY)
            return path;
        path.lineTo((float) getX1(), (float) getY1());
        path.lineTo((float) getX2(), (float) getY2());
        return path;
    }

    public void draw(Graphics2D g) {
        g.draw(this.getGeneralPath());
    }

    @Override
    public LineArc2D transform(AffineTransform2D trans) {
        double[] tab = trans.getCoefficients();
        double x1 = x0*tab[0]+y0*tab[1]+tab[2];
        double y1 = x0*tab[3]+y0*tab[4]+tab[5];
        return new LineArc2D(x1, y1, dx*tab[0]+dy*tab[1], dx*tab[3]+dy*tab[4],
                t0, t1);
    }

    @Override
    public String toString() {
        return Double.toString(x0).concat(new String(" ")).concat(
                Double.toString(y0)).concat(new String(" ")).concat(
                Double.toString(dx)).concat(new String(" ")).concat(
                Double.toString(dy));
    }

    // ===================================================================
    // methods of Object interface

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LineArc2D))
            return false;
        LineArc2D arc = (LineArc2D) obj;

        // First check if two arcs lie on the same line
        if (!this.isColinear(arc))
            return false;

        // Check limits for straight lines
        if (t0==Double.NEGATIVE_INFINITY&&t1==Double.POSITIVE_INFINITY) {
            // Check limits
            if (arc.t0!=Double.NEGATIVE_INFINITY)
                return false;
            if (arc.t1!=Double.POSITIVE_INFINITY)
                return false;
            return true;
        }

        // Check limits for rays
        if (t0==Double.NEGATIVE_INFINITY) {
            // Check limits
            if (arc.t0==Double.NEGATIVE_INFINITY)
                return this.getPoint2().getDistance(arc.getPoint2())<Shape2D.ACCURACY;
            if (arc.t1==Double.POSITIVE_INFINITY)
                return this.getPoint2().getDistance(arc.getPoint1())<Shape2D.ACCURACY;
            return false;
        }
        if (t1==Double.POSITIVE_INFINITY) {
            // Check limits
            if (arc.t0==Double.NEGATIVE_INFINITY)
                return this.getPoint1().getDistance(arc.getPoint2())<Shape2D.ACCURACY;
            if (arc.t1==Double.POSITIVE_INFINITY)
                return this.getPoint1().getDistance(arc.getPoint1())<Shape2D.ACCURACY;
            return false;
        }

        // current line arc is neither a line nor an arc, check that arc is an
        // edge
        if (arc.t0==Double.NEGATIVE_INFINITY||arc.t0==Double.POSITIVE_INFINITY)
            return false;
        if (arc.t1==Double.NEGATIVE_INFINITY||arc.t1==Double.POSITIVE_INFINITY)
            return false;

        // We still have to test the case of edges
        if (getPoint1().getDistance(arc.getPoint1())<ACCURACY)
            return getPoint2().getDistance(arc.getPoint2())<ACCURACY;

        if (getPoint1().getDistance(arc.getPoint2())>ACCURACY)
            return false;
        if (getPoint2().getDistance(arc.getPoint1())>ACCURACY)
            return false;
        return true;
    }
    
    @Override
    public LineArc2D clone() {
        return new LineArc2D(x0, y0, dx, dy, t0, t1);
    }
}
