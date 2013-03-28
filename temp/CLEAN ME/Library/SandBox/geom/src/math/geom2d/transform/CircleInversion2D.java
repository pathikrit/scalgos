/* File CircleInversion2D.java 
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

package math.geom2d.transform;

import java.util.*;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.curve.PolyCurve2D;
import math.geom2d.domain.BoundaryPolyCurve2D;
import math.geom2d.domain.BoundarySet2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polyline2D;
import math.geom2d.polygon.Ring2D;
import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;

// Imports

/**
 * circle inversion : performs a bijection between points outside the circle and
 * points inside the circle.
 */
public class CircleInversion2D implements Transform2D {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables
    protected Circle2D circle = new Circle2D();

    // ===================================================================
    // constructors

    /**
     * Construct a new circle inversion based on the unit circle centered on the
     * origin.
     */
    public CircleInversion2D() {
        this.circle = new Circle2D(0, 0, 1);
    }

    public CircleInversion2D(Circle2D circle) {
        this.circle = circle;
    }

    // ===================================================================
    // accessors

    public void setCircle(double xc, double yc, double r) {
        this.circle = new Circle2D(xc, yc, r);
    }

    public void setCircle(Circle2D circle) {
        this.circle = new Circle2D(circle.getCenter(), circle.getRadius());
    }

    // ===================================================================
    // modifiers

    // ===================================================================
    // general methods

    /**
     * Transforms a general shape, and return the transformed shape.
     * <p>
     * Transformed shape can be computed for different cases:
     * <ul>
     * <li>Point2D is transformed into another Point2D</li>
     * <li>LinearShape2D is transformed into a CircleArc2D or a Circle2D</li>
     * <li>Circle2D is transformed into another Circle2D</li>
     * <li>Polyline2D is transformed into a continuous set of circle arcs</li>
     * </ul>
     */
    public Shape2D transformShape(Shape2D shape) {

        if (shape instanceof Point2D) {
            double xc = circle.getCenter().getX();
            double yc = circle.getCenter().getY();
            double r = circle.getRadius();
            Point2D pt = (Point2D) shape;

            double d = r*r/pt.getDistance(xc, yc);
            double theta = Math.atan2(pt.getY()-yc, pt.getX()-xc);
            return Point2D.createPolar(circle.getCenter(), d, theta);
        } else if (shape instanceof LinearShape2D) {
            Point2D center = circle.getCenter();
            double r = circle.getRadius();
            LinearShape2D line = (LinearShape2D) shape;

            Point2D po = line.getSupportingLine().getProjectedPoint(center);
            double d = center.getDistance(po);

            // Degenerate case of a point belonging to the line :
            // the transform is the line itself.
            if (Math.abs(d)<Shape2D.ACCURACY)
                return new StraightLine2D(line);

            // angle from center to line
            double angle = Angle2D.getHorizontalAngle(center, po);

            // center of transformed circle
            double r2 = r*r/d/2;
            Point2D c2 = Point2D.createPolar(center, r2, angle);

            // case of straight line -> create a full circle
            if (line instanceof StraightLine2D)
                return new Circle2D(c2, r2);

            // case of line segment -> create a circle arc
            if (line instanceof LineSegment2D) {
                LineSegment2D segment = (LineSegment2D) line;

                // transform limits of edges, to obtain limits of arc
                Point2D p1 = segment.getFirstPoint();
                Point2D p2 = segment.getLastPoint();
                p1 = this.transformPoint(p1);
                p2 = this.transformPoint(p2);

                // compute start and end angles of arc
                double theta1 = Angle2D.getHorizontalAngle(c2, p1);
                double theta2 = Angle2D.getHorizontalAngle(c2, p2);

                boolean direct = new StraightLine2D(segment).isInside(center);

                return new CircleArc2D(c2, r2, theta1, theta2, direct);
            }
        } else if (shape instanceof Circle2D) {
            Point2D center = circle.getCenter();
            Circle2D circle = (Circle2D) shape;

            Point2D c1 = circle.getCenter();
            StraightLine2D line = new StraightLine2D(center, c1);

            // transform the two extreme points of the circle
            Collection<Point2D> points = circle.getIntersections(line);
            Iterator<Point2D> iter = points.iterator();
            Point2D p1 = this.transformPoint(iter.next());
            Point2D p2 = this.transformPoint(iter.next());

            // get center and diameter of transformed circle
            double d = p1.getDistance(p2);
            c1 = Point2D.midPoint(p1, p2);

            return new Circle2D(c1, d/2);
        } else if (shape instanceof Polyline2D) {
            // get all edges of polyline
            Collection<LineSegment2D> edges = ((Polyline2D) shape).getEdges();

            // transform each edge into a circle arc
            ArrayList<CircleArc2D> arcs = new ArrayList<CircleArc2D>();
            for (LineSegment2D edge : edges)
                arcs.add((CircleArc2D) this.transformShape(edge));

            // create new shape by putting all arcs together
            return new PolyCurve2D<CircleArc2D>(arcs);
        } else if (shape instanceof Polygon2D) {
            // get all rings of polygon
            Collection<Ring2D> rings = ((Polygon2D) shape).getRings();

            // for each ring, create a curve formed by several circle arcs
            ArrayList<BoundaryPolyCurve2D<CircleArc2D>> curves = 
                new ArrayList<BoundaryPolyCurve2D<CircleArc2D>>(rings.size());    
            for (Ring2D ring : rings)
                curves.add(this.transformRing(ring));

            // create new shape by putting all boundaries together
            return new BoundarySet2D<BoundaryPolyCurve2D<CircleArc2D>>(curves);
        }

        return null;
    }

    
     /**
      * @deprecated use transformShape() instead (0.7.0)
      */
    @Deprecated
    public Shape2D transform(Shape2D shape) {
        return this.transformShape(shape);
    }

    
    public Point2D transformPoint(java.awt.geom.Point2D pt) {
        Point2D center = circle.getCenter();
        double r = circle.getRadius();

        double d = r*r/Point2D.getDistance(pt, center);
        double theta = Angle2D.getHorizontalAngle(center, pt);
        return Point2D.createPolar(center, d, theta);
    }

