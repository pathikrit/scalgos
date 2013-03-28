/**
 * 
 */

package math.geom2d.grid;

import java.util.Collection;

import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.point.PointSet2D;
import math.geom2d.line.LineSegment2D;

/**
 * Defines a grid for snapping mouse pointer. The main purpose of a grid is to
 * find the closest vertex to a given point. It also provides methods for
 * accessing the collection of vertices and edges visible in a Box2D.
 * 
 * @author dlegland
 */
public interface Grid2D {

    public abstract PointSet2D getVertices(Box2D box);

    public abstract Collection<LineSegment2D> getEdges(Box2D box);

    public abstract Point2D getClosestVertex(java.awt.geom.Point2D point);
}
