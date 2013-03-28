/* File LineObject2D.java 
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

package math.geom2d.line;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Angle2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Vector2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.polygon.Polyline2D;

// Imports

/**
 * Line object defined from 2 points. This object keep points reference in
 * memory, and recomputes properties directly from points. LineObject2D is
 * mutable.
 * <p>
 * Example :
 * <p>
 * <code>
 * // Create an Edge2D<br>
 * LineObject2D line = new LineObject2D(new Point2D(0, 0), new Point2D(1, 2));<br>
 * // Change direction of line, by changing second point :<br>
 * line.setPoint2(new Point2D(4, 5));<br>
 * // Change position and direction of the line, by changing first point. <br>
 * // 'line' is now the edge (2,3)-(4,5)<br>
 * line.setPoint1(new Point2D(2, 3));<br>
 * </code>
 * <p>
 * <p>
 * This class is maybe slower than Edge2D or StraightLine2D, because parameters
 * are updated each time a computation is made, causing lot of additional
 * processing.
 * @deprecated use Line2D instead
 */
@Deprecated
public class LineObject2D extends AbstractLine2D implements Cloneable {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    /**
     * The origin point.
     */
    private Point2D point1;
    
    /**
     * The destination point.
     */
    private Point2D point2;

    // ===================================================================
    // constructors

    /** Define a new LineObject2D with two extremities. */
    public LineObject2D(Point2D point1, Point2D point2) {
        super(point1.getX(), point1.getY(), 
                point2.getX()-point1.getX(), point2.getY()-point1.getY());
        this.point1 = point1;
        this.point2 = point2;
    }

