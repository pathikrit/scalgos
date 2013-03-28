/**
 * 
 */

package math.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.domain.BoundaryPolyCurve2D;
import math.geom2d.domain.PolyOrientedCurve2D;
import math.geom2d.domain.SmoothOrientedCurve2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.StraightLine2D;

/**
 * Some utility functions for manipulating Polyline2D.
 * 
 * @author dlegland
 * @since 0.6.3
 */
public abstract class Polyline2DUtils {

    /**
     * Creates a curve parallel to the given polyline, at a distance d. The
     * resulting curve is continuous, but can self-intersect. It is composed of
     * line segments, and circle arcs.
     * 
     * @param polyline the source curve
     * @param d the signed distance between the original curve and its parallel
     * @return the curve parallel to the original curve at a distance d
     */
    public final static PolyOrientedCurve2D<SmoothOrientedCurve2D> 
    createParallel(Polyline2D polyline, double d) {

        // Collection of parallel curves
        PolyOrientedCurve2D<SmoothOrientedCurve2D> result = 
            new PolyOrientedCurve2D<SmoothOrientedCurve2D>();
        result.setClosed(polyline instanceof Ring2D);

        // evacuate degenerate case.
        if (polyline.getVertices().size()<2)
            return result;

        // ----- declarations -----

        // vertices of the current edge
        Point2D v1, v2;

        // The corresponding parallel points, and the intersection point
        // for first curve
        Point2D p1, p2, p0 = null;

        // The line parallel to the previous and current line segments
        StraightLine2D line0, line;

        // Circle located at corners
        Circle2D circle;

        Iterator<Point2D> iterator;

        // ----- Initializations -----

        if (polyline instanceof Ring2D) {
            // Add eventually a circle arc, and the first line segment.

            // Extract parallel to last edge
            LineSegment2D lastEdge = polyline.getLastEdge();
            line0 = StraightLine2D.createParallel(lastEdge, d);

            v2 = lastEdge.getLastPoint();
            p0 = line0.getProjectedPoint(v2);

            // extract current vertices, and current parallel
            iterator = polyline.getVertices().iterator();
            v1 = iterator.next();
            v2 = iterator.next();
            line = new StraightLine2D(v1, v2).getParallel(d);

            // Check angle of the 2 lines
            p1 = line.getProjectedPoint(v1);
            if (Angle2D.getAngle(line0, line)>Math.PI^d<0) {
                // Line is going to the right -> next line segment will be
                // truncated
                p1 = line.getIntersection(line0);
                p0 = p1;
            } else {
                // line is going to the left -> add a circle arc
                circle = new Circle2D(v1, Math.abs(d));
                result.addCurve(new CircleArc2D(v1, Math.abs(d), circle
                        .getPosition(p0), circle.getPosition(p1), d>0));
            }

            p2 = line.getProjectedPoint(v2);
            line0 = line;
        } else {
            // extract current vertices
            iterator = polyline.getVertices().iterator();
            v1 = iterator.next();
            v2 = iterator.next();

            // current parallel
            line0 = new StraightLine2D(v1, v2).getParallel(d);
            p1 = line0.getProjectedPoint(v1);
            p2 = line0.getProjectedPoint(v2);
        }

        // ----- Main loop -----

        // Main iteration on vertices
        while (iterator.hasNext()) {
            // Compute line parallel to current line segment
            v1 = v2;
            v2 = iterator.next();
            line = new StraightLine2D(v1, v2).getParallel(d);

            // Check angle of the 2 lines
            if (Angle2D.getAngle(line0, line)>Math.PI^d<0) {
                // Line is going to the right -> add the previous line segment
                // truncated at corner
                p2 = line.getIntersection(line0);
                // TODO: need mode precise control
                result.addCurve(new LineSegment2D(p1, p2));
                p1 = p2;
            } else {
                // line is going to the left -> add the complete line segment
                // and a circle arc
                result.addCurve(new LineSegment2D(p1, p2));
                p1 = line.getProjectedPoint(v1);
                circle = new Circle2D(v1, Math.abs(d));
                result.addCurve(new CircleArc2D(v1, Math.abs(d), circle
                        .getPosition(p2), circle.getPosition(p1), d>0));
            }

            // Prepare for next iteration
            p2 = line.getProjectedPoint(v2);
            line0 = line;
        }

        // ----- Post processing -----

        if (polyline instanceof Ring2D) {
            // current line segment join the last point to the first point
            iterator = polyline.getVertices().iterator();
            v1 = v2;
            v2 = iterator.next();
            line = new StraightLine2D(v1, v2).getParallel(d);

            // Check angle of the 2 lines
            if (Angle2D.getAngle(line0, line)>Math.PI^d<0) {
                // Line is going to the right -> add the previous line segment
                // truncated at corner
                p2 = line.getIntersection(line0);
                // TODO: need mode precise control
                result.addCurve(new LineSegment2D(p1, p2));
                p1 = p2;
            } else {
                // line is going to the left -> add the complete line segment
                // and a circle arc
                result.addCurve(new LineSegment2D(p1, p2));
                p1 = line.getProjectedPoint(v1);
                circle = new Circle2D(v1, Math.abs(d));
                result.addCurve(new CircleArc2D(v1, Math.abs(d), circle
                        .getPosition(p2), circle.getPosition(p1), d>0));
            }

            // Add the last line segment
            result.addCurve(new LineSegment2D(p1, p0));
        } else {
            // Add the last line segment
            result.addCurve(new LineSegment2D(p1, p2));
        }

        // Return the resulting curve
        return result;
    }

