/* file : ClosedPolyline2D.java
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
 * Created on 16 avr. 2007
 *
 */

package math.geom2d.line;

import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.polygon.Ring2D;

/**
 * Extends Polyline2D, by considering that the last point is connected to the
 * first one. A ClosedPolyline2D can be used as boundary for Polygons.
 * 
 * @deprecated use math.geom2d.polygon.Ring2D instead (0.7.0)
 * @author dlegland
 */
@Deprecated
public class ClosedPolyline2D extends Ring2D  {

    public ClosedPolyline2D() {
        super();
    }

    public ClosedPolyline2D(Point2D initialPoint) {
        super(initialPoint);
    }

    public ClosedPolyline2D(Point2D[] points) {
        super(points);
    }

    public ClosedPolyline2D(double[] xcoords, double[] ycoords) {
        super(xcoords, ycoords);
    }

    public ClosedPolyline2D(Collection<? extends Point2D> points) {
        super(points);
    }

    // ===================================================================
    // Methods specific to ClosedPolyline2D


    /**
     * Returns the closed polyline with same points taken in reverse order. The
     * first points is still the same. Points of reverse curve are the same as
     * the original curve (same pointers).
     */
    @Override
    public ClosedPolyline2D getReverseCurve() {
        Point2D[] points2 = new Point2D[points.size()];
        int n = points.size();
        if (n>0) {
            points2[0] = points.get(0);
            for (int i = 1; i<n; i++)
                points2[i] = points.get(n-i);
        }
        return new ClosedPolyline2D(points2);
    }

    /**
     * Return an instance of Polyline2D. If t1 is lower than t0, the returned
     * Polyline contains the origin of the curve.
     */
    @Override
    public Polyline2D getSubCurve(double t0, double t1) {
        // code adapted from CurveSet2D

        Polyline2D res = new Polyline2D();

        // number of points in the polyline
        int indMax = (int) this.getT1();

        // format to ensure t is between T0 and T1
        t0 = Math.min(Math.max(t0, 0), indMax);
        t1 = Math.min(Math.max(t1, 0), indMax);

        // find curves index
        int ind0 = (int) Math.floor(t0+Shape2D.ACCURACY);
        int ind1 = (int) Math.floor(t1+Shape2D.ACCURACY);

        // need to subdivide only one line segment
        if (ind0==ind1&&t0<t1) {
            // extract limit points
            res.addPoint(this.getPoint(t0));
            res.addPoint(this.getPoint(t1));
            // return result
            return res;
        }

        // add the point corresponding to t0
        res.addPoint(this.getPoint(t0));

        if (ind1>ind0) {
            // add all the whole points between the 2 cuts
            for (int n = ind0+1; n<=ind1; n++)
                res.addPoint(points.get(n));
        } else {
            // add all points until the end of the set
            for (int n = ind0+1; n<indMax; n++)
                res.addPoint(points.get(n));

            // add all points from the beginning of the set
            for (int n = 0; n<=ind1; n++)
                res.addPoint(points.get(n));
        }

        // add the last point
        res.addPoint(this.getPoint(t1));

        // return the curve set
        return res;
    }

    // ===================================================================
    // Methods inherited from interface Shape2D

    /**
     * Return the transformed shape, as a ClosePolyline2D.
     */
    @Override
    public ClosedPolyline2D transform(AffineTransform2D trans) {
        Point2D[] pts = new Point2D[points.size()];
        for (int i = 0; i<points.size(); i++)
            pts[i] = trans.transform(points.get(i));
        return new ClosedPolyline2D(pts);
    }
    
}
