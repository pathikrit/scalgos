/* file : Polyline2D.java
 * 
 * Project : geometry
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
 * 
 * Created on 8 mai 2006
 *
 */

package math.geom2d.line;

import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.Point2D;

/**
 * A polyline is a continuous curve where each piece of the curve is a
 * LineSegment2D.
 * 
 * @deprecated use math.geom2d.polygon.Polyline2D instead (0.7.0)
 * @author dlegland
 */
@Deprecated
public class Polyline2D extends math.geom2d.polygon.Polyline2D {

    protected ArrayList<Point2D> points = new ArrayList<Point2D>();

    // ===================================================================
    // Contructors

    public Polyline2D() {
        super();
    }

    public Polyline2D(Point2D initialPoint) {
        super(initialPoint);
    }

    public Polyline2D(Point2D[] points) {
       super(points);
    }

    public Polyline2D(Collection<? extends Point2D> points) {
        super(points);
    }

    public Polyline2D(double[] xcoords, double[] ycoords) {
        super(xcoords, ycoords);
    }

    // ===================================================================
    // Methods specific to Polyline2D

    /**
     * Returns the polyline with same points considered in reverse order.
     * Reversed polyline keep same references as original polyline.
     */
    public Polyline2D getReverseCurve() {
        Point2D[] points2 = new Point2D[points.size()];
        int n = points.size();
        if (n>0) {
            for (int i = 0; i<n; i++)
                points2[i] = points.get(n-1-i);
        }
        return new Polyline2D(points2);
    }
}
