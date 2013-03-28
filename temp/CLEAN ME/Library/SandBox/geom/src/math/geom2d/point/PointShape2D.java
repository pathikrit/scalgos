/**
 * File: 	PointShape2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 6 févr. 09
 */
package math.geom2d.point;

import java.util.Collection;

import math.geom2d.Point2D;
import math.geom2d.Shape2D;


/**
 * Interface for shapes composed of a finite set of points. Single points
 * should also implements this interface. Implementations of this interface
 * can contains duplicate points.
 * @author dlegland
 *
 */
public interface PointShape2D extends Shape2D, Iterable<Point2D> {

    /**
     * Returns the points in the shape as a collection.
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
