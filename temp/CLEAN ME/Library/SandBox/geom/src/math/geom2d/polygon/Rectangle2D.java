/* File Rectangle2D.java 
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

package math.geom2d.polygon;

// Imports
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.domain.Boundary2DUtils;
import math.geom2d.domain.BoundarySet2D;
import math.geom2d.domain.Domain2D;
import math.geom2d.domain.GenericDomain2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.StraightLine2D;

/**
 * Rectangle2D defines a rectangle rotated around its first corner.
 */
public class Rectangle2D implements Polygon2D {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    protected double x0;
    protected double y0;
    protected double w;
    protected double h;
    protected double theta;

    // ===================================================================
    // constructors

    /** Main constructor */
    public Rectangle2D(double x0, double y0, double w, double h, double theta) {
        this.x0 = x0;
        this.y0 = y0;
        this.w = w;
        this.h = h;
        this.theta = theta;
    }

    /** Empty contructor (size and position zero) */
    public Rectangle2D() {
        this(0, 0, 0, 0, 0);
    }

    /** Constructor from awt, to allow easy construction from existing apps. */
    public Rectangle2D(java.awt.geom.Rectangle2D rect) {
        this.x0 = rect.getX();
        this.y0 = rect.getY();
        this.w = rect.getWidth();
        this.h = rect.getHeight();
        this.theta = 0;
    }

    /** Main constructor */
    public Rectangle2D(double x0, double y0, double w, double h) {
        this.x0 = x0;
        this.y0 = y0;
        this.w = w;
        this.h = h;
        this.theta = 0;
    }

    /** Main constructor */
    public Rectangle2D(Point2D point, double w, double h, double theta) {
        this.x0 = point.getX();
        this.y0 = point.getY();
        this.w = w;
        this.h = h;
        this.theta = theta;
    }

    /** Main constructor */
    public Rectangle2D(Point2D point, double w, double h) {
        this.x0 = point.getX();
        this.y0 = point.getY();
        this.w = w;
        this.h = h;
        this.theta = 0;
    }

    // ===================================================================
    // accessors

    public double getX() {
        return x0;
    }

