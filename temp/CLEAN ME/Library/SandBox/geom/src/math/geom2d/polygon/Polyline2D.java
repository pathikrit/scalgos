/* file : Polyline2D.java
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
 * Created on 8 mai 2006
 *
 */

package math.geom2d.polygon;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.ContinuousOrientedCurve2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.line.StraightLine2D;

/**
 * A polyline is a continuous curve where each piece of the curve is a
 * LineSegment2D.
 * 
 * @author dlegland
 */
public class Polyline2D implements ContinuousOrientedCurve2D, Cloneable {

    protected ArrayList<Point2D> points = new ArrayList<Point2D>();

    // ===================================================================
    // Contructors

    public Polyline2D() {
    }

    public Polyline2D(Point2D initialPoint) {
        points.add(initialPoint);
    }

    public Polyline2D(Point2D[] points) {
        for (Point2D element : points)
            this.points.add(element);
    }

    public Polyline2D(Collection<? extends Point2D> points) {
        this.points.addAll(points);
    }

    public Polyline2D(double[] xcoords, double[] ycoords) {
        for (int i = 0; i<xcoords.length; i++)
            points.add(new Point2D(xcoords[i], ycoords[i]));
    }

    // ===================================================================
    // Methods specific to Polyline2D

    /**
     * Return an iterator on the collection of points.
     */
    public Iterator<Point2D> getPointsIterator() {
        return points.iterator();
    }

    /**
     * Return the collection of points as an array of Point2D.
     * 
     * @return an array of Point2D
     */
    public Point2D[] getPointArray() {
        Point2D[] tab = new Point2D[points.size()];
        for (int i = 0; i<points.size(); i++)
            tab[i] = points.get(i);
        return tab;
    }

    public void addPoint(Point2D point) {
        points.add(point);
    }

    public void removePoint(Point2D point) {
        points.remove(point);
    }

    public void clearPoints() {
        points.clear();
    }

    /**
     * Returns the vertices of the polyline.
     */
    public Collection<Point2D> getVertices() {
        return points;
    }

    /**
     * Returns the i-th vertex of the polyline.
     * 
     * @param i index of the vertex, between 0 and the number of vertices
     */
    public Point2D getVertex(int i) {
        return points.get(i);
    }

    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int getVertexNumber() {
        return points.size();
    }

    /**
     * Returns an array of LineSegment2D. The number of edges is the number of
     * vertices minus one.
     * 
     * @return the edges of the polyline
     */
    public Collection<LineSegment2D> getEdges() {
        int n = points.size();
        ArrayList<LineSegment2D> edges = new ArrayList<LineSegment2D>();

        if (n<2)
            return edges;

        for (int i = 0; i<n-1; i++)
            edges.add(new LineSegment2D(points.get(i), points.get(i+1)));

        return edges;
    }

    public LineSegment2D getFirstEdge() {
        if (points.size()<2)
            return null;
        return new LineSegment2D(points.get(0), points.get(1));
    }

    public LineSegment2D getLastEdge() {
        int n = points.size();
        if (n<2)
            return null;
        return new LineSegment2D(points.get(n-2), points.get(n-1));
    }

