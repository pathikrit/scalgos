/* File ContinuousCurve2D.java 
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

package math.geom2d.curve;

// Imports
import java.util.*;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.polygon.Polyline2D;

/**
 * Interface for all curves which can be drawn with one stroke. This includes
 * closed curves (ellipses, polygon boundaries...), infinite curves (straight
 * lines, parabolas, ...), and 'finite' curves, such as polylines, conic arcs,
 * line segments, splines... Note that an hyperbola is compound of 2 continuous
 * curves.
 * <p>
 * Such curves accept parametric representation, in the form :
 * <code>p(t)={x(t),y(t)}</code>, with <code>t</code> contained in
 * appropriate domain. Bounds of domain of definition can be obtained by methods
 * <code>getT0()</code> and <code>getT1()</code>.
 * <p>
 */

public interface ContinuousCurve2D extends Curve2D {

    // ===================================================================
    // constants

    /**
     * constant for curves topologically equivalent to a closed edge. This is
     * the case for line segment, conic finite arcs, circle or ellipse arcs,
     * Bezize Curves...
     */
    public final static int CLOSED_EDGE = 1;

    /**
     * Constant for curves topologically equivalent to an open edge. For
     * example, straight lines, parabolas, or hyperbolas.
     */
    public final static int OPEN_EDGE   = 2;

    /**
     * Constant for curves topologically equivalent to a circle. This includes
     * circles, ellipses, simple polygon boundaries.
     */
    public final static int LOOP        = 3;
    public final static int CIRCLE      = 3;

    // ===================================================================
    // general methods

    /**
     * Return true if the curve makes a loop, that is come back to starting
     * point after covering the path.
     */
    public abstract boolean isClosed();

    /**
     * Returns a set of smooth curves.
     */
    public abstract Collection<? extends SmoothCurve2D> getSmoothPieces();

    /**
     * Returns an approximation of the curve as a polyline with <code>n</code>
     * line segments. If the curve is closed, the method should return an
     * instance of ClosedPolyline2D.
     * 
     * @param n the number of line segments
     * @return a polyline with <code>n</code> line segments.
     */
    public abstract Polyline2D getAsPolyline(int n);

    /**
     * Append the path of the curve to the given path.
     * 
     * @param path a path to modify
     * @return the modified path
     */
    public abstract java.awt.geom.GeneralPath appendPath(
            java.awt.geom.GeneralPath path);

    public abstract ContinuousCurve2D getReverseCurve();

    public abstract ContinuousCurve2D getSubCurve(double t0, double t1);

    public abstract CurveSet2D<? extends ContinuousCurve2D> clip(Box2D box);

    public abstract ContinuousCurve2D transform(AffineTransform2D trans);
}