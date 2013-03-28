/**
 * 
 */

package math.geom2d.polygon.convhull;

import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;

/**
 * Computes the convex hull of a set of points as a single Polygon2D.
 * 
 * @author dlegland
 */
public class JarvisMarch2D implements ConvexHull2D {

    /**
     * Creates a new Convex hull calculator.
     */
    public JarvisMarch2D(){
    }
    
    /**
     * Computes the convex hull of a set of points as a single Polygon2D.
     * Current implementation start at the point with lowest y-coord. The points
     * are considered in counter-clockwise order. Result is an instance of
     * SimplePolygon2D. Complexity is O(n*h), with n number of points, h number
     * of points of the hull. Worst case complexity is O(n^2).
     */
    public Polygon2D convexHull(Collection<? extends Point2D> points) {
        // Init iteration on points
        Point2D lowestPoint = null;
        double y;
        double ymin = Double.MAX_VALUE;

        // Iteration on the set of points to find point with lowest y-coord
        for (Point2D point : points) {
            y = point.getY();
            if (y<ymin) {
                ymin = y;
                lowestPoint = point;
            }
        }

        // initialize array of points located on convex hull
        ArrayList<Point2D> hullPoints = new ArrayList<Point2D>();

        // Init iteration on points
        Point2D currentPoint = lowestPoint;
        Point2D nextPoint = null;
        double angle = 0;

        // Iterate on point set to find point with smallest angle with respect
        // to previous line
        do {
            hullPoints.add(currentPoint);
            nextPoint = findNextPoint(currentPoint, angle, points);
            angle = Angle2D.getHorizontalAngle(currentPoint, nextPoint);
            currentPoint = nextPoint;
        } while (currentPoint!=lowestPoint);

        // Create a polygon with points located on the convex hull
        SimplePolygon2D convexHull = new SimplePolygon2D(hullPoints);
        return convexHull;
    }

    private Point2D findNextPoint(Point2D basePoint, double startAngle,
            Collection<? extends Point2D> points) {
        Point2D minPoint = null;
        double minAngle = Double.MAX_VALUE;
        double angle;

        for (Point2D point : points) {
            // Avoid to test same point
            if (basePoint.equals(point))
                continue;

            // Compute angle between current direction and next point
            angle = Angle2D.getHorizontalAngle(basePoint, point);
            angle = Angle2D.formatAngle(angle-startAngle);

            // Keep current point if angle is minimal
            if (angle<minAngle) {
                minAngle = angle;
                minPoint = point;
            }
        }

        return minPoint;
    }
}
