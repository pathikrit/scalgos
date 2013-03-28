/* File Line2D.java 
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
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Vector2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.OrientedCurve2D;
import math.geom2d.polygon.Polyline2D;

// Imports

/**
 * Line object defined from 2 points. This object keep points reference in
 * memory, and recomputes properties directly from points. Line2D is
 * mutable.
 * <p>
 * Example :
 * <p>
 * <code>
 * // Create an Edge2D<br>
 * Line2D line = new Line2D(new Point2D(0, 0), new Point2D(1, 2));<br>
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
 */
public class Line2D implements LinearShape2D, SmoothCurve2D, OrientedCurve2D, 
Cloneable {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    /**
     * The origin point.
     */
    public Point2D p1;
    
    /**
     * The destination point.
     */
    public Point2D p2;


    // ===================================================================
    // constructors

    /**
     * Checks if two line intersect. Uses the Point2D.ccw() method,
     * which is based on Sedgewick algorithm.
     * 
     * @param edge1 a line object
     * @param edge2 a line object
     * @return true if the 2 line intersect
     */
    public final static boolean intersects(Line2D line1, Line2D line2) {
        Point2D e1p1 = line1.getFirstPoint();
        Point2D e1p2 = line1.getLastPoint();
        Point2D e2p1 = line2.getFirstPoint();
        Point2D e2p2 = line2.getLastPoint();

        boolean b1 = Point2D.ccw(e1p1, e1p2, e2p1)
                *Point2D.ccw(e1p1, e1p2, e2p2)<=0;
        boolean b2 = Point2D.ccw(e2p1, e2p2, e1p1)
                *Point2D.ccw(e2p1, e2p2, e1p2)<=0;
        return b1&&b2;
    }

    // ===================================================================
    // constructors

    /** Define a new Line2D with two extremities. */
    public Line2D(Point2D point1, Point2D point2) {
        this.p1 = point1;
        this.p2 = point2;
    }

    /** Define a new Line2D with two extremities. */
    public Line2D(double x1, double y1, double x2, double y2) {
        p1 = new Point2D(x1, y1);
        p2 = new Point2D(x2, y2);
    }

    // ===================================================================
    // Methods specific to Line2D

    /**
     * Returns the length of the edge.
     */
    public double getLength() {
        return Math.hypot(
                p1.getX()-p2.getX(), 
                p1.getY()-p2.getY());
    }

    /**
     * Return the first point of the edge. It corresponds to getPoint(0).
     * 
     * @return the first point.
     */
    public Point2D getPoint1() {
        return p1;
    }

    /**
     * Return the last point of the edge. It corresponds to getPoint(1).
     * 
     * @return the last point.
     */
    public Point2D getPoint2() {
        return p2;
    }

    public double getX1() {
        return p1.getX();
    }

    public double getY1() {
        return p1.getY();
    }

    public double getX2() {
        return p2.getX();
    }

    public double getY2() {
        return p2.getY();
    }

    /**
     * Return the opposite vertex of the edge.
     * 
     * @param point : one of the vertices of the edge
     * @return the other vertex
     */
    public Point2D getOtherPoint(Point2D point) {
        if (point.equals(p1))
            return p2;
        if (point.equals(p2))
            return p1;
        return null;
    }

    public void setPoint1(Point2D point) {
        p1 = point;
    }

    public void setPoint2(Point2D point) {
        p2 = point;
    }

    // ===================================================================
    // methods implementing the LinearShape2D interface

    public boolean isColinear(LinearShape2D line) {
        return new LineSegment2D(p1, p2).isColinear(line);
    }

    /**
     * Test if the this object is parallel to the given one. This method is
     * overloaded to update parameters before computation.
     */
    public boolean isParallel(LinearShape2D line) {
        return new LineSegment2D(p1, p2).isParallel(line);
    }

    /* (non-Javadoc)
     * @see math.geom2d.line.LinearShape2D#getIntersection(math.geom2d.line.LinearShape2D)
     */
    public Point2D getIntersection(LinearShape2D line) {
        return new LineSegment2D(p1, p2).getIntersection(line);
    }

    /* (non-Javadoc)
     * @see math.geom2d.line.LinearShape2D#getOrigin()
     */
    public Point2D getOrigin() {
        return p1;
    }

    /* (non-Javadoc)
     * @see math.geom2d.line.LinearShape2D#getSupportingLine()
     */
    public StraightLine2D getSupportingLine() {
        return new StraightLine2D(p1, p2);
    }

    /* (non-Javadoc)
     * @see math.geom2d.line.LinearShape2D#getVector()
     */
    public Vector2D getVector() {
        return new Vector2D(p1, p2);
    }

    public double[][] getParametric() {
        return new LineSegment2D(p1, p2).getParametric();
    }

    public double[] getCartesianEquation() {
        return new LineSegment2D(p1, p2).getCartesianEquation();
    }

    public double[] getPolarCoefficients() {
        return new LineSegment2D(p1, p2).getPolarCoefficients();
    }

    public double[] getSignedPolarCoefficients() {
        return new LineSegment2D(p1, p2).getSignedPolarCoefficients();
    }

    public double getHorizontalAngle() {
        return new LineSegment2D(p1, p2).getHorizontalAngle();
    }

    
    // ===================================================================
    // methods implementing the OrientedCurve2D interface
    
    public double getSignedDistance(java.awt.geom.Point2D p) {
        return getSignedDistance(p.getX(), p.getY());
    }

    public double getSignedDistance(double x, double y) {
        return new LineSegment2D(p1, p2).getSignedDistance(x, y);
    }

    
    // ===================================================================
    // methods implementing the ContinuousCurve2D interface
    
    /* (non-Javadoc)
     * @see math.geom2d.curve.ContinuousCurve2D#getSmoothPieces()
     */
    public Collection<? extends Line2D> getSmoothPieces() {
        ArrayList<Line2D> array = new ArrayList<Line2D>(1);
        array.add(this);
        return array;
    }

    /**
     * Returns false.
     * @see math.geom2d.curve.ContinuousCurve2D#isClosed()
     */
    public boolean isClosed() {
        return false;
    }
    
    // ===================================================================
    // methods implementing the Shape2D interface

    /**
     * Get the distance of the point (x, y) to this edge.
     */
    public double getDistance(java.awt.geom.Point2D p) {
        return getDistance(p.getX(), p.getY());
    }

    /**
     * Get the distance of the point (x, y) to this edge.
     */
    public double getDistance(double x, double y) {
        StraightLine2D support = new StraightLine2D(p1, p2);
        Point2D proj = support.getProjectedPoint(x, y);
        if (contains(proj))
            return proj.distance(x, y);
        double d1 = Math.hypot(p1.getX()-x, p1.getY()-y);
        double d2 = Math.hypot(p2.getX()-x, p2.getY()-y);
        // System.out.println("dist lineObject2D : " + Math.min(d1, d2));
        return Math.min(d1, d2);
    }

    /**
     * Create a straight line parallel to this object, and going through the
     * given point.
     * 
     * @param point the point to go through
     * @return the parallel through the point
     */
    public StraightLine2D getParallel(Point2D point) {
        return new LineSegment2D(p1, p2).getParallel(point);
    }

    /**
     * Create a straight line perpendicular to this object, and going through
     * the given point.
     * 
     * @param point : the point to go through
     * @return the perpendicular through point
     */
    public StraightLine2D getPerpendicular(Point2D point) {
        return new LineSegment2D(p1, p2).getPerpendicular(point);
    }

    /**
     * Clip the line object by a box. The result is an instance of CurveSet2D<LineArc2D>,
     * which contains only instances of LineArc2D. If the line object is not
     * clipped, the result is an instance of CurveSet2D<LineArc2D> which
     * contains 0 curves.
     */
    public CurveSet2D<? extends Line2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<Curve2D> set = Curve2DUtils.clipCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<Line2D> result = new CurveSet2D<Line2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof Line2D)
                result.addCurve((Line2D) curve);
        }
        return result;
    }

    /**
     * Returns the bounding box of the Line2D.
     */
    public Box2D getBoundingBox() {
        return new Box2D(p1, p2);
    }

    // ===================================================================
    // methods inherited from SmoothCurve2D interface

    public Vector2D getTangent(double t) {
        return new Vector2D(p1, p2);
    }

    /**
     * Returns 0 as every linear shape.
     */
    public double getCurvature(double t) {
        return 0.0;
    }

    public Polyline2D getAsPolyline(int n) {
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

    public double getWindingAngle(java.awt.geom.Point2D point) {
        return new LineSegment2D(p1, p2).getWindingAngle(point);
    }

    public boolean isInside(java.awt.geom.Point2D point) {
        return new LineSegment2D(p1, p2).getSignedDistance(point)<0;
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
        double x = p1.getX()*(1-t) + p2.getX()*t;
        double y = p1.getY()*(1-t) + p2.getY()*t;
        return new Point2D(x, y);
    }

    /**
     * Get the first point of the curve.
     * 
     * @return the first point of the curve
     */
    public Point2D getFirstPoint() {
        return p1;
    }

    /**
     * Get the last point of the curve.
     * 
     * @return the last point of the curve.
     */
    public Point2D getLastPoint() {
        return p2;
    }

    public Collection<Point2D> getSingularPoints() {
        ArrayList<Point2D> list = new ArrayList<Point2D>(2);
        list.add(p1);
        list.add(p2);
        return list;
    }

    public boolean isSingular(double pos) {
        if (Double.isInfinite(pos))
            return false;
        if (Math.abs(pos)<Shape2D.ACCURACY)
            return true;
        if (Math.abs(pos-1)<Shape2D.ACCURACY)
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
    public double getPosition(java.awt.geom.Point2D point) {
        return new LineSegment2D(p1, p2).getPosition(point);
    }

    public double project(java.awt.geom.Point2D point) {
        return new LineSegment2D(p1, p2).project(point);
    }

    /**
     * Returns the line object which starts at <code>point2</code> and ends at
     * <code>point1</code>.
     */
    public Line2D getReverseCurve() {
        return new Line2D(p2, p1);
    }

    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    /**
     * Return a new Line2D, which is the portion of the line delimited by
     * parameters t0 and t1.
     */
    public Line2D getSubCurve(double t0, double t1) {
        if(t0>t1) 
            return null;
        t0 = Math.max(t0, getT0());
        t1 = Math.min(t1, getT1());
        return new Line2D(this.getPoint(t0), this.getPoint(t1));
    }

    /* (non-Javadoc)
     * @see math.geom2d.curve.Curve2D#getIntersections(math.geom2d.line.LinearShape2D)
     */
    public Collection<Point2D> getIntersections(LinearShape2D line) {
        return new LineSegment2D(p1, p2).getIntersections(line);
    }

    public void draw(Graphics2D g) {
        g.draw(this.getGeneralPath());
    }

    // ===================================================================
    // methods inherited from Shape2D interface

    public Line2D transform(AffineTransform2D trans) {
        return new Line2D(
                p1.transform(trans), 
                p2.transform(trans));
    }

    // ===================================================================
    // methods inherited from Shape interface

    /**
     * Returns true if the point (x, y) lies on the line, with precision given
     * by Shape2D.ACCURACY.
     */
    public boolean contains(double x, double y) {
        return new LineSegment2D(p1, p2).contains(x, y);
    }

    /**
     * Returns true if the point p lies on the line, with precision given by
     * Shape2D.ACCURACY.
     */
    public boolean contains(java.awt.geom.Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * Returns true
     */
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns false
     */
    public boolean isEmpty() {
        return false;
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        path.moveTo((float) p1.getX(), (float) p1.getX());
        path.lineTo((float) p2.getX(), (float) p2.getY());
        return path;
    }

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        path.lineTo((float) p1.getX(), (float) p1.getX());
        path.lineTo((float) p2.getX(), (float) p2.getY());
        return path;
    }

    // ===================================================================
    // methods inherited from Object interface

    @Override
    public String toString() {
        return "Line2D(" + p1 + ")-(" + p2 + ")";
    }

    /**
     * Two Line2D are equals if the share the two same points, 
     * in the same order.
     * 
     * @param obj the edge to compare to.
     * @return true if extremities of both edges are the same.
     */
    @Override
    public boolean equals(Object obj) {
        // check class
        if(!(obj instanceof Line2D))
            return false;
        
        // cast class, and compare members
        Line2D edge = (Line2D) obj;
        return p1.equals(edge.p1) && p2.equals(edge.p2);
    }
    
    @Override
    public Line2D clone() {
        return new Line2D(p1.clone(), p2.clone());
    }
}