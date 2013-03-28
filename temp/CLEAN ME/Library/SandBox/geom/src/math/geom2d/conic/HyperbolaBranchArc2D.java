
package math.geom2d.conic;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.UnboundedShapeException;
import math.geom2d.Vector2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.ContinuousOrientedCurve2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.polygon.Polyline2D;

/**
 * An arc of hyperbola, defined from the parent hyperbola branch, and two
 * positions on the parent curve.
 * 
 * @author dlegland
 */
public class HyperbolaBranchArc2D implements ContinuousOrientedCurve2D,
        SmoothCurve2D, Cloneable {

    /** The supporting hyperbola branch */
    HyperbolaBranch2D branch = null;

    double            t0     = 0;
    double            t1     = 1;

    public HyperbolaBranchArc2D(HyperbolaBranch2D branch, double t0, double t1) {
        this.branch = branch;
        this.t0 = t0;
        this.t1 = t1;
    }

    // ===================================================================
    // methods specific to the arc

    public HyperbolaBranch2D getHyperbolaBranch() {
        return branch;
    }

    // ===================================================================
    // methods inherited from SmoothCurve2D interface

    public double getCurvature(double t) {
        return branch.getCurvature(t);
    }

    public Vector2D getTangent(double t) {
        return branch.getTangent(t);
    }

    // ===================================================================
    // methods inherited from OrientedCurve2D interface

    public double getSignedDistance(java.awt.geom.Point2D point) {
        return this.getSignedDistance(point.getX(), point.getY());
    }

    public double getSignedDistance(double x, double y) {
        // TODO Auto-generated method stub
        return 0;
    }

    public double getWindingAngle(java.awt.geom.Point2D point) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isInside(java.awt.geom.Point2D pt) {
        // TODO Auto-generated method stub
        return false;
    }

    // ===================================================================
    // methods inherited from ContinuousCurve2D interface

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        return this.getAsPolyline(60).appendPath(path);
    }

    public Polyline2D getAsPolyline(int n) {
        if (!this.isBounded())
            throw new UnboundedShapeException();
        Point2D[] points = new Point2D[n+1];

        double dt = (t1-t0)/n;
        points[0] = this.getPoint(t0);
        for (int i = 1; i<n; i++)
            points[i] = this.getPoint((i)*dt+t0);
        points[n] = this.getPoint(t1);

        return new Polyline2D(points);
    }

    /**
     * returns an instance of ArrayList<SmoothCurve2D> containing only
     * <code>this</code>.
     */
    public Collection<? extends SmoothCurve2D> getSmoothPieces() {
        ArrayList<SmoothCurve2D> list = new ArrayList<SmoothCurve2D>();
        list.add(this);
        return list;
    }

    /** return false. */
    public boolean isClosed() {
        return false;
    }

    // ===================================================================
    // methods inherited from Curve2D interface

    /**
     * If t0 equals minus infinity, throws an UnboundedShapeException.
     */
    public Point2D getFirstPoint() {
        if (t0==Double.NEGATIVE_INFINITY)
            throw new UnboundedShapeException();
        return this.getPoint(t0);
    }

    /**
     * If t1 equals infinity, throws an UnboundedShapeException.
     */
    public Point2D getLastPoint() {
        if (t1==Double.POSITIVE_INFINITY)
            throw new UnboundedShapeException();
        return this.getPoint(t1);
    }

    public Collection<Point2D> getSingularPoints() {
        ArrayList<Point2D> list = new ArrayList<Point2D>(2);
        if (t0!=Double.NEGATIVE_INFINITY)
            list.add(this.getFirstPoint());
        if (t1!=Double.POSITIVE_INFINITY)
            list.add(this.getLastPoint());
        return list;
    }

    public boolean isSingular(double pos) {
        if (Math.abs(pos-t0)<Shape2D.ACCURACY)
            return true;
        if (Math.abs(pos-t1)<Shape2D.ACCURACY)
            return true;
        return false;
    }

    public Collection<Point2D> getIntersections(LinearShape2D line) {
        Collection<Point2D> inters0 = this.branch.getIntersections(line);
        ArrayList<Point2D> inters = new ArrayList<Point2D>();
        for (Point2D point : inters0) {
            double pos = this.branch.project(point);
            if (pos>this.t0&&pos<this.t1)
                inters.add(point);
        }

        return inters;
    }

    /**
     * If t0 equals minus infinity, throws an UnboundedShapeException.
     */
   public Point2D getPoint(double t) {
       if(Double.isInfinite(t))
           throw new UnboundedShapeException();
        t = Math.min(Math.max(t, t0), t1);
        return branch.getPoint(t);
    }

    public double getPosition(java.awt.geom.Point2D point) {
        if (!this.branch.contains(point))
            return Double.NaN;
        double t = this.branch.getPosition(point);
        if (t-t0<-ACCURACY)
            return Double.NaN;
        if (t1-t<ACCURACY)
            return Double.NaN;
        return t;
    }

    public double project(java.awt.geom.Point2D point) {
        double t = this.branch.project(point);
        return Math.min(Math.max(t, t0), t1);
    }

    public HyperbolaBranchArc2D getReverseCurve() {
        Hyperbola2D hyper = branch.hyperbola;
        Hyperbola2D hyper2 = new Hyperbola2D(hyper.xc, hyper.yc, hyper.a,
                hyper.b, hyper.theta, !hyper.direct);
        return new HyperbolaBranchArc2D(new HyperbolaBranch2D(hyper2,
                branch.positive), -t1, -t0);
    }

    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    /**
     * Returns a new HyperbolaBranchArc2D, with same parent hyperbola branch,
     * and with new parameterization bounds. The new bounds are constrained to
     * belong to the old bounds interval. If t1<t0, returns null.
     */
    public HyperbolaBranchArc2D getSubCurve(double t0, double t1) {
        if (t1<t0)
            return null;
        t0 = Math.max(this.t0, t0);
        t1 = Math.min(this.t1, t1);
        return new HyperbolaBranchArc2D(branch, t0, t1);
    }

    public double getT0() {
        return t0;
    }

    public double getT1() {
        return t1;
    }

    // ===================================================================
    // methods inherited from Shape2D interface

    public Box2D getBoundingBox() {
        if (!this.isBounded())
            throw new UnboundedShapeException();
        return this.getAsPolyline(100).getBoundingBox();
    }

    /**
     * Clip the hyperbola branch arc by a box. The result is an instance of
     * CurveSet2D<HyperbolaBranchArc2D>, which contains only instances of
     * HyperbolaBranchArc2D. If the shape is not clipped, the result is an
     * instance of CurveSet2D<HyperbolaBranchArc2D> which contains 0 curves.
     */
    public CurveSet2D<? extends HyperbolaBranchArc2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<SmoothCurve2D> set = Curve2DUtils.clipSmoothCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<HyperbolaBranchArc2D> result = new CurveSet2D<HyperbolaBranchArc2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof HyperbolaBranchArc2D)
                result.addCurve((HyperbolaBranchArc2D) curve);
        }
        return result;
    }

    public double getDistance(java.awt.geom.Point2D point) {
        Point2D p = getPoint(project(new Point2D(point)));
        return p.getDistance(point);
    }

    public double getDistance(double x, double y) {
        Point2D p = getPoint(project(new Point2D(x, y)));
        return p.getDistance(x, y);
    }

    public boolean isBounded() {
        if (t0==Double.NEGATIVE_INFINITY)
            return false;
        if (t1==Double.POSITIVE_INFINITY)
            return false;
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    public HyperbolaBranchArc2D transform(AffineTransform2D trans) {
        HyperbolaBranch2D branch2 = branch.transform(trans);

        // Compute position of end points on the transformed parabola
        double startPos = Double.isInfinite(t0) ? Double.NEGATIVE_INFINITY
                : branch2.project(this.getFirstPoint().transform(trans));
        double endPos = Double.isInfinite(t1) ? Double.POSITIVE_INFINITY
                : branch2.project(this.getLastPoint().transform(trans));

        // Compute the new arc
        return new HyperbolaBranchArc2D(branch2, startPos, endPos);
    }

    public boolean contains(java.awt.geom.Point2D p) {
        return this.contains(p.getX(), p.getY());
    }

    public boolean contains(double x, double y) {
        if (!branch.contains(x, y))
            return false;
        double t = branch.getPosition(new Point2D(x, y));
        if (t<t0)
            return false;
        if (t>t1)
            return false;
        return true;
    }

    public java.awt.geom.GeneralPath getGeneralPath() {

        if (!this.isBounded())
            throw new UnboundedShapeException();
        return this.getAsPolyline(100).getGeneralPath();
    }

    public void draw(Graphics2D g2) {
        this.getAsPolyline(100).draw(g2);
    }
    
    // ===================================================================
    // methods overriding object
  
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HyperbolaBranchArc2D))
            return false;
        HyperbolaBranchArc2D arc = (HyperbolaBranchArc2D) obj;
        
        if(!branch.equals(arc.branch)) return false;
        if(Math.abs(t0-arc.t0)>Shape2D.ACCURACY) return false;
        if(Math.abs(t1-arc.t1)>Shape2D.ACCURACY) return false;
        return true;
    }

    @Override
    public HyperbolaBranchArc2D clone() {
        return new HyperbolaBranchArc2D(branch.clone(), t0, t1);
    }
}
