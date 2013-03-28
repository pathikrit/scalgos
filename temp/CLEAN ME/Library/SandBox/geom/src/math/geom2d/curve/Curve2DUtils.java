/**
 * 
 */

package math.geom2d.curve;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Vector2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.line.LinearShape2D;

/**
 * Collects some useful methods for clipping curves.
 * 
 * @author dlegland
 */
public abstract class Curve2DUtils {

    /**
     * Clip a curve, and return a CurveSet2D. If the curve is totally outside
     * the box, return a CurveSet2D with 0 curves inside. If the curve is
     * totally inside the box, return a CurveSet2D with only one curve, which is
     * the original curve.
     */
    public final static CurveSet2D<Curve2D> clipCurve(Curve2D curve, Box2D box) {
        // Case of continuous curve:
        // convert the result of ClipContinuousCurve to CurveSet of Curve2D
        if (curve instanceof ContinuousCurve2D)
            return new CurveSet2D<Curve2D>(Curve2DUtils.clipContinuousCurve(
                    (ContinuousCurve2D) curve, box).getCurves());

        // case of a CurveSet2D
        if (curve instanceof CurveSet2D)
            return Curve2DUtils.clipCurveSet((CurveSet2D<?>) curve, box);

        // Unknown case
        System.err.println("Unknown curve class in Box2D.clipCurve()");
        return new CurveSet2D<Curve2D>();
    }

    /**
     * clip a CurveSet2D.
     */
    public final static CurveSet2D<Curve2D> clipCurveSet(
            CurveSet2D<?> curveSet, Box2D box) {
        // Clip the current curve
        CurveSet2D<Curve2D> result = new CurveSet2D<Curve2D>();
        CurveSet2D<?> clipped;

        // a clipped parts of current curve to the result
        for (Curve2D curve : curveSet) {
            clipped = Curve2DUtils.clipCurve(curve, box);
            for (Curve2D clippedPart : clipped)
                result.addCurve(clippedPart);
        }

        // return a set of curves
        return result;
    }

