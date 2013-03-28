/* File Polygon2D.java 
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

package math.geom2d.polygon;

// Imports
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.domain.BoundarySet2D;
import math.geom2d.domain.Domain2D;
import math.geom2d.line.LineSegment2D;

/**
 * Represent any class made of a finite set of simply connected edges. This
 * include simple polygons, multiple polygons, or more specialized shapes like
 * rectangles, squares...
 */
public interface Polygon2D extends Domain2D {

    /** Returns the vertices (singular points) of the polygon */
    public abstract Collection<Point2D> getVertices();

    /**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and the number of vertices
     */
    public abstract Point2D getVertex(int i);

    /**
     * Returns the number of vertices of the polygon
     * 
     * @since 0.6.3
     */
    public abstract int getVertexNumber();

    /** Return the edges as line segments of the polygon */
    public abstract Collection<LineSegment2D> getEdges();

    /** Returns the number of edges of the polygon */
    public abstract int getEdgeNumber();
    
    /**
     * Returns the set of rings comprising the boundary of this polygon.
     * @return the set of boundary rings.
     */
    public abstract Collection<Ring2D> getRings();

    
    // ===================================================================
    // general methods

    public abstract BoundarySet2D<Ring2D> getBoundary();

    /**
     * Returns the new Polygon created by an affine transform of this polygon.
     */
    public abstract Polygon2D transform(AffineTransform2D trans);

    /**
     * Returns the complementary polygon.
     * 
     * @return the polygon complementary to this
     */
    public Polygon2D complement();
}