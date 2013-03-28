/**
 * File: 	ConvexHull2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 18 janv. 09
 */
package math.geom2d.polygon.convhull;

import java.util.Collection;

import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;


/**
 * @author dlegland
 *
 */
public interface ConvexHull2D {

    public abstract Polygon2D convexHull(Collection<? extends Point2D> points);
}