    // ===================================================================
    // Methods inherited from ContinuousCurve2D

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
    // Methods implementing OrientedCurve2D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.OrientedCurve2D#getSignedDistance(double, double)
     */
    public double getSignedDistance(double x, double y) {
        double dist = this.getDistance(x, y);
        if (isInside(new Point2D(x, y)))
            return -dist;
        else
            return dist;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.OrientedCurve2D#getSignedDistance(java.awt.geom.Point2D)
     */
    public double getSignedDistance(java.awt.geom.Point2D point) {
        double dist = this.getDistance(point.getX(), point.getY());
        if (isInside(point))
            return -dist;
        else
            return dist;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.OrientedCurve2D#getWindingAngle(java.awt.geom.Point2D)
     */
    public double getWindingAngle(java.awt.geom.Point2D point) {
        double angle = 0;
        int n = points.size();
        for (int i = 0; i<n-1; i++)
            angle += new LineSegment2D(points.get(i), points.get(i+1))
                    .getWindingAngle(point);

        return angle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.OrientedCurve2D#isInside(java.awt.geom.Point2D)
     */
    public boolean isInside(java.awt.geom.Point2D pt) {
        if (new Ring2D(this.getPointArray()).isInside(pt))
            return true;

        if (this.points.size()<3)
            return false;

        Point2D p0 = this.getFirstPoint();
        Point2D q0 = this.points.get(1);
        if (new StraightLine2D(q0, p0).isInside(pt))
            return false;

        Point2D p1 = this.getLastPoint();
        Point2D q1 = this.points.get(points.size()-2);
        if (new StraightLine2D(p1, q1).isInside(pt))
            return false;

        if (new StraightLine2D(p0, p1).isInside(pt))
            return true;

        return false;
    }

    // ===================================================================
    // Methods inherited from ContinuousCurve2D

    /**
     * return false, as Polyline2D is not closed by definition.
     */
    public boolean isClosed() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.ContinuousCurve2D#getSmoothPieces()
     */
    public Collection<? extends SmoothCurve2D> getSmoothPieces() {
        return getEdges();
    }

    // ===================================================================
    // Methods inherited from Curve2D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve2D#getIntersections(math.geom2d.LinearShape2D)
     */
    public Collection<Point2D> getIntersections(LinearShape2D line) {
        ArrayList<Point2D> list = new ArrayList<Point2D>();

        // extract intersections with each edge, and add to a list
        Point2D point;
        for (LineSegment2D edge : this.getEdges()) {
            point = edge.getIntersection(line);
            if (point!=null)
                if (!list.contains(point))
                    list.add(point);
        }

        // return result
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve2D#getPoint(double, math.geom2d.Point2D)
     */
    public math.geom2d.Point2D getPoint(double t) {
        // format position to stay between limits
        double t0 = this.getT0();
        double t1 = this.getT1();
        t = Math.max(Math.min(t, t1), t0);

        // index of vertex before point
        int ind0 = (int) Math.floor(t+Shape2D.ACCURACY);
        double tl = t-ind0;
        Point2D p0 = points.get(ind0);

        // check if equal to a vertex
        if (Math.abs(t-ind0)<Shape2D.ACCURACY)
            return new Point2D(p0);

        // index of vertex after point
        int ind1 = ind0+1;
        Point2D p1 = points.get(ind1);

        // position on line;

        double x0 = p0.getX();
        double y0 = p0.getY();
        double dx = p1.getX()-x0;
        double dy = p1.getY()-y0;

        return new Point2D(x0+tl*dx, y0+tl*dy);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve2D#getPosition(math.geom2d.Point2D)
     */
    public double getPosition(java.awt.geom.Point2D point) {
        int ind = 0;
        double dist, minDist = Double.POSITIVE_INFINITY;
        double x = point.getX();
        double y = point.getY();

        int i = 0;
        LineSegment2D closest = null;
        for (LineSegment2D edge : this.getEdges()) {
            dist = edge.getDistance(x, y);
            if (dist<minDist) {
                minDist = dist;
                ind = i;
                closest = edge;
            }
            i++;
        }

        return closest.getPosition(point)+ind;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve2D#getPosition(math.geom2d.Point2D)
     */
    public double project(java.awt.geom.Point2D point) {
        double dist, minDist = Double.POSITIVE_INFINITY;
        double x = point.getX();
        double y = point.getY();
        double pos = Double.NaN;

        int i = 0;
        for (LineSegment2D edge : this.getEdges()) {
            dist = edge.getDistance(x, y);
            if (dist<minDist) {
                minDist = dist;
                pos = edge.project(point)+i;
            }
            i++;
        }

        return pos;
    }

    /**
     * returns 0.
     */
    public double getT0() {
        return 0;
    }

    /**
     * return the number of points in the polyline, minus one.
     */
    public double getT1() {
        return points.size()-1;
    }

    public Point2D getFirstPoint() {
        if (points.size()==0)
            return null;
        return points.get(0);
    }

    /**
     * if polyline is closed, return the first point.
     */
    public Point2D getLastPoint() {
        if (points.size()==0)
            return null;
        return points.get(points.size()-1);
    }

    public Collection<Point2D> getSingularPoints() {
        return points;
    }

    public boolean isSingular(double pos) {
        if (Math.abs(pos-Math.round(pos))<Shape2D.ACCURACY)
            return true;
        return false;
    }

    /**
     * Returns the polyline with same points considered in reverse order.
     * Reversed polyline keep same references as original polyline.
     */
    public Polyline2D getReverseCurve() {
        Point2D[] points2 = new Point2D[points.size()];
        int n = points.size();
        if (n>0) {
            for (int i = 0; i<n; i++)
                points2[i] = points.get(n-1-i);
        }
        return new Polyline2D(points2);
    }

    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    /**
     * Return an instance of Polyline2D. If t1 is lower than t0, return an
     * instance of Polyline2D with zero points.
     */
    public Polyline2D getSubCurve(double t0, double t1) {
        // code adapted from CurveSet2D

        Polyline2D res = new Polyline2D();

        if (t1<t0)
            return res;

        // number of points in the polyline
        int indMax = (int) this.getT1();

        // format to ensure t is between T0 and T1
        t0 = Math.min(Math.max(t0, 0), indMax);
        t1 = Math.min(Math.max(t1, 0), indMax);

        // find curves index
        int ind0 = (int) Math.floor(t0);
        int ind1 = (int) Math.floor(t1);

        // need to subdivide only one line segment
        if (ind0==ind1) {
            // extract limit points
            res.addPoint(this.getPoint(t0));
            res.addPoint(this.getPoint(t1));
            // return result
            return res;
        }

        // add the point corresponding to t0
        res.addPoint(this.getPoint(t0));

        // add all the whole points between the 2 cuts
        for (int n = ind0+1; n<=ind1; n++)
            res.addPoint(points.get(n));

        // add the last point
        res.addPoint(this.getPoint(t1));

        // return the polyline
        return res;
    }

    // ===================================================================
    // Methods implementing the Shape2D interface

    /** Always returns true, because a polyline is always bounded. */
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns true if the polyline does not contain any point.
     */
    public boolean isEmpty() {
        return points.size()==0;
    }

    /**
     * Clip the polyline by a box. The result is an instance of CurveSet2D<Polyline2D>,
     * which contains only instances of Polyline2D. If the ellipse arc is not
     * clipped, the result is an instance of CurveSet2D<Polyline2D> which
     * contains 0 curves.
     */
    public CurveSet2D<? extends Polyline2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<Curve2D> set = Curve2DUtils.clipCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<Polyline2D> result = new CurveSet2D<Polyline2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof Polyline2D)
                result.addCurve((Polyline2D) curve);
        }
        return result;
    }

    public Box2D getBoundingBox() {
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;

        Iterator<Point2D> iter = points.iterator();
        Point2D point;
        double x, y;
        while (iter.hasNext()) {
            point = iter.next();
            x = point.getX();
            y = point.getY();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
        }

        return new Box2D(xmin, xmax, ymin, ymax);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Shape2D#getDistance(double, double)
     */
    public double getDistance(double x, double y) {
        double dist = Double.MAX_VALUE;
        for (LineSegment2D edge : this.getEdges())
            dist = Math.min(dist, edge.getDistance(x, y));
        return dist;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Shape2D#getDistance(java.awt.geom.Point2D)
     */
    public double getDistance(java.awt.geom.Point2D point) {
        return getDistance(point.getX(), point.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Shape2D#transform(math.geom2d.AffineTransform2D)
     */
    public Polyline2D transform(AffineTransform2D trans) {
        Point2D[] pts = new Point2D[points.size()];
        for (int i = 0; i<points.size(); i++)
            pts[i] = trans.transform(points.get(i));
        return new Polyline2D(pts);
    }

    // ===================================================================
    // Methods inherited from Shape interface

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(double, double)
     */
    public boolean contains(double x, double y) {
        for (LineSegment2D edge : this.getEdges())
            if (edge.contains(x, y))
                return true;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(java.awt.geom.Point2D)
     */
    public boolean contains(java.awt.geom.Point2D point) {
        return this.contains(point.getX(), point.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.ContinuousCurve2D#appendPath(java.awt.geom.GeneralPath)
     */
    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {

        if (points.size()<2)
            return path;

        // line to each point
        for (Point2D point : points)
            path.lineTo((float) (point.getX()), (float) (point.getY()));

        return path;
    }

    /**
     * Return a general path iterator.
     */
    public java.awt.geom.GeneralPath getGeneralPath() {
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        if (points.size()<2)
            return path;

        // get point iterator
        Iterator<Point2D> iter = points.iterator();

        // move to first point
        Point2D point = iter.next();
        path.moveTo((float) (point.getX()), (float) (point.getY()));

        // line to each other point
        while (iter.hasNext()) {
            point = iter.next();
            path.lineTo((float) (point.getX()), (float) (point.getY()));
        }

        return path;
    }

    public void draw(Graphics2D g) {
        g.draw(this.getGeneralPath());
    }

    // ===================================================================
    // Methods inherited from the Object Class

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Polyline2D))
            return false;
        Polyline2D polyline = (Polyline2D) object;

        if (points.size()!=polyline.points.size())
            return false;
        for (int i = 0; i<points.size(); i++)
            if (!(points.get(i)).equals(polyline.points.get(i)))
                return false;
        return true;
    }
    
    @Override
    public Polyline2D clone() {
        ArrayList<Point2D> array = new ArrayList<Point2D>(points.size());
        for(Point2D point : points)
            array.add(point.clone());
        return new Polyline2D(array);
    }
}
