/* file : Boundary2D.java
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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.CurveSet2D;

/**
 * A Boundary2D is the curve which defines the contour of a domain in the plane.
 * It is compound of one or several non-intersecting and oriented curves.
 * 
 * @author dlegland
 */
public interface Boundary2D extends OrientedCurve2D {

    /**
     * @deprecated  (0.7.0)
     */
    public final static Boundary2D EMPTY_BOUNDARY = new EmptyBoundary2D();

    /**
     * Returns true if the point is 'inside' the domain bounded by the curve.
     * 
     * @param pt a point in the plane
     * @return true if the point is on the left side of the curve.
     */
    public abstract boolean isInside(java.awt.geom.Point2D pt);

    /**
     * Returns the different continuous curves composing the boundary
     */
    public abstract Collection<ContinuousBoundary2D> getBoundaryCurves();

    /**
     * Returns the domain delimited by this boundary.
     * 
     * @return the domain delimited by this boundary
     */
    public abstract Domain2D getDomain();

    /**
     * Forces the subclasses to return an instance of Boundary2D.
     */
    public abstract Boundary2D getReverseCurve();

    /**
     * Forces the subclasses to return an instance of Boundary2D.
     */
    public abstract Boundary2D transform(AffineTransform2D trans);

    public abstract void fill(Graphics2D g2);

    /**
     * @deprecated empty shapes are represented by null value, reducing the
     *      total number of classes  (0.7.0)
     * @author dlegland
     */
    @Deprecated
    public static class EmptyBoundary2D extends Curve2D.EmptyCurve2D implements
            Boundary2D {

        public void fill(Graphics2D g2) {
        }

        public Collection<ContinuousBoundary2D> getBoundaryCurves() {
            return new ArrayList<ContinuousBoundary2D>();
        }

        public Domain2D getDomain() {
            return null;
        }

        /**
         * Always return false, as a point can not be contained in an empty
         * boundary.
         */
        public boolean isInside(Point2D pt) {
            return false;
        }

        public double getSignedDistance(Point2D point) {
            return Double.NaN;
        }

        public double getSignedDistance(double x, double y) {
            return Double.NaN;
        }

        public double getWindingAngle(Point2D point) {
            return Double.NaN;
        }

        @Override
        public Collection<? extends ContinuousCurve2D> getContinuousCurves() {
            return new ArrayList<ContinuousCurve2D>(0);
        }

        @Override
        public Boundary2D getReverseCurve() {
            return this;
        }

        @Override
        public Boundary2D transform(AffineTransform2D trans) {
            return this;
        }

        @Override
        public CurveSet2D<? extends OrientedCurve2D> clip(Box2D box) {
            return new CurveSet2D<OrientedCurve2D>();
        }
    }
}
