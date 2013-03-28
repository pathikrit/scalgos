/* file : OrientedCurve2D.java
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
 * Created on 25 déc. 2006
 *
 */

package math.geom2d.domain;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.CurveSet2D;

/**
 * An OrientedCurve2D defines an 'inside' and an 'outside'. It is typically a
 * part of the boundary of a domain. Several OrientedCurve2D form a
 * ContinuousBoundary2D, and one or several ContinousBoundary2D form a
 * Boundary2D.
 * 
 * @author dlegland
 */
public interface OrientedCurve2D extends Curve2D {

    /**
     * Return the angle portion that the curve turn around the given point.
     * Result is a signed angle.
     * 
     * @param point a point of the plane
     * @return a signed angle
     */
    public abstract double getWindingAngle(java.awt.geom.Point2D point);

    /**
     * Get the signed distance of the curve to the given point: this distance is
     * positive if the point lies outside the shape, and is negative if the
     * point lies inside the shape. In this case, absolute value of distance is
     * equals to the distance to the border of the shape.
     * 
     * @param point a point of the plane
     * @return the signed distance to the curve
     */
    public abstract double getSignedDistance(java.awt.geom.Point2D point);

    /**
     * The same as getSignedDistance(Point2D), but by passing 2 double as
     * arguments.
     * 
     * @param x x-coord of a point
     * @param y y-coord of a point
     * @return the signed distance of the point (x,y) to the curve
     */
    public abstract double getSignedDistance(double x, double y);

    /**
     * Returns true if the point is 'inside' the domain bounded by the curve.
     * 
     * @param pt a point in the plane
     * @return true if the point is on the left side of the curve.
     */
    // TODO: think about either deprecate or better define
    public abstract boolean isInside(java.awt.geom.Point2D pt);

    public abstract OrientedCurve2D getReverseCurve();

    // TODO: what to do with non-continuous oriented curves ?
    // public abstract OrientedCurve2D getSubCurve(double t0, double t1);

    public abstract CurveSet2D<? extends OrientedCurve2D> clip(Box2D box);

    public abstract OrientedCurve2D transform(AffineTransform2D trans);
}