    /**
     * Creates a curve parallel to the given polyline, at a distance d. The
     * resulting curve is continuous, but can self-intersect. It is composed of
     * line segments, and circle arcs.
     * 
     * @param polyline the source curve
     * @param d the signed distance between the original curve and its parallel
     * @return the curve parallel to the original curve at a distance d
     */
    public final static BoundaryPolyCurve2D<SmoothOrientedCurve2D>
    createClosedParallel(
            Ring2D polyline, double d) {

        // Collection of parallel curves
        BoundaryPolyCurve2D<SmoothOrientedCurve2D> result = 
            new BoundaryPolyCurve2D<SmoothOrientedCurve2D>();
        result.setClosed(true);

        // evacuate degenerate case.
        if (polyline.getVertices().size()<2)
            return result;

        // ----- declarations -----

        // vertices of the current edge
        Point2D v1, v2;

        // The corresponding parallel points, and the intersection point
        // for first curve
        Point2D p1, p2, p0 = null;

        // The line parallel to the previous and current line segments
        StraightLine2D line0, line;

        // Circle located at corners
        Circle2D circle;

        Iterator<Point2D> iterator;

        // ----- Initializations -----

        // Add eventually a circle arc, and the first line segment.

        // Extract parallel to last edge
        LineSegment2D lastEdge = polyline.getLastEdge();
        line0 = StraightLine2D.createParallel(lastEdge, d);

        v2 = lastEdge.getLastPoint();
        p0 = line0.getProjectedPoint(v2);

        // extract current vertices, and current parallel
        iterator = polyline.getVertices().iterator();
        v1 = iterator.next();
        v2 = iterator.next();
        line = new StraightLine2D(v1, v2).getParallel(d);

        // Check angle of the 2 lines
        p1 = line.getProjectedPoint(v1);
        if (Angle2D.getAngle(line0, line)>Math.PI^d<0) {
            // Line is going to the right -> next line segment will be
            // truncated
            p1 = line.getIntersection(line0);
            p0 = p1;
        } else {
            // line is going to the left -> add a circle arc
            circle = new Circle2D(v1, Math.abs(d));
            result.addCurve(new CircleArc2D(v1, Math.abs(d), circle
                    .getPosition(p0), circle.getPosition(p1), d>0));
        }

        p2 = line.getProjectedPoint(v2);
        line0 = line;

        // ----- Main loop -----

        // Main iteration on vertices
        while (iterator.hasNext()) {
            // Compute line parallel to current line segment
            v1 = v2;
            v2 = iterator.next();
            line = new StraightLine2D(v1, v2).getParallel(d);

            // Check angle of the 2 lines
            if (Angle2D.getAngle(line0, line)>Math.PI^d<0) {
                // Line is going to the right -> add the previous line segment
                // truncated at corner
                p2 = line.getIntersection(line0);
                // TODO: need mode precise control
                result.addCurve(new LineSegment2D(p1, p2));
                p1 = p2;
            } else {
                // line is going to the left -> add the complete line segment
                // and a circle arc
                result.addCurve(new LineSegment2D(p1, p2));
                p1 = line.getProjectedPoint(v1);
                circle = new Circle2D(v1, Math.abs(d));
                result.addCurve(new CircleArc2D(v1, Math.abs(d), circle
                        .getPosition(p2), circle.getPosition(p1), d>0));
            }

            // Prepare for next iteration
            p2 = line.getProjectedPoint(v2);
            line0 = line;
        }

        // ----- Post processing -----

        // current line segment join the last point to the first point
        iterator = polyline.getVertices().iterator();
        v1 = v2;
        v2 = iterator.next();
        line = new StraightLine2D(v1, v2).getParallel(d);

        // Check angle of the 2 lines
        if (Angle2D.getAngle(line0, line)>Math.PI^d<0) {
            // Line is going to the right -> add the previous line segment
            // truncated at corner
            p2 = line.getIntersection(line0);
            // TODO: need mode precise control
            result.addCurve(new LineSegment2D(p1, p2));
            p1 = p2;
        } else {
            // line is going to the left -> add the complete line segment
            // and a circle arc
            result.addCurve(new LineSegment2D(p1, p2));
            p1 = line.getProjectedPoint(v1);
            circle = new Circle2D(v1, Math.abs(d));
            result.addCurve(new CircleArc2D(v1, Math.abs(d), circle
                    .getPosition(p2), circle.getPosition(p1), d>0));
        }

        // Add the last line segment
        result.addCurve(new LineSegment2D(p1, p0));

        // Return the resulting curve
        return result;
    }
    
    /**
     * Return all intersection points between the 2 polylines.
     * @param poly1 a polyline
     * @param poly2 a polyline
     * @return the set of intersection points
     */
    public final static Collection<Point2D> intersect(
            Polyline2D poly1, Polyline2D poly2) {
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        
        Point2D point;
        for(LineSegment2D edge1 : poly1.getEdges()){
            for(LineSegment2D edge2 : poly2.getEdges()){
                point = edge1.getIntersection(edge2);
                if(point!=null)
                    points.add(point);
            }
        }

        return points;
    }
}