    /**
     * <p>
     * Clips a continuous curve and returns a set of continuous curves.
     * </p>
     * <p>
     * Algorithm is the following one:
     * <ul>
     * <li>Compute intersections between curve and box boundary</li>
     * <li>Sort intersections according to their position on the curve</li>
     * <li>Remove intersections which do not cross (they only touch) the box
     * boundary </li>
     * <li>Add portions of curves located between two intersections and inside
     * of the box</li>
     * </ul>
     * </p>
     * <p>
     * Special processing is added when the first point of the curve lies on the
     * boundary of the box, and when the curve is closed (when the first point
     * of the curve is inside the box, the method return a portion of curve
     * between the last intersection and the first intersection).
     * </p>
     */
    public final static CurveSet2D<ContinuousCurve2D> clipContinuousCurve(
            ContinuousCurve2D curve, Box2D box) {

        // Create CurveSet2D for storing the result
        CurveSet2D<ContinuousCurve2D> res = new CurveSet2D<ContinuousCurve2D>();

        // ------ Compute ordered list of intersections

        // create array of intersection points
        ArrayList<Point2D> points = new ArrayList<Point2D>();

        // add all the intersections with edges of the box boundary
        for (LinearShape2D edge : box.getEdges())
            points.addAll(curve.getIntersections(edge));

        // convert list to point array, sorted wrt to their position on the
        // curve
        SortedSet<Double> set = new TreeSet<Double>();
        for (Point2D p : points)
            set.add(new Double(curve.getPosition(p)));

        // iterator on the intersection positions
        Iterator<Double> iter = set.iterator();

        // ----- remove intersections which do not cross the boundary

        // init arrays
        int nInter = set.size();
        double[] positions = new double[nInter+2];
        double[] between = new double[nInter+1];

        // fill up array of positions, with extreme positions of curve
        positions[0] = curve.getT0();
        for (int i = 0; i<nInter; i++)
            positions[i+1] = iter.next();
        positions[nInter+1] = curve.getT1();

        // compute positions of points between intersections
        for (int i = 0; i<nInter+1; i++)
            between[i] = choosePosition(positions[i], positions[i+1]);

        // array of positions to remove
        ArrayList<Double> toRemove = new ArrayList<Double>();

        // remove an intersection point if the curve portions before and after
        // are both either inside or outside of the box.
        for (int i = 0; i<nInter; i++) {
            Point2D p1 = curve.getPoint(between[i]);
            Point2D p2 = curve.getPoint(between[i+1]);
            boolean b1 = box.contains(p1);
            boolean b2 = box.contains(p2);
            if (b1==b2)
                toRemove.add(positions[i+1]);
        }

        // remove unnecessary intersections
        set.removeAll(toRemove);

        // iterator on the intersection positions
        iter = set.iterator();

        // ----- Check case of no intersection point

        // if no intersection point, the curve is totally either inside or
        // outside the box
        if (set.size()==0) {
            // compute position of an arbitrary point on the curve
            Point2D point;
            if (curve.isBounded()) {
                point = curve.getFirstPoint();
            } else {
                double pos = choosePosition(curve.getT0(), curve.getT1());
                point = curve.getPoint(pos);
            }

            // if the box contains a point, it contains the whole curve
            if (box.contains(point))
                res.addCurve(curve);
            return res;
        }

        // ----- Check if the curve starts inside of the box

        // the flag for a curve that starts inside the box
        boolean inside = false;
        boolean touch = false;

        // different behavior if curve is bounded or not
        double t0 = curve.getT0();
        if (Double.isInfinite(t0)) {
            // choose point between -infinite and first intersection
            double pos = choosePosition(t0, set.iterator().next());
            inside = box.contains(curve.getPoint(pos));
        } else {
            // extract first point of the curve
            Point2D point = curve.getFirstPoint();
            inside = box.contains(point);

            // if first point is on the boundary, then choose another point
            // located between first point and first intersection
            if (box.getBoundary().contains(point)) {
                touch = true;

                double pos = choosePosition(t0, iter.next());
                while (Math.abs(pos-t0)<Shape2D.ACCURACY&&iter.hasNext())
                    pos = choosePosition(t0, iter.next());
                if (Math.abs(pos-t0)<Shape2D.ACCURACY)
                    pos = choosePosition(t0, curve.getT1());
                point = curve.getPoint(pos);

                // remove the first point from the list of intersections
                set.remove(t0);

                // if inside, adds the first portion of the curve,
                // and remove next intersection
                if (box.contains(point)) {
                    pos = set.iterator().next();
                    res.addCurve(curve.getSubCurve(t0, pos));
                    set.remove(pos);
                }

                // update iterator
                iter = set.iterator();

                inside = false;
            }
        }

        // different behavior depending if first point lies inside the box
        double pos0 = Double.NaN;
        if (inside&&!touch)
            if (curve.isClosed())
                pos0 = iter.next();
            else
                res.addCurve(curve.getSubCurve(curve.getT0(), iter.next()));

        // ----- add portions of curve between each couple of intersections

        double pos1, pos2;
        while (iter.hasNext()) {
            pos1 = iter.next().doubleValue();
            if (iter.hasNext())
                pos2 = iter.next().doubleValue();
            else
                pos2 = curve.isClosed()&&!touch ? pos0 : curve.getT1();
            res.addCurve(curve.getSubCurve(pos1, pos2));
        }

        return res;
    }

    /**
     * Clip a continuous smooth curve. Currently just call the static method
     * clipContinuousCurve, and cast clipped curves.
     */
    public final static CurveSet2D<SmoothCurve2D> clipSmoothCurve(
            SmoothCurve2D curve, Box2D box) {
        CurveSet2D<SmoothCurve2D> result = new CurveSet2D<SmoothCurve2D>();
        for (ContinuousCurve2D cont : Curve2DUtils.clipContinuousCurve(curve,
                box))
            if (cont instanceof SmoothCurve2D)
                result.addCurve((SmoothCurve2D) cont);

        return result;
    }

