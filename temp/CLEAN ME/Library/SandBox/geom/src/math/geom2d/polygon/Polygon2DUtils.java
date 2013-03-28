/**
 * 
 */

package math.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import math.geom2d.Point2D;
import math.geom2d.domain.BoundaryPolyCurve2D;
import math.geom2d.domain.BoundarySet2D;
import math.geom2d.domain.Domain2D;
import math.geom2d.domain.GenericDomain2D;
import math.geom2d.domain.SmoothOrientedCurve2D;
import math.geom2d.line.LineSegment2D;

/**
 * @author dlegland
 */
public abstract class Polygon2DUtils {

    /**
     * Computes the winding number of the polygon. Algorithm adapted from
     * http://www.geometryalgorithms.com/Archive/algorithm_0103/algorithm_0103.htm
     * 
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the number of windings of the curve around the point
     */
    public final static int windingNumber(Collection<Point2D> vertices,
            java.awt.geom.Point2D point) {
        int wn = 0; // the winding number counter

        // Extract the last point of the collection
        Point2D previous = null;
        for (Point2D vertex : vertices)
            previous = vertex;
        double x1 = previous.getX();
        double y1 = previous.getY();
        double x2, y2;

        // Iterate on couple of vertices, starting from couple (last,first)
        double y = point.getY();
        for (Point2D p : vertices) {
            // second vertex of current edge
            x2 = p.getX();
            y2 = p.getY();

            // TODO: should avoid create new objects, and use a dedicated method
            if (y1<=y) {
                if (y2>y) // an upward crossing
                    if (new LineSegment2D(x1, y1, x2, y2).isInside(point))
                        wn++;
            } else {
                if (y2<=y) // a downward crossing
                    if (!(new LineSegment2D(x1, y1, x2, y2).isInside(point)))
                        wn--;
            }

            // for next iteration
            x1 = x2;
            y1 = y2;
        }

        return wn;
    }

    public final static Domain2D createBuffer(Polygon2D polygon, double d) {
        BoundarySet2D<BoundaryPolyCurve2D<SmoothOrientedCurve2D>> result = 
            new BoundarySet2D<BoundaryPolyCurve2D<SmoothOrientedCurve2D>>();

        for (Ring2D polyline : polygon.getBoundary())
            result.addCurve(Polyline2DUtils.createClosedParallel(polyline, d));

        return new GenericDomain2D(result);
    }
    
    public final static Polygon2D union(Polygon2D polygon1, 
            Polygon2D polygon2) {
        
        // The resulting boundary
        ArrayList<Ring2D> boundary = new ArrayList<Ring2D>();
        
        // Extract polygon boundaries
        BoundarySet2D<Ring2D> boundary1 = polygon1.getBoundary();
        BoundarySet2D<Ring2D> boundary2 = polygon2.getBoundary();
        
        // compute intersections
        ArrayList<Point2D> intersections = new ArrayList<Point2D>();
        for(Ring2D ring1 : boundary1.getCurves()){
            for(Ring2D ring2 : boundary2.getCurves()){
                intersections.addAll(Polyline2DUtils.intersect(ring1, ring2));
            }
        }
        
        // Check the case of no intersection
        if(intersections.size()==0) {
            if(!polygon1.contains(boundary2.getFirstPoint())){
                boundary.addAll(boundary2.getCurves());
            }
            if(!polygon2.contains(boundary1.getFirstPoint())){
                boundary.addAll(boundary1.getCurves());
            }
            return new MultiPolygon2D(boundary);
        }
        
        // locate intersection on each boundary
        SortedSet<Double> positions1 = new TreeSet<Double>();
        SortedSet<Double> positions2 = new TreeSet<Double>();
        for (Point2D p : intersections) {
            positions1.add(new Double(boundary1.getPosition(p)));
            positions2.add(new Double(boundary2.getPosition(p)));
        }
        
        //TODO: manage several boundaries
        
        // ---
        // Manage next Ring2D
        
        // prepare the point list for the new ring
        ArrayList<Point2D> points = new ArrayList<Point2D>();

        // get an unprocessed intersection point
        Point2D refPoint = intersections.iterator().next();
        points.add(refPoint);
        
        // check if the point is going inside or outside from poly1 when
        // following boundary of poly2
        double pos = boundary1.getPosition(refPoint);
        
        // find the position of the next intersection point on boundary1
        double nextPos = findNext(positions1, pos);
        double middlePos = chooseBetween(pos, nextPos);
        Point2D middlePoint = boundary1.getPoint(middlePos);
        //TODO: not sure about non continuous curves
        
        BoundarySet2D<Ring2D> currentBoundary = 
            polygon2.contains(middlePoint) ? boundary2 : boundary1;
        boolean isOnFirstBoundary = !polygon2.contains(middlePoint);
        
        // iterate on each boundary until we come back to ref point
        Point2D point = refPoint;
        do{
            if(isOnFirstBoundary){
                nextPos = findNext(positions1, pos);
                
            }
        }while(point!=refPoint);
        
        
        return null;
    }
    
    private final static <T> T findNext(SortedSet<T> set, T element){
        SortedSet<T> tail = set.tailSet(element);
        return tail.isEmpty() ? set.first() : tail.first();
    }
    
    private final static double chooseBetween(double pos1, double pos2) {
        if(pos2>pos1) {
            return pos1 + (pos2-pos1)/2;
        } else {
            return pos2/2;
        }
    }
}