    public BoundaryPolyCurve2D<CircleArc2D> transformRing(Ring2D ring) {    
        // get all edges of the ring
        Collection<LineSegment2D> edges = ring.getEdges();

        // transform each edge into a circle arc
        ArrayList<CircleArc2D> arcs = new ArrayList<CircleArc2D>();
        for (LineSegment2D edge : edges)
            arcs.add((CircleArc2D) this.transformShape(edge));

        // create new shape by putting all arcs together
        return new BoundaryPolyCurve2D<CircleArc2D>(arcs);
    }
    
    public Point2D transform(java.awt.geom.Point2D pt) {
        Point2D center = circle.getCenter();
        double r = circle.getRadius();

        double d = r*r/Point2D.getDistance(pt, center);
        double theta = Angle2D.getHorizontalAngle(center, pt);
        return Point2D.createPolar(center, d, theta);
    }

    /** Transforms an array of points, and returns the transformed points. */
    public Point2D[] transform(java.awt.geom.Point2D[] src, Point2D[] dst) {

        double d, theta;
        double xc, yc, r;

        // create the array if necessary
        if (dst==null)
            dst = new Point2D[src.length];

        // create instances of Points if necessary
        if (dst[0]==null)
            for (int i = 0; i<dst.length; i++)
                dst[i] = new Point2D();

        xc = circle.getCenter().getX();
        yc = circle.getCenter().getY();
        r = circle.getRadius();

        // transform each point
        for (int i = 0; i<src.length; i++) {
            d = java.awt.geom.Point2D.distance(src[i].getX(), src[i].getY(), xc, yc);
            d = r*r/d;
            theta = Math.atan2(src[i].getY()-yc, src[i].getX()-xc);
            dst[i].setLocation(d*Math.cos(theta), d*Math.sin(theta));
        }

        return dst;
    }

}