    /**
     * Clip a continuous smooth curve by the half-plane defined by a line. This
     * method is mainly used to help debugging when implementing curves.
     */
    public final static CurveSet2D<SmoothCurve2D> clipSmoothCurve(
            SmoothCurve2D curve, StraightLine2D line) {

        // get the list of intersections with the line
        ArrayList<Point2D> list = new ArrayList<Point2D>();
        list.addAll(curve.getIntersections(line));

        // convert list to point array, sorted with respect to their position
        // on the curve, but do not add tangent points with curvature greater
        // than 0
        SortedSet<java.lang.Double> set = new TreeSet<java.lang.Double>();
        double position;
        Vector2D vector = line.getVector();
        for (Point2D point : list) {
            // get position of intersection on the curve (use project to avoid
            // round-off problems)
            position = curve.project(point);

            // Condition of colinearity with direction vector of line
            Vector2D tangent = curve.getTangent(position);
            if (Vector2D.isColinear(tangent, vector)) {
                // condition on the curvature (close to zero = cusp point)
                double curv = curve.getCurvature(position);
                if (Math.abs(curv)>Shape2D.ACCURACY)
                    continue;
            }
            set.add(new java.lang.Double(position));
        }

        // Create CurveSet2D for storing the result
        CurveSet2D<SmoothCurve2D> res = new CurveSet2D<SmoothCurve2D>();

        // extract first point of the curve, or a point arbitrarily far
        Point2D point1;
        if (Double.isInfinite(curve.getT0()))
            point1 = curve.getPoint(-1000);
        else
            point1 = curve.getFirstPoint();

        // Extract first valid intersection point, if it exists
        double pos1, pos2;
        Iterator<java.lang.Double> iter = set.iterator();

        // if no intersection point, the curve is either totally inside
        // or totally outside the box
        if (!iter.hasNext()) {
            // Find a point on the curve and not on the line
            // First tries with first point
            double t0 = curve.getT0();
            if (t0==Double.NEGATIVE_INFINITY)
                t0 = -100;
            while (line.contains(point1)) {
                double t1 = curve.getT1();
                if (t1==Double.POSITIVE_INFINITY)
                    t1 = +100;
                t0 = (t0+t1)/2;
                point1 = curve.getPoint(t0);
            }
            if (line.getSignedDistance(point1)<0)
                res.addCurve(curve);
            return res;
        }

        // different behavior depending if first point lies inside the box
        if (line.getSignedDistance(point1)<0&&!line.contains(point1)) {
            pos1 = iter.next().doubleValue();
            res.addCurve(curve.getSubCurve(curve.getT0(), pos1));
        }

        // add the portions of curve between couples of intersections
        while (iter.hasNext()) {
            pos1 = iter.next().doubleValue();
            if (iter.hasNext())
                pos2 = iter.next().doubleValue();
            else
                pos2 = curve.getT1();
            res.addCurve(curve.getSubCurve(pos1, pos2));
        }

        return res;
    }

    public final static int findNextCurveIndex(double[] positions, double pos) {
        int ind = -1;
        double posMin = java.lang.Double.MAX_VALUE;
        for (int i = 0; i<positions.length; i++) {
            // avoid NaN
            if (java.lang.Double.isNaN(positions[i]))
                continue;
            // avoid values before
            if (positions[i]-pos<Shape2D.ACCURACY)
                continue;

            // test if closer that other points
            if (positions[i]<posMin) {
                ind = i;
                posMin = positions[i];
            }
        }

        if (ind!=-1)
            return ind;

        // if not found, return index of smallest value (mean that pos is last
        // point on the boundary, so we need to start at the beginning).
        for (int i = 0; i<positions.length; i++) {
            if (java.lang.Double.isNaN(positions[i]))
                continue;
            if (positions[i]-posMin<Shape2D.ACCURACY) {
                ind = i;
                posMin = positions[i];
            }
        }
        return ind;
    }

    /**
     * Choose an arbitrary position between positions t0 and t1, which can be
     * infinite.
     * 
     * @param t0 the first bound of a curve parameterization
     * @param t1 the second bound of a curve parameterization
     * @return a position located between t0 and t1
     */
    private final static double choosePosition(double t0, double t1) {
        if (Double.isInfinite(t0)) {
            if (Double.isInfinite(t1))
                return 0;
            return t1-10;
        }

        if (Double.isInfinite(t1))
            return t0+10;

        return (t0+t1)/2;
    }
}
