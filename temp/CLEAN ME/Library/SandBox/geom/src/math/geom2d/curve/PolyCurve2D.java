/* file : PolyCurve2D.java
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

package math.geom2d.curve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.polygon.Polyline2D;

/**
 * A PolyCurve2D is a set of piecewise smooth curve arcs, such that the end of a
 * curve is the beginning of the next curve, and such that they do not intersect
 * nor self-intersect.
 * <p>
 * 
 * @author dlegland
 */
public class PolyCurve2D<T extends ContinuousCurve2D> extends CurveSet2D<T>
        implements ContinuousCurve2D {

    /** flag for indicating if the curve is closed or not (default is open) */
    protected boolean closed = false;

    public PolyCurve2D() {
    }

    public PolyCurve2D(T[] curves) {
        super(curves);
    }

    public PolyCurve2D(T[] curves, boolean closed) {
        super(curves);
        this.closed = closed;
    }

    public PolyCurve2D(Collection<? extends T> curves) {
        super(curves);
    }

    public PolyCurve2D(Collection<? extends T> curves, boolean closed) {
        super(curves);
        this.closed = closed;
    }

    public void setClosed(boolean b) {
        closed = b;
    }

    public Polyline2D getAsPolyline(int n) {
        Point2D[] points = new Point2D[n+1];
        double t0 = this.getT0();
        double t1 = this.getT1();
        double dt = (t1-t0)/n;
        for (int i = 0; i<n; i++)
            points[i] = this.getPoint(i*dt+t0);
        return new Polyline2D(points);
    }

    public boolean isClosed() {
        return closed;
    }

    /**
     * Returns a collection containing only instances of SmoothCurve2D.
     * 
     * @return a collection of SmoothCurve2D
     */
    public Collection<? extends SmoothCurve2D> getSmoothPieces() {
        ArrayList<SmoothCurve2D> list = new ArrayList<SmoothCurve2D>();
        for (Curve2D curve : this.curves)
            list.addAll(PolyCurve2D.getSmoothCurves(curve));
        return list;
    }

    /**
     * return a collection containing only instances of SmoothCurve2D.
     * 
     * @param curve the curve to decompose
     * @return a collection of SmoothCurve2D
     */
    private final static Collection<SmoothCurve2D> getSmoothCurves(Curve2D curve) {
        ArrayList<SmoothCurve2D> array = new ArrayList<SmoothCurve2D>();

        if (curve instanceof SmoothCurve2D) {
            array.add((SmoothCurve2D) curve);
            return array;
        }

        if (curve instanceof CurveSet2D) {
            for (Curve2D curve2 : ((CurveSet2D<?>) curve).getCurves())
                array.addAll(getSmoothCurves(curve2));
            return array;
        }

        if (curve==null)
            return array;

        System.err.println("could not find smooth parts of curve with class "
                +curve.getClass().getName());
        return array;
    }

    @Override
    public PolyCurve2D<? extends ContinuousCurve2D> getReverseCurve() {
        ContinuousCurve2D[] curves2 = new ContinuousCurve2D[curves.size()];
        int n = curves.size();
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).getReverseCurve();
        return new PolyCurve2D<ContinuousCurve2D>(curves2);
    }

    /**
     * Return an instance of PolyCurve2D. If t0>t1 and curve is not closed,
     * return a PolyCurve2D without curves inside.
     */
    @Override
    public PolyCurve2D<? extends ContinuousCurve2D> getSubCurve(double t0,
            double t1) {
        CurveSet2D<?> set = super.getSubCurve(t0, t1);
        PolyCurve2D<ContinuousCurve2D> subCurve = new PolyCurve2D<ContinuousCurve2D>();

        if (t1<t0&!this.isClosed())
            return subCurve;
        subCurve.setClosed(false);

        // convert to PolySmoothCurve by adding curves.
        Iterator<?> iter = set.getCurves().iterator();
        while (iter.hasNext())
            subCurve.addCurve((ContinuousCurve2D) iter.next());

        return subCurve;
    }

    /**
     * Clip the PolyCurve2D by a box. The result is an instance of CurveSet2D<ContinuousCurve2D>,
     * which contains only instances of ContinuousCurve2D. If the PolyCurve2D is
     * not clipped, the result is an instance of CurveSet2D<ContinuousCurve2D>
     * which contains 0 curves.
     */
    @Override
    public CurveSet2D<? extends ContinuousCurve2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<Curve2D> set = Curve2DUtils.clipCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<ContinuousCurve2D> result = new CurveSet2D<ContinuousCurve2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof ContinuousCurve2D)
                result.addCurve((ContinuousCurve2D) curve);
        }
        return result;
    }

    @Override
    public PolyCurve2D<? extends ContinuousCurve2D> transform(
            AffineTransform2D trans) {
        PolyCurve2D<ContinuousCurve2D> result = new PolyCurve2D<ContinuousCurve2D>();
        for (ContinuousCurve2D curve : curves)
            result.addCurve(curve.transform(trans));
        result.setClosed(this.isClosed());
        return result;
    }

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        Point2D point;
        for (ContinuousCurve2D curve : getCurves()) {
            point = curve.getPoint(curve.getT0());
            path.lineTo((float) point.getX(), (float) point.getY());
            curve.appendPath(path);
        }

        // eventually close the curve
        if (closed) {
            point = this.getFirstPoint();
            path.lineTo((float) point.getX(), (float) point.getY());
        }

        return path;
    }

    @Override
    public java.awt.geom.GeneralPath getGeneralPath() {
        // create new path
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();

        if (curves.size()==0)
            return path;

        // extract the first curve
        Iterator<T> iter = curves.iterator();
        ContinuousCurve2D curve = iter.next();

        // move to the first point
        Point2D point;
        point = curve.getFirstPoint();
        path.moveTo((float) point.getX(), (float) point.getY());

        // add the path of the first curve
        path = curve.appendPath(path);

        // add the paths of the other curves
        while (iter.hasNext())
            path = iter.next().appendPath(path);

        // eventually closes the curve
        if (closed) {
            point = this.getFirstPoint();
            path.lineTo((float) point.getX(), (float) point.getY());
        }

        // return the final path
        return path;
    }
    
    @Override
    public boolean equals(Object obj) {
        // check class, and cast type
        if (!(obj instanceof CurveSet2D))
            return false;
        PolyCurve2D<?> curveSet = (PolyCurve2D<?>) obj;

        // check the number of curves in each set
        if (this.getCurveNumber()!=curveSet.getCurveNumber())
            return false;

        // return false if at least one couple of curves does not match
        for(int i=0; i<curves.size(); i++)
            if(!this.curves.get(i).equals(curveSet.curves.get(i)))
                return false;
        
        // otherwise return true
        return true;
    }
}
