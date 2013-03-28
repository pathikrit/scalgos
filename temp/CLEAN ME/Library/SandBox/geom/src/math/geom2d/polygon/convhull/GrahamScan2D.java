/**
 * File: 	GrahamScan2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 18 janv. 09
 */
package math.geom2d.polygon.convhull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;


/**
 * @author dlegland
 *
 */
public class GrahamScan2D implements ConvexHull2D {

    /**
     * Creates a new Convex hull calculator.
     */
    public GrahamScan2D() {
    }

    /* (non-Javadoc)
     * @see math.geom2d.polygon.convhull.ConvexHull2D#convexHull(java.util.Collection)
     */
    public Polygon2D convexHull(Collection<? extends Point2D> points) {
        int nbPoints = points.size();
        //TODO: manage small values of n
        
        // Find point with lowest y-coord
        Point2D lowestPoint = null;
        double lowestY = Double.MAX_VALUE;
        for(Point2D point : points){
            double y = point.getY();
            if(y<lowestY){
                lowestPoint = point;
                lowestY = y;
            }
        }
        
        // build the comparator, using the lowest point
        Comparator<Point2D> comparator = 
            new CompareByPseudoAngle(lowestPoint);
        
        // create a sorted set
        ArrayList<Point2D> sorted = new ArrayList<Point2D>(nbPoints);
        sorted.addAll(points);
        Collections.sort(sorted, comparator);
        
        // main loop
        // i-> current vertex of point cloud
        // m-> current hull vertex
        int m = 2;
        for(int i=3; i<nbPoints; i++){
            while(Point2D.ccw(sorted.get(m), sorted.get(m-1), 
                    sorted.get(i))>=0)
                m--;
            m++;
            Collections.swap(sorted, m, i);
        }

        // Format result to return a polygon
        List<Point2D> hull = sorted.subList(0, Math.min(m+1, nbPoints));
        return new SimplePolygon2D(hull);
    }

    private class CompareByPseudoAngle implements Comparator<Point2D>{
        Point2D basePoint;
        public CompareByPseudoAngle(Point2D base) {
            this.basePoint = base;
        }
        
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Point2D point1, Point2D point2) {
            double angle1 = Angle2D.getPseudoAngle(basePoint, point1);
            double angle2 = Angle2D.getPseudoAngle(basePoint, point2);
            
            if(angle1<angle2) return -1;
            if(angle1>angle2) return +1;
            //TODO: and what about colinear points ?
            return 0;
        }
    }
}
