/* File CurveSet2D.java 
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
 */

package math.geom2d.curve;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.line.StraightLine2D;

/**
 * <p>
 * A parameterized set of curves. A curve cannot be included twice in a
 * CurveSet2D.
 * </p>
 * <p>
 * The k-th curve contains points with positions between 2*k and 2*k+1. This
 * allows to differentiate extremities of contiguous curves. The points with
 * positions t between 2*k+1 and 2*k+2 belong to the curve k if t<2*k+1.5, or
 * to the curve k+1 if t>2*k+1.5
 * </p>
 * 
 * @author Legland
 */
public class CurveSet2D<T extends Curve2D> implements Curve2D, Iterable<T>,
Cloneable {

    /** The inner array of curves */
    protected ArrayList<T> curves = new ArrayList<T>();

    // ===================================================================
    // static methods

    /**
     * Mapping of the parameter t, relative to the local curve, into the
     * interval [0 1], [0 1[, ]0 1], or ]0 1[, depending on the values of t0 and
     * t1.
     * 
     * @param t a value between t0 and t1
     * @param t0 the lower bound of parameterization domain
     * @param t1 the upper bound of parameterization domain
     * @return a value between 0 and 1
     */
    protected final static double toUnitSegment(double t, double t0, double t1) {
        if (t<=t0)
            return 0;
        if (t>=t1)
            return 1;

        if (t0==Double.NEGATIVE_INFINITY&&t1==Double.POSITIVE_INFINITY)
            return Math.atan(t)/Math.PI+.5;

        if (t0==Double.NEGATIVE_INFINITY)
            return Math.atan(t-t1)*2/Math.PI+1;

        if (t1==Double.POSITIVE_INFINITY)
            return Math.atan(t-t0)*2/Math.PI;

        // t0 and t1 are both finite
        return (t-t0)/(t1-t0);
    }

    /**
     * Transforms the value t between 0 and 1 in a value comprised between t0
     * and t1.
     * 
     * @param t a value between 0 and 1
     * @param t0 the lower bound of parameterization domain
     * @param t1 the upper bound of parameterization domain
     * @return a value between t0 and t1
     */
    protected final static double fromUnitSegment(double t, double t0, double t1) {
        if (t<=0)
            return t0;
        if (t>=1)
            return t1;

        if (t0==Double.NEGATIVE_INFINITY&&t1==Double.POSITIVE_INFINITY)
            return Math.tan((t-.5)*Math.PI);

        if (t0==Double.NEGATIVE_INFINITY)
            return Math.tan((t-1)*Math.PI/2)+t1;

        if (t1==Double.POSITIVE_INFINITY)
            return Math.tan(t*Math.PI/2)+t0;

        // t0 and t1 are both finite
        return t*(t1-t0)+t0;
    }

    // ===================================================================
    // Constructors

    /**
     * Empty constructor. Initializes an empty array of curves.
     */
    public CurveSet2D() {
    }

    /**
     * Constructor from an array of curves.
     * 
     * @param curves the array of curves in the set
     */
    public CurveSet2D(T[] curves) {
        for (T element : curves)
            this.addCurve(element);
    }

    /**
     * Constructor from a collection of curves. The curves are added to the
     * inner collection of curves.
     * 
     * @param curves the collection of curves to add to the set
     */
    public CurveSet2D(Collection<? extends T> curves) {
        this.curves.addAll(curves);
    }

    // ===================================================================
    // methods specific to CurveSet2D

    /**
     * Converts the position on the curve set, which is comprised between 0 and
     * 2*Nc-1 with Nc being the number of curves, to the position on the curve
     * which contains the position. The result is comprised between the t0 and
     * the t1 of the child curve.
     * 
     * @see #getGlobalPosition(int, double)
     * @see #getCurveIndex(double)
     * @param t the position on the curve set
     * @return the position on the subcurve
     */
    public double getLocalPosition(double t) {
        int i = this.getCurveIndex(t);
        T curve = curves.get(i);
        double t0 = curve.getT0();
        double t1 = curve.getT1();
        return fromUnitSegment(t-2*i, t0, t1);
    }

    /**
     * Converts a position on a curve (between t0 and t1 of the curve) to the
     * position on the curve set (between 0 and 2*Nc-1).
     * 
     * @see #getLocalPosition(double)
     * @see #getCurveIndex(double)
     * @param i the index of the curve to consider
     * @param t the position on the curve
     * @return the position on the curve set, between 0 and 2*Nc-1
     */
    public double getGlobalPosition(int i, double t) {
        T curve = curves.get(i);
        double t0 = curve.getT0();
        double t1 = curve.getT1();
        return toUnitSegment(t, t0, t1)+i*2;
    }

    /**
     * Returns the index of the curve corresponding to a given position.
     * 
     * @param t the position on the set of curves, between 0 and twice the
     *            number of curves minus 1
     * @return the index of the curve which contains position t
     */
    public int getCurveIndex(double t) {

        // check bounds
        if (curves.size()==0)
            return 0;
        if (t>curves.size()*2-1)
            return curves.size()-1;

        // curve index
        int nc = (int) Math.floor(t);

        // check index if even-> corresponds to a curve
        int indc = (int) Math.floor(nc/2);
        if (indc*2==nc)
            return indc;
        else
            return t-nc<.5 ? indc : indc+1;
    }

    // ===================================================================
    // Management of curves

    /**
     * Adds the curve to the curve set, if it does not already belongs to the
     * set.
     * 
     * @param curve the curve to add
     */
    public void addCurve(T curve) {
        if (!curves.contains(curve))
            curves.add(curve);
    }

    /**
     * Removes the specified curve from the curve set.
     * 
     * @param curve the curve to remove
     */
    public void removeCurve(T curve) {
        curves.remove(curve);
    }

    /**
     * Clears the inner curve collection.
     */
    public void clearCurves() {
        curves.clear();
    }

    /**
     * Returns the collection of curves
     * 
     * @return the inner collection of curves
     */
    public Collection<T> getCurves() {
        return curves;
    }

    /**
     * Returns the inner curve corresponding to the given index.
     * 
     * @param index index of the curve
     * @return the i-th inner curve
     * @since 0.6.3
     */
    public T getCurve(int index) {
        return curves.get(index);
    }

    /**
     * Returns the child curve corresponding to a given position.
     * 
     * @param t the position on the set of curves, between 0 and twice the
     *            number of curves
     * @return the curve corresponding to the position.
     * @since 0.6.3
     */
    public T getChildCurve(double t) {
        if (curves.size()==0)
            return null;
        return curves.get(getCurveIndex(t));
    }

    /**
     * Returns the first curve of the collection if it exists, null otherwise.
     * 
     * @return the first curve of the collection
     */
    public T getFirstCurve() {
        if (curves.size()==0)
            return null;
        return curves.get(0);
    }

    /**
     * Returns the last curve of the collection if it exists, null otherwise.
     * 
     * @return the last curve of the collection
     */
    public T getLastCurve() {
        if (curves.size()==0)
            return null;
        return curves.get(curves.size()-1);
    }

    /**
     * Returns the number of curves in the collection
     * 
     * @return the number of curves in the collection
     */
    public int getCurveNumber() {
        return curves.size();
    }

    /**
     * Returns true if the CurveSet does not contain any curve.
     */
    public boolean isEmpty() {
        return curves.size()==0;
    }

    // ===================================================================
    // methods inherited from interface Curve2D

    public Collection<Point2D> getIntersections(LinearShape2D line) {
        ArrayList<Point2D> intersect = new ArrayList<Point2D>();

        // add intersections with each curve
        for (Curve2D curve : curves)
            intersect.addAll(curve.getIntersections(line));

        return intersect;
    }

    public double getT0() {
        return 0;
    }

    public double getT1() {
        return Math.max(curves.size()*2-1, 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve2D#getPoint(double)
     */
    public Point2D getPoint(double t) {
        if (curves.size()==0)
            return null;
        if (t<getT0())
            return this.getFirstCurve().getFirstPoint();
        if (t>getT1())
            return this.getLastCurve().getLastPoint();

        // curve index
        int nc = (int) Math.floor(t);

        // check index if even-> corresponds to a curve
        int indc = (int) Math.floor(nc/2);
        if (indc*2==nc) {
            Curve2D curve = curves.get(indc);
            double pos = fromUnitSegment(t-nc, curve.getT0(), curve.getT1());
            return curve.getPoint(pos);
        } else {
            // return either last point of preceding curve,
            // or first point of next curve
            if (t-nc<.5)
                return curves.get(indc).getLastPoint();
            else
                return curves.get(indc+1).getFirstPoint();
        }
    }

    /**
     * Get the first point of the curve.
     * 
     * @return the first point of the curve
     */
    public Point2D getFirstPoint() {
        if (curves.size()==0)
            return null;
        return getFirstCurve().getFirstPoint();
    }

    /**
     * Get the last point of the curve.
     * 
     * @return the last point of the curve.
     */
    public Point2D getLastPoint() {
        if (curves.size()==0)
            return null;
        return getLastCurve().getLastPoint();
    }

    public Collection<Point2D> getSingularPoints() {
        ArrayList<Point2D> list = new ArrayList<Point2D>();
        for (Curve2D curve : curves)
            for (Point2D point : curve.getSingularPoints())
                if (!list.contains(point))
                    list.add(point);
        return list;
    }

    public boolean isSingular(double pos) {
        if (Math.abs(pos-Math.round(pos))<Shape2D.ACCURACY)
            return true;

        int nc = this.getCurveIndex(pos);
        // int nc = (int) Math.floor(pos);
        if (nc-Math.floor(pos/2.0)>0)
            return true; // if is between 2
        // curves

        Curve2D curve = curves.get(nc);
        // double pos2 = fromUnitSegment(pos-2*nc, curve.getT0(),
        // curve.getT1());
        return curve.isSingular(this.getLocalPosition(pos));
    }

    public double getPosition(java.awt.geom.Point2D point) {
        double minDist = Double.MAX_VALUE, dist = minDist;
        double x = point.getX(), y = point.getY();
        double pos = 0, t0, t1;

        int i = 0;
        for (Curve2D curve : curves) {
            dist = curve.getDistance(x, y);
            if (dist<minDist) {
                minDist = dist;
                pos = curve.getPosition(point);
                // format position
                t0 = curve.getT0();
                t1 = curve.getT1();
                pos = toUnitSegment(pos, t0, t1)+i*2;
            }
            i++;
        }
        return pos;
    }

    public double project(java.awt.geom.Point2D point) {
        double minDist = Double.MAX_VALUE, dist = minDist;
        double x = point.getX(), y = point.getY();
        double pos = 0, t0, t1;

        int i = 0;
        for (Curve2D curve : curves) {
            dist = curve.getDistance(x, y);
            if (dist<minDist) {
                minDist = dist;
                pos = curve.project(point);
                // format position
                t0 = curve.getT0();
                t1 = curve.getT1();
                pos = toUnitSegment(pos, t0, t1)+i*2;
            }
            i++;
        }
        return pos;
    }

    public Curve2D getReverseCurve() {
        Curve2D[] curves2 = new Curve2D[curves.size()];
        int n = curves.size();
        for (int i = 0; i<n; i++)
            curves2[i] = curves.get(n-1-i).getReverseCurve();
        return new CurveSet2D<Curve2D>(curves2);
    }

    /**
     * Return an instance of CurveSet2D.
     */
    public CurveSet2D<? extends Curve2D> getSubCurve(double t0, double t1) {
        // number of curves in the set
        int nc = curves.size();

        // create a new empty curve set
        CurveSet2D<Curve2D> res = new CurveSet2D<Curve2D>();
        Curve2D curve;

        // format to ensure t is between T0 and T1
        t0 = Math.min(Math.max(t0, 0), nc*2-.6);
        t1 = Math.min(Math.max(t1, 0), nc*2-.6);

        // find curves index
        double t0f = Math.floor(t0);
        double t1f = Math.floor(t1);

        // indices of curves supporting points
        int ind0 = (int) Math.floor(t0f/2);
        int ind1 = (int) Math.floor(t1f/2);

        // case of t a little bit after a curve
        if (t0-2*ind0>1.5)
            ind0++;
        if (t1-2*ind1>1.5)
            ind1++;

        // start at the beginning of a curve
        t0f = 2*ind0;
        t1f = 2*ind1;

        double pos0, pos1;

        // need to subdivide only one curve
        if (ind0==ind1&&t0<t1) {
            curve = curves.get(ind0);
            pos0 = fromUnitSegment(t0-t0f, curve.getT0(), curve.getT1());
            pos1 = fromUnitSegment(t1-t1f, curve.getT0(), curve.getT1());
            res.addCurve(curve.getSubCurve(pos0, pos1));
            return res;
        }

        // add the end of the curve containing first cut
        curve = curves.get(ind0);
        pos0 = fromUnitSegment(t0-t0f, curve.getT0(), curve.getT1());
        res.addCurve(curve.getSubCurve(pos0, curve.getT1()));

        if (ind1>ind0) {
            // add all the whole curves between the 2 cuts
            for (int n = ind0+1; n<ind1; n++)
                res.addCurve(curves.get(n));
        } else {
            // add all curves until the end of the set
            for (int n = ind0+1; n<nc; n++)
                res.addCurve(curves.get(n));

            // add all curves from the beginning of the set
            for (int n = 0; n<ind1; n++)
                res.addCurve(curves.get(n));
        }

        // add the beginning of the last cut curve
        curve = curves.get(ind1);
        pos1 = fromUnitSegment(t1-t1f, curve.getT0(), curve.getT1());
        res.addCurve(curve.getSubCurve(curve.getT0(), pos1));

        // return the curve set
        return res;
    }

    // ===================================================================
    // methods inherited from interface Shape2D

    public double getDistance(java.awt.geom.Point2D p) {
        return getDistance(p.getX(), p.getY());
    }

    public double getDistance(double x, double y) {
        double dist = Double.POSITIVE_INFINITY;
        for (Curve2D curve : curves)
            dist = Math.min(dist, curve.getDistance(x, y));
        return dist;
    }

    /**
     * return true, if all curve pieces are bounded
     */
    public boolean isBounded() {
        for (Curve2D curve : curves)
            if (!curve.isBounded())
                return false;
        return true;
    }

    /**
     * Clip a curve, and return a CurveSet2D. If the curve is totally outside
     * the box, return a CurveSet2D with 0 curves inside. If the curve is
     * totally inside the box, return a CurveSet2D with only one curve, which is
     * the original curve.
     */
    public CurveSet2D<? extends Curve2D> clip(Box2D box) {

        // create array of points
        ArrayList<Point2D> points = new ArrayList<Point2D>();

        // extract intersections with the box boundary
        for (StraightLine2D edge : box.getClippingLines())
            points.addAll(this.getIntersections(edge));

        // convert list to point array, sorted wrt to their position on the
        // curve
        SortedSet<Double> set = new TreeSet<Double>();
        for (Point2D p : points)
            set.add(new java.lang.Double(this.getPosition(p)));

        // Create CurveSet2D for storing the result
        CurveSet2D<Curve2D> res = new CurveSet2D<Curve2D>();

        // extract first point of the curve
        Point2D point1 = this.getFirstPoint();

        // if no intersection point, the curve is totally either inside or
        // outside the box
        if (set.size()==0) {
            if (box.contains(point1))
                res.addCurve(this);
            return res;
        }

        double pos1, pos2;
        Iterator<java.lang.Double> iter = set.iterator();

        // different behavior depending if first point lies inside the box
        if (this.contains(point1)&&!box.getBoundary().contains(point1))
            res.addCurve(this.getSubCurve(this.getT0(), iter.next()));

        // add the portions of curve between couples of intersections
        while (iter.hasNext()) {
            pos1 = iter.next().doubleValue();
            if (iter.hasNext())
                pos2 = iter.next().doubleValue();
            else
                pos2 = this.getT1();
            res.addCurve(this.getSubCurve(pos1, pos2));
        }

        return res;
    }

    /**
     * Return bounding box for the CurveSet2D.
     */
    public Box2D getBoundingBox() {
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;

        Box2D box;
        for (Curve2D curve : curves) {
            box = curve.getBoundingBox();
            xmin = Math.min(xmin, box.getMinX());
            ymin = Math.min(ymin, box.getMinY());
            xmax = Math.max(xmax, box.getMaxX());
            ymax = Math.max(ymax, box.getMaxY());
        }

        return new Box2D(xmin, xmax, ymin, ymax);
    }

    /**
     * Transform each curve, and build a new CurveSet2D with the set of
     * transformed curves.
     */
    public CurveSet2D<? extends Curve2D> transform(AffineTransform2D trans) {
        CurveSet2D<Curve2D> result = new CurveSet2D<Curve2D>();
        for (Curve2D curve : curves)
            result.addCurve(curve.transform(trans));
        return result;
    }

    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> continuousCurves = new ArrayList<ContinuousCurve2D>();

        for (Curve2D curve : curves) {
            if (curve instanceof ContinuousCurve2D) {
                continuousCurves.add((ContinuousCurve2D) curve);
            } else {
                continuousCurves.addAll(curve.getContinuousCurves());
            }
        }

        return continuousCurves;
    }

    // ===================================================================
    // methods inherited from interface Shape2D

    /** Returns true if one of the curves contains the point */
    public boolean contains(java.awt.geom.Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /** Returns true if one of the curves contains the point */
    public boolean contains(double x, double y) {
        for (Curve2D curve : curves) {
            if (curve.contains(x, y))
                return true;
        }
        return false;
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        // create new path
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();

        if (curves.size()==0)
            return path;

        Point2D point;

        // move to the first point of the first curves
        for (ContinuousCurve2D curve : this.getContinuousCurves()) {
            point = curve.getFirstPoint();
            path.moveTo((float) point.getX(), (float) point.getY());
            path = curve.appendPath(path);
        }

        // return the final path
        return path;
    }

    public void draw(Graphics2D g2) {
        g2.draw(this.getGeneralPath());
    }

    // ===================================================================
    // methods inherited from interface Object

    /**
     * Returns true if obj is a CurveSet2D with the same number of curves, and
     * such that each curve belongs to both objects.
     */
    @Override
    public boolean equals(Object obj) {
        // check class, and cast type
        if (!(obj instanceof CurveSet2D))
            return false;
        CurveSet2D<?> curveSet = (CurveSet2D<?>) obj;

        // check the number of curves in each set
        if (this.getCurveNumber()!=curveSet.getCurveNumber())
            return false;

        // return false if at least one couple of curves does not match
        for(int i=0; i<curves.size(); i++)
            if(!curves.get(i).equals(curveSet.curves.get(i)))
                return false;
        
        // otherwise return true
        return true;
    }

    @Override
    public CurveSet2D<? extends Curve2D> clone() {
        ArrayList<Curve2D> array = new ArrayList<Curve2D>(curves.size());
        for(T curve : curves)
            array.add(curve);
        return new CurveSet2D<Curve2D>(array);
    }
    
    // ===================================================================
    // methods implementing the Iterable interface

   /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<T> iterator() {
        return curves.iterator();
    }
}
