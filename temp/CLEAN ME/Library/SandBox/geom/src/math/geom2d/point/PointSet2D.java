/**
 * File: 	PointSet2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 4 févr. 09
 */
package math.geom2d.point;

import java.util.Collection;

import math.geom2d.Point2D;


/**
 * @author dlegland
 *
 */
public interface PointSet2D extends PointShape2D, Iterable<Point2D> {

    /**
     * Adds a new point to the set of point. If point is not an instance of
     * Point2D, a Point2D with same location is added instead of point.
     * 
     * @param point
     */
    public void addPoint(java.awt.geom.Point2D point);

    /**
     * Add a series of points
     * 
     * @param points an array of points
     */
    public void addPoints(Collection<Point2D> points);

    /**
     * Returns an iterator on the internal point collection.
     * 
     * @return the collection of points
     */
    public Collection<Point2D> getPoints();

    /**
     * Returns the number of points in the set.
     * 
     * @return the number of points
     */
    public int getPointNumber();
}
