/* file : BoundarySet2D.java
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
 * Created on 1 mai 2006
 *
 */

package math.geom2d.domain;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;

/**
 * A BoundarySet2D is a set of continuous oriented curves. Each curve of the set
 * defines its own domain.
 * <p>
 * 
 * @author dlegland
 */
public class BoundarySet2D<T extends ContinuousBoundary2D> extends
        CurveSet2D<T> implements Boundary2D {

    // ===================================================================
    // Constructors

    public BoundarySet2D() {
    }

    public BoundarySet2D(T[] curves) {
        super(curves);
    }

    public BoundarySet2D(Collection<? extends T> curves) {
        super(curves);
    }

    public BoundarySet2D(T curve) {
        super();
        this.addCurve(curve);
    }

    // ===================================================================
    // Methods implementing Boundary2D interface

    public Collection<ContinuousBoundary2D> getBoundaryCurves() {
        ArrayList<ContinuousBoundary2D> list = new ArrayList<ContinuousBoundary2D>(
                1);
        for (Curve2D curve : this.curves)
            list.add((ContinuousBoundary2D) curve);
        return list;
    }

    public Domain2D getDomain() {
        return new GenericDomain2D(this);
    }

    public void fill(Graphics2D g2) {
        g2.fill(this.getGeneralPath());
    }

    // ===================================================================
    // Methods implementing OrientedCurve2D interface

    public double getWindingAngle(java.awt.geom.Point2D point) {
        double angle = 0;
        for (OrientedCurve2D curve : this.getCurves())
            angle += curve.getWindingAngle(point);
        return angle;
    }

    public double getSignedDistance(java.awt.geom.Point2D p) {
        return getSignedDistance(p.getX(), p.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Shape2D#getSignedDistance(math.geom2d.Point2D)
     */
    public double getSignedDistance(double x, double y) {
        double minDist = Double.POSITIVE_INFINITY;
        double dist = Double.POSITIVE_INFINITY;

        for (OrientedCurve2D curve : this.getCurves()) {
            dist = Math.min(dist, curve.getSignedDistance(x, y));
            if (Math.abs(dist)<Math.abs(minDist))
                minDist = dist;
        }
        return minDist;
    }

    public boolean isInside(java.awt.geom.Point2D point) {
        return this.getSignedDistance(point.getX(), point.getY())<0;
    }

    // ===================================================================
    // Methods implementing Curve2D interface

    @Override
    public BoundarySet2D<? extends ContinuousBoundary2D> getReverseCurve() {
        ContinuousBoundary2D[] curves2 = new ContinuousBoundary2D[curves.size()];
        int n = curves.size();
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).getReverseCurve();
        return new BoundarySet2D<ContinuousBoundary2D>(curves2);
    }

    @Override
    public CurveSet2D<? extends ContinuousOrientedCurve2D> getSubCurve(
            double t0, double t1) {
        // get the subcurve
        CurveSet2D<? extends Curve2D> curveSet = super.getSubCurve(t0, t1);

        // create subcurve array
        ArrayList<ContinuousOrientedCurve2D> curves = new ArrayList<ContinuousOrientedCurve2D>();
        for (Curve2D curve : curveSet.getCurves())
            curves.add((ContinuousOrientedCurve2D) curve);

        // Create CurveSet for the result
        return new CurveSet2D<ContinuousOrientedCurve2D>(curves);
    }

    // ===================================================================
    // Methods implementing the Shape2D interface

    /**
     * Clip the curve by a box. The result is an instance of
     * ContinuousOrientedCurveSet2D<ContinuousOrientedCurve2D>, which contains
     * only instances of ContinuousOrientedCurve2D. If the curve is not clipped,
     * the result is an instance of ContinuousOrientedCurveSet2D<ContinuousOrientedCurve2D>
     * which contains 0 curves.
     */
    @Override
    public CurveSet2D<? extends ContinuousOrientedCurve2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<Curve2D> set = Curve2DUtils.clipCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<ContinuousOrientedCurve2D> result = new CurveSet2D<ContinuousOrientedCurve2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof ContinuousOrientedCurve2D)
                result.addCurve((ContinuousOrientedCurve2D) curve);
        }
        return result;
    }

    @Override
    public BoundarySet2D<? extends ContinuousBoundary2D> transform(
            AffineTransform2D trans) {
        BoundarySet2D<ContinuousBoundary2D> result = new BoundarySet2D<ContinuousBoundary2D>();
        for (Curve2D curve : curves)
            result.addCurve((ContinuousBoundary2D) curve.transform(trans));
        return result;
    }
}