    public double getY() {
        return y0;
    }

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }

    public double getTheta() {
        return theta;
    }

    // ===================================================================
    // mutators

    /**
     * Apply the characteristics of the given Rectangle to this object.
     */
    public void setRectangle(Rectangle2D rect) {
        this.x0 = rect.x0;
        this.y0 = rect.y0;
        this.w = rect.w;
        this.h = rect.h;
        this.theta = rect.theta;
    }

    /**
     * Apply the characteristics of the given Rectangle to this object.
     */
    public void setRectangle(java.awt.geom.Rectangle2D rect) {
        this.x0 = rect.getX();
        this.y0 = rect.getY();
        this.w = rect.getWidth();
        this.h = rect.getHeight();
        this.theta = 0;
    }

    /**
     * Apply the characteristics of the given Rectangle to this object.
     */
    public void setRectangle(double x, double y, double width, double height,
            double theta) {
        this.x0 = x;
        this.y0 = y;
        this.w = width;
        this.h = height;
        this.theta = theta;
    }

    // ===================================================================
    // methods inherited from interface AbstractPolygon2D

    /**
     * Returns the vertices of the rectangle as a collection of points.
     * 
     * @return the vertices of the rectangle.
     */
    public Collection<Point2D> getVertices() {
        AffineTransform2D rot = AffineTransform2D.createRotation(x0, y0, theta);
        ArrayList<Point2D> array = new ArrayList<Point2D>(4);

        array.add(new Point2D(x0, y0).transform(rot));
        array.add(new Point2D(x0+w, y0).transform(rot));
        array.add(new Point2D(x0+w, y0+h).transform(rot));
        array.add(new Point2D(x0, y0+h).transform(rot));

        return array;
    }

    /**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and 3
     */
    public Point2D getVertex(int i) {
        AffineTransform2D rot = AffineTransform2D.createRotation(x0, y0, theta);
        switch (i) {
        case 0:
            return new Point2D(x0, y0).transform(rot);
        case 1:
            return new Point2D(x0+w, y0).transform(rot);
        case 2:
            return new Point2D(x0+w, y0+h).transform(rot);
        case 3:
            return new Point2D(x0, y0+h).transform(rot);
        default:
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Return the number of vertices of the rectangle, which is 4.
     * 
     * @since 0.6.3
     */
    public int getVertexNumber() {
        return 4;
    }

    public Collection<LineSegment2D> getEdges() {
        ArrayList<LineSegment2D> edges = new ArrayList<LineSegment2D>(4);
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);

        double x1 = w*cot+x0;
        double y1 = w*sit+y0;
        double x2 = w*cot-h*sit+x0;
        double y2 = w*sit+h*cot+y0;
        double x3 = -h*sit+x0;
        double y3 = h*cot+y0;

        edges.add(new LineSegment2D(x0, y0, x1, y1));
        edges.add(new LineSegment2D(x1, y1, x2, y2));
        edges.add(new LineSegment2D(x2, y2, x3, y3));
        edges.add(new LineSegment2D(x3, y3, x0, y0));
        return edges;
    }

    public int getEdgeNumber() {
        return 4;
    }

    /* (non-Javadoc)
     * @see math.geom2d.polygon.Polygon2D#getRings()
     */
    public Collection<Ring2D> getRings() {
        ArrayList<Ring2D> rings = new ArrayList<Ring2D>(1);
        rings.add(new Ring2D(this.getVertices()));
        return rings;
    }

    // ===================================================================
    // methods implementing the Domain2D interface

    public BoundarySet2D<Ring2D> getBoundary() {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        Point2D pts[] = new Point2D[4];
        pts[0] = new Point2D(x0, y0);
        pts[1] = new Point2D(w*cot+x0, w*sit+y0);
        pts[2] = new Point2D(w*cot-h*sit+x0, w*sit+h*cot+y0);
        pts[3] = new Point2D(-h*sit+x0, h*cot+y0);

        return new BoundarySet2D<Ring2D>(new Ring2D(pts));
    }

    public Polygon2D complement() {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        Point2D pts[] = new Point2D[4];
        pts[0] = new Point2D(x0, y0);
        pts[1] = new Point2D(-h*sit+x0, h*cot+y0);
        pts[2] = new Point2D(w*cot-h*sit+x0, w*sit+h*cot+y0);
        pts[3] = new Point2D(w*cot+x0, w*sit+y0);

        return new SimplePolygon2D(pts);
    }

    // ===================================================================
    // methods inherited from Shape2D interface

    /** Always returns true, because a rectangle is always bounded. */
    public boolean isBounded() {
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    public double getDistance(java.awt.geom.Point2D p) {
        return Math.max(getSignedDistance(p.getX(), p.getY()), 0);
    }

    public double getDistance(double x, double y) {
        return Math.max(getSignedDistance(x, y), 0);
    }

    /**
     * Get the signed distance of the shape to the given point : this distance
     * is positive if the point lies outside the shape, and is negative if the
     * point lies inside the shape. In this case, absolute value of distance is
     * equals to the distance to the border of the shape.
     */
    public double getSignedDistance(java.awt.geom.Point2D p) {
        return getSignedDistance(p.getX(), p.getY());
    }

    /**
     * Get the signed distance of the shape to the given point : this distance
     * is positive if the point lies outside the shape, and is negative if the
     * point lies inside the shape. In this case, absolute value of distance is
     * equals to the distance to the border of the shape.
     */
    public double getSignedDistance(double x, double y) {
        double dist = getBoundary().getDistance(x, y);
        if (contains(x, y))
            return -dist;
        else
            return dist;
    }

    /**
     * Return the clipped polygon.
     */
    public Domain2D clip(Box2D box) {
        return new GenericDomain2D(Boundary2DUtils.clipBoundary(this
                .getBoundary(), box));
    }

    /**
     * Return bounding box of the rectangle.
     */
    public Box2D getBoundingBox() {
        double xmin = x0;
        double xmax = x0;
        double ymin = y0;
        double ymax = y0;
        double x, y;
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);

        x = w*cot+x0;
        y = w*sit+y0;
        if (xmin>x)
            xmin = x;
        if (ymin>y)
            ymin = y;
        if (xmax<x)
            xmax = x;
        if (ymax<y)
            ymax = y;

        x = w*cot-h*sit+x0;
        y = w*sit+h*cot+y0;
        if (xmin>x)
            xmin = x;
        if (ymin>y)
            ymin = y;
        if (xmax<x)
            xmax = x;
        if (ymax<y)
            ymax = y;

        x = h*sit+x0;
        y = h*cot+y0;
        if (xmin>x)
            xmin = x;
        if (ymin>y)
            ymin = y;
        if (xmax<x)
            xmax = x;
        if (ymax<y)
            ymax = y;

        return new Box2D(xmin, xmax, ymin, ymax);
    }

    /**
     * Return the new Polygon created by an affine transform of this polygon.
     */
    public SimplePolygon2D transform(AffineTransform2D trans) {
        int nPoints = 4;
        Point2D[] array = new Point2D[nPoints];
        Point2D[] res = new Point2D[nPoints];
        Iterator<Point2D> iter = this.getVertices().iterator();
        for (int i = 0; i<nPoints; i++) {
            array[i] = iter.next();
            res[i] = new Point2D();
        }

        trans.transform(array, res);
        return new SimplePolygon2D(res);
    }

    // ===================================================================
    // methods inherited from Shape interface

    /**
     * This method simply invoke ancestor method. It is redefined to avoid
     * ambiguity with contains(Shape2D).
     */
    public boolean contains(java.awt.geom.Point2D point) {
        return contains(point.getX(), point.getY());
    }

    public boolean contains(double x, double y) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);

        double x1 = w*cot+x0;
        double y1 = w*sit+y0;
        double x2 = w*cot-h*sit+x0;
        double y2 = w*sit+h*cot+y0;
        double x3 = -h*sit+x0;
        double y3 = h*cot+y0;

        StraightLine2D line = new StraightLine2D(x0, y0, x1-x0, y1-y0);
        if (line.getSignedDistance(x, y)>0)
            return false;
        line = new StraightLine2D(x1, y1, x2-x1, y2-y1);
        if (line.getSignedDistance(x, y)>0)
            return false;
        line = new StraightLine2D(x2, y2, x3-x2, y3-y2);
        // line.setPoints(x2, y2, x3, y3);
        if (line.getSignedDistance(x, y)>0)
            return false;
        line = new StraightLine2D(x3, y3, x0-x3, y0-y3);
        // line.setPoints(x3, y3, x0, y0);
        if (line.getSignedDistance(x, y)>0)
            return false;
        return true;
    }

    public void draw(Graphics2D g2) {
        g2.draw(this.getBoundary().getGeneralPath());
    }

    public void fill(Graphics2D g) {
        g.fill(this.getBoundary().getGeneralPath());
    }

    // ===================================================================
    // methods inherited from Object interface

    /**
     * Test if retangles are the same. We consider two rectangles are equals if
     * their corners are the same. Then, we can have different origin and
     * different angles, but equal rectangles.
     */
    @Override
    public boolean equals(Object obj) {
        // check class, and cast type
        if (!(obj instanceof Rectangle2D))
            return false;
        Rectangle2D rect = (Rectangle2D) obj;

        // first get list of corners of the 2 rectangles.
        // Iterator<Point2D> iter1 = this.getPoints();
        // Point2D point;

        // check all 4 corners of the first rectangle
        // while(iter1.hasNext()){
        // point = (Point2D) iter1.next();
        boolean ok;
        for (Point2D point : this.getVertices()) {
            ok = false;

            // compare with all 4 corners of second rectangle
            // Iterator<Point2D> iter2 = rect.getPoints();
            // while(iter2.hasNext())
            // if(point.equals(iter2.next()))
            // ok = true;
            for (Point2D point2 : rect.getVertices())
                if (point.equals(point2)) {
                    ok = true;
                    break;
                }

            // if the point does not belong to the corners of the other
            // rectangle,
            // then the two rect are different
            if (!ok)
                return false;
        }

        // test ok for 4 corners, then the two rectangles are the same.
        return true;
    }

}