    /** Define a new LineObject2D with two extremities. */
    public LineObject2D(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2-x1, y2-y1);
        point1 = new Point2D(x1, y1);
        point2 = new Point2D(x2, y2);
    }

    // ===================================================================
    // Methods specific to LineObject2D

    /**
     * Recompute (x0,y0) and (dx,dy) from position of points. If point1 is set
     * to null, recompute only (dx,dy). If point2 is set to null, recompute only
     * (x0,y0). If both points are set to null , recompute nothing.
     */
    private void updateParameters() {
        if (point1!=null) {
            x0 = point1.getX();
            y0 = point1.getY();
        }
        if (point2!=null) {
            dx = point2.getX()-x0;
            dy = point2.getY()-y0;
        }
    }

    public void setPoint1(Point2D point) {
        point1 = point;
        updateParameters();
    }

    public void setPoint2(Point2D point) {
        point2 = point;
        updateParameters();
    }

    // ===================================================================
    // accessors

    /**
     * Returns true
     */
    public boolean isBounded() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isColinear(LinearShape2D line) {
        updateParameters();
        return super.isColinear(line);
    }

    /**
     * Test if the this object is parallel to the given one. This method is
     * overloaded to update parameters before computation.
     */
    @Override
    public boolean isParallel(LinearShape2D line) {
        updateParameters();
        return super.isParallel(line);
    }

    /**
     * Get the distance of the point (x, y) to this edge.
     */
    @Override
    public double getDistance(java.awt.geom.Point2D p) {
        return getDistance(p.getX(), p.getY());
    }

    /**
     * Get the distance of the point (x, y) to this edge.
     */
    @Override
    public double getDistance(double x, double y) {
        updateParameters();
        Point2D proj = super.getProjectedPoint(x, y);
        if (contains(proj))
            return proj.distance(x, y);
        double d1 = Double.POSITIVE_INFINITY;
        double d2 = Double.POSITIVE_INFINITY;
        if (point1!=null)
            d1 = Math.sqrt((x0-x)*(x0-x)+(y0-y)*(y0-y));
        if (point2!=null)
            d2 = Math.sqrt((x0+dx-x)*(x0+dx-x)+(y0+dy-y)*(y0+dy-y));
        // System.out.println("dist lineObject2D : " + Math.min(d1, d2));
        return Math.min(d1, d2);
    }

    @Override
    public double getSignedDistance(java.awt.geom.Point2D p) {
        return getSignedDistance(p.getX(), p.getY());
    }

    @Override
    public double getSignedDistance(double x, double y) {
        updateParameters();
        return super.getSignedDistance(x, y);
    }

    @Override
    public double[][] getParametric() {
        updateParameters();
        return super.getParametric();
    }

    @Override
    public double[] getCartesianEquation() {
        updateParameters();
        return super.getCartesianEquation();
    }

    @Override
    public double[] getPolarCoefficients() {
        updateParameters();
        return super.getPolarCoefficients();
    }

    @Override
    public double[] getSignedPolarCoefficients() {
        updateParameters();
        return super.getSignedPolarCoefficients();
    }

    @Override
    public double getHorizontalAngle() {
        updateParameters();
        return super.getHorizontalAngle();
    }

    @Override
    public Point2D getProjectedPoint(Point2D p) {
        updateParameters();
        return super.getProjectedPoint(p);
    }

    @Override
    public Point2D getProjectedPoint(double x, double y) {
        updateParameters();
        return super.getProjectedPoint(x, y);
    }

    /**
     * Create a straight line parallel to this object, and going through the
     * given point.
     * 
     * @param point the point to go through
     * @return the parallel through the point
     */
    @Override
    public StraightLine2D getParallel(Point2D point) {
        updateParameters();
        return null;
    }

    /**
     * Create a straight line perpendicular to this object, and going through
     * the given point.
     * 
     * @param point : the point to go through
     * @return the perpendicular through point
     */
    @Override
    public StraightLine2D getPerpendicular(Point2D point) {
        updateParameters();
        return super.getPerpendicular(point);
    }

    /**
     * Clip the line object by a box. The result is an instance of CurveSet2D<LineArc2D>,
     * which contains only instances of LineArc2D. If the line object is not
     * clipped, the result is an instance of CurveSet2D<LineArc2D> which
     * contains 0 curves.
     */
    @Override
    public CurveSet2D<? extends LineArc2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<Curve2D> set = Curve2DUtils.clipCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<LineArc2D> result = new CurveSet2D<LineArc2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof LineArc2D)
                result.addCurve((LineArc2D) curve);
        }
        return result;
    }

    /**
     * Return more precise bounds for the LineObject. Return an instance of
     * HRectangle2D.
     */
    public Box2D getBoundingBox() {
        if (point1==null||point2==null)
            return new Box2D(Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY);
        updateParameters();
        return new Box2D(x0, x0+dx, y0, y0+dy);
    }

    /**
     * Returns the length of the edge.
     */
    public double getLength() {
        updateParameters();
        return Math.hypot(dx, dy);
    }

    /**
     * Return the first point of the edge. It corresponds to getPoint(0).
     * 
     * @return the first point.
     */
    public Point2D getPoint1() {
        return point1;
    }

    /**
     * Return the last point of the edge. It corresponds to getPoint(1).
     * 
     * @return the last point.
     */
    public Point2D getPoint2() {
        return point2;
    }

    public double getX1() {
        return point1.getX();
    }

    public double getY1() {
        return point1.getY();
    }

    public double getX2() {
        return point2.getX();
    }

    public double getY2() {
        return point2.getY();
    }

    /**
     * Return the opposite vertex of the edge.
     * 
     * @param point : one of the vertices of the edge
     * @return the other vertex
     */
    public Point2D getOtherPoint(Point2D point) {
        if (point.equals(point1))
            return point2;
        if (point.equals(point2))
            return point1;
        return null;
    }

    // ===================================================================
    // methods inherited from SmoothCurve2D interface

    @Override
    public Vector2D getTangent(double t) {
        updateParameters();
        return new Vector2D(dx, dy);
    }

    /**
     * Returns 0 as every straight object.
     */
    @Override
    public double getCurvature(double t) {
        return 0.0;
    }

    public Polyline2D getAsPolyline(int n) {
        updateParameters();
        Point2D[] points = new Point2D[n+1];
        double t0 = this.getT0();
        double t1 = this.getT1();
        double dt = (t1-t0)/n;
        for (int i = 0; i<n; i++)
            points[i] = this.getPoint(i*dt+t0);
        return new Polyline2D(points);
    }

    // ===================================================================
    // methods inherited from OrientedCurve2D interface

    @Override
    public double getWindingAngle(java.awt.geom.Point2D point) {
        updateParameters();

        double angle0 = super.getHorizontalAngle();
        double angle1 = (angle0+Math.PI)%(2*Math.PI);
        if (point1!=null)
            angle1 = Angle2D.getHorizontalAngle(point.getX(), point.getY(), x0,
                    y0);

        double angle2 = angle0;
        if (point2!=null)
            angle2 = Angle2D.getHorizontalAngle(point.getX(), point.getY(), x0
                    +dx, y0+dy);

        if (this.isInside(point)) {
            if (angle2>angle1)
                return angle2-angle1;
            else
                return 2*Math.PI-angle1+angle2;
        } else {
            if (angle2>angle1)
                return angle2-angle1-2*Math.PI;
            else
                return angle2-angle1;
        }
    }

    @Override
    public boolean isInside(java.awt.geom.Point2D point) {
        return this.getSignedDistance(point.getX(), point.getY())<0;
    }

    // ===================================================================
    // methods inherited from Curve2D interface

    /**
     * Returns 0.
     */
    public double getT0() {
        return 0.0;
    }

    /**
     * Returns 1.
     */
    public double getT1() {
        return 1.0;
    }

    public Point2D getPoint(double t) {
        if (t<0)
            return null;
        if (t>1)
            return null;
        updateParameters();
        return new Point2D(x0+dx*t, y0+dy*t);
    }

    /**
     * Get the first point of the curve.
     * 
     * @return the first point of the curve
     */
    public Point2D getFirstPoint() {
        return point1;
    }

    /**
     * Get the last point of the curve.
     * 
     * @return the last point of the curve.
     */
    public Point2D getLastPoint() {
        return point2;
    }

    public Collection<Point2D> getSingularPoints() {
        ArrayList<Point2D> list = new ArrayList<Point2D>(2);
        list.add(point1);
        list.add(point2);
        return list;
    }

    public boolean isSingular(double pos) {
        if (Double.isInfinite(pos))
            return false;
        if (Math.abs(pos)<Shape2D.ACCURACY&&point1!=null)
            return true;
        if (Math.abs(pos-1)<Shape2D.ACCURACY&&point2!=null)
            return true;
        return false;
    }

    /**
     * Gets position of the point on the line. If point belongs to the line,
     * this position is defined by the ratio :
     * <p>
     * <code> t = (xp - x0)/dx <\code>, or equivalently :<p>
     * <code> t = (yp - y0)/dy <\code>.<p>
     * If point does not belong to edge, return Double.NaN. The current implementation 
     * uses the direction with the biggest derivative, in order to avoid divisions 
     * by zero.
     */
    @Override
    public double getPosition(java.awt.geom.Point2D point) {
        if (!contains(point))
            return Double.NaN;
        // not useful to update, because parameters were updated in contains()
        // method
        // updateParameters();
        if (Math.abs(dx)>Math.abs(dy))
            return (point.getX()-x0)/dx;
        else
            return (point.getY()-y0)/dy;
    }

    @Override
    public double project(java.awt.geom.Point2D point) {
        updateParameters();
        double t;
        if (Math.abs(dx)>Math.abs(dy))
            t = (point.getX()-x0)/dx;
        else
            t = (point.getY()-y0)/dy;
        return Math.min(Math.max(t, getT0()), getT1());
    }

    /**
     * Returns the line object which starts at <code>point2</code> and ends at
     * <code>point1</code>.
     */
    public LineObject2D getReverseCurve() {
        return new LineObject2D(point2, point1);
    }

    @Override
    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    /**
     * Return a new LineArc2D, which is the portion of the linearc delimited by
     * parameters t0 and t1.
     */
    @Override
    public LineArc2D getSubCurve(double t0, double t1) {
        t0 = Math.max(t0, getT0());
        t1 = Math.min(t1, getT1());
        return new LineArc2D(this, t0, t1);
    }

    public void draw(Graphics2D g) {
        g.draw(this.getGeneralPath());
    }

    // ===================================================================
    // methods inherited from Shape2D interface

    @Override
    public LineObject2D transform(AffineTransform2D trans) {
        return new LineObject2D(
                point1.transform(trans), 
                point2.transform(trans));
    }

    // ===================================================================
    // methods inherited from Shape interface

    /**
     * Returns true if the point (x, y) lies on the line, with precision given
     * by Shape2D.ACCURACY.
     */
    public boolean contains(double x, double y) {
        updateParameters();
        boolean b = super.supportContains(x, y);
        double t;
        if (Math.abs(dx)>Math.abs(dy))
            t = (x-x0)/dx;
        else
            t = (y-y0)/dy;

        return t>=0&&t<=1&&b;
    }

    /**
     * Return true if the point p lies on the line, with precision given by
     * Shape2D.ACCURACY.
     */
    @Override
    public boolean contains(java.awt.geom.Point2D p) {
        return contains(p.getX(), p.getY());
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        path.moveTo((float) point1.getX(), (float) point1.getX());
        path.lineTo((float) point2.getX(), (float) point2.getY());
        return path;
    }

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        path.lineTo((float) point1.getX(), (float) point1.getX());
        path.lineTo((float) point2.getX(), (float) point2.getY());
        return path;
    }

    // ===================================================================
    // methods inherited from Object interface

    @Override
    public String toString() {
        updateParameters();
        return Double.toString(x0).concat(new String(" ")).concat(
                Double.toString(y0)).concat(new String(" ")).concat(
                Double.toString(dx)).concat(new String(" ")).concat(
                Double.toString(dy));
    }

    /**
     * Two LineObject2D are equals if the share the two same points, 
     * in the same order.
     * 
     * @param obj the edge to compare to.
     * @return true if extremities of both edges are the same.
     */
    @Override
    public boolean equals(Object obj) {
        // check class
        if(!(obj instanceof LineObject2D))
            return false;
        
        // cast class, and compare members
        LineObject2D edge = (LineObject2D) obj;
        return point1==edge.point1&&point2==edge.point2;
    }
    
    @Override
    public LineObject2D clone() {
        return new LineObject2D(point1.clone(), point2.clone());
    }
}