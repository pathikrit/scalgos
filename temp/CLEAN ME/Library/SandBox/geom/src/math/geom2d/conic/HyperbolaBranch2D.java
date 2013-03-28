
package math.geom2d.conic;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.UnboundedShapeException;
import math.geom2d.Vector2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.ContinuousBoundary2D;
import math.geom2d.domain.Domain2D;
import math.geom2d.domain.GenericDomain2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.polygon.Polyline2D;

/**
 * Branch of an Hyperbola2D.
 */
public class HyperbolaBranch2D implements ContinuousBoundary2D, SmoothCurve2D,
Cloneable {

    Hyperbola2D hyperbola = null;
    boolean     positive  = true;

    public HyperbolaBranch2D(Hyperbola2D hyperbola, boolean b) {
        this.hyperbola = hyperbola;
        this.positive = b;
    }

    // ===================================================================
    // methods specific to HyperbolaBranch2D

    /**
     * Returns the supporting hyperbola of this branch.
     */
    public Hyperbola2D getHyperbola() {
        return hyperbola;
    }

    /**
     * Returns true if this branch is the positive one, i.e. it contains the
     * positive axis in the basis of the supporting hyperbola.
     * 
     * @return true if this branch contains the positive axis.
     */
    public boolean isPositiveBranch() {
        return positive;
    }

    // ===================================================================
    // methods inherited from SmoothCurve2D interface

    /**
     * Use formula given in <a
     * href="http://mathworld.wolfram.com/Hyperbola.html">http://mathworld.wolfram.com/Hyperbola.html</a>
     */
    public double getCurvature(double t) {
        double a = hyperbola.a;
        double b = hyperbola.b;
        double asih = a*Math.sinh(t);
        double bcoh = b*Math.cosh(t);
        return a*b/Math.pow(Math.hypot(bcoh, asih), 3);
    }

    public Vector2D getTangent(double t) {
        double a = hyperbola.a;
        double b = hyperbola.b;
        double theta = hyperbola.theta;
        double dx, dy;
        if (positive) {
            dx = a*Math.sinh(t);
            dy = b*Math.cosh(t);
        } else {
            dx = -a*Math.sinh(t);
            dy = -b*Math.cosh(t);
        }
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new Vector2D(dx*cot-dy*sit, dx*sit+dy*cot);
    }

    // ===================================================================
    // methods inherited from Boundary2D interface

    /**
     * Returns an instance of ArrayList<ContinuousBoundary2D> containing only
     * <code>this</code>.
     */
    public Collection<ContinuousBoundary2D> getBoundaryCurves() {
        ArrayList<ContinuousBoundary2D> list = new ArrayList<ContinuousBoundary2D>();
        list.add(this);
        return list;
    }

    public Domain2D getDomain() {
        return new GenericDomain2D(this);
    }

    /** Throws an UnboundedShapeException */
    public void fill(Graphics2D g2) {
        throw new UnboundedShapeException();
    }

    // ===================================================================
    // methods inherited from OrientedCurve2D interface

    public double getSignedDistance(java.awt.geom.Point2D point) {
        double dist = this.getDistance(point);
        return this.isInside(point) ? -dist : dist;
    }

    public double getSignedDistance(double x, double y) {
        return this.getSignedDistance(new Point2D(x, y));
    }

    public double getWindingAngle(java.awt.geom.Point2D point) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isInside(java.awt.geom.Point2D point) {
        if (hyperbola.isDirect()) {
            if (hyperbola.isInside(point))
                return true;
            double x = hyperbola.toLocal(new Point2D(point)).getX();
            return positive ? x<0 : x>0;
        } else {
            if (!hyperbola.isInside(point))
                return false;
            double x = hyperbola.toLocal(new Point2D(point)).getX();
            return positive ? x>0 : x<0;
        }
    }

    // ===================================================================
    // methods inherited from ContinuousCurve2D interface

    /**
     * returns an instance of ArrayList<SmoothCurve2D> containing only
     * <code>this</code>.
     */
    public Collection<? extends SmoothCurve2D> getSmoothPieces() {
        ArrayList<SmoothCurve2D> list = new ArrayList<SmoothCurve2D>();
        list.add(this);
        return list;
    }

    /** Return false, by definition of Hyperbola branch */
    public boolean isClosed() {
        return false;
    }

    /**
     * Returns a hyperbola arc with -100<t<100 transformed into polyline.
     */
    public Polyline2D getAsPolyline(int n) {
        return new HyperbolaBranchArc2D(this, -100, 100).getAsPolyline(n);
    }

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        return new HyperbolaBranchArc2D(this, -100, 100).appendPath(path);
    }

    
    // ===================================================================
    // methods inherited from Curve2D interface

    /** Throws an UnboundedShapeException */
    public Point2D getFirstPoint() {
        throw new UnboundedShapeException();
    }

    /** Throws an UnboundedShapeException */
    public Point2D getLastPoint() {
        throw new UnboundedShapeException();
    }

    public Collection<Point2D> getSingularPoints() {
        return new ArrayList<Point2D>(0);
    }

    /**
     * Always returns false, as an hyperbola branch does not have any singular
     * point.
     */
    public boolean isSingular(double pos) {
        return false;
    }

    public Point2D getPoint(double t) {
        if (Double.isInfinite(t))
            throw new UnboundedShapeException();

        double x, y;
        if (positive) {
            x = Math.cosh(t);
            if (Double.isInfinite(x))
                x = Math.abs(t);
            y = Math.sinh(t);
            if (Double.isInfinite(y))
                y = t;
        } else {
            x = -Math.cosh(t);
            if (Double.isInfinite(x))
                x = -Math.abs(t);
            y = -Math.sinh(t);
            if (Double.isInfinite(y))
                y = -t;
        }
        return hyperbola.toGlobal(new Point2D(x, y));
    }

    public double getPosition(java.awt.geom.Point2D point) {
        Point2D pt = hyperbola.toLocal(new Point2D(point));
        double y = this.positive ? pt.getY() : -pt.getY();
        return Math.log(y+Math.hypot(y, 1));
    }

    public double project(java.awt.geom.Point2D point) {
        Point2D pt = hyperbola.toLocal(new Point2D(point));
        double y = this.positive ? pt.getY() : -pt.getY();
        return Math.log(y+Math.hypot(y, 1));
    }

    public HyperbolaBranch2D getReverseCurve() {
        Hyperbola2D hyper2 = new Hyperbola2D(hyperbola.xc, hyperbola.yc,
                hyperbola.a, hyperbola.b, hyperbola.theta, !hyperbola.direct);
        return new HyperbolaBranch2D(hyper2, positive);
    }

    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    /**
     * returns an instance of HyprbolaBranchArc2D initialized with
     * <code>this</code>.
     */
    public HyperbolaBranchArc2D getSubCurve(double t0, double t1) {
        return new HyperbolaBranchArc2D(this, t0, t1);
    }

    /** returns Double.NEGATIVE_INFINITY. */
    public double getT0() {
        return Double.NEGATIVE_INFINITY;
    }

    /** returns Double.POSITIVE_INFINITY. */
    public double getT1() {
        return Double.POSITIVE_INFINITY;
    }

    public Collection<Point2D> getIntersections(LinearShape2D line) {
        // compute intersections with support hyperbola
        Collection<Point2D> inters = hyperbola.getIntersections(line);

        // check which points belong to this branch
        Collection<Point2D> result = new ArrayList<Point2D>();
        for (Point2D point : inters) {
            if (!(hyperbola.toLocal(point).getX()>0^positive))
                result.add(point);
        }

        // return result
        return result;
    }

    
    // ===================================================================
    // methods inherited from Shape2D interface

    /** Returns a bounding box with infinite bounds in every direction */
    public Box2D getBoundingBox() {
        return new Box2D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Clip the circle arc by a box. The result is an instance of CurveSet2D<HyperbolaBranchArc2D>,
     * which contains only instances of HyperbolaBranchArc2D. If the conic arc
     * is not clipped, the result is an instance of CurveSet2D<HyperbolaBranchArc2D>
     * which contains 0 curves.
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
        Point2D projected = this.getPoint(this.project(new Point2D(point)));
        return projected.getDistance(point);
    }

    public double getDistance(double x, double y) {
        Point2D projected = this.getPoint(this.project(new Point2D(x, y)));
        return projected.getDistance(x, y);
    }

    /** return false, as an hyperbola branch is never bounded. */
    public boolean isBounded() {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public HyperbolaBranch2D transform(AffineTransform2D trans) {
        return new HyperbolaBranch2D(this.hyperbola.transform(trans),
                this.positive);
    }

    public boolean contains(java.awt.geom.Point2D point) {
        return this.contains(point.getX(), point.getY());
    }

    public boolean contains(double x, double y) {
        if (!hyperbola.contains(x, y))
            return false;
        Point2D point = hyperbola.toLocal(new Point2D(x, y));
        return point.getX()>0;
    }

    /** Throws an UnboundedShapeException */
    public void draw(Graphics2D g) {
        throw new UnboundedShapeException();
    }
    
    // ===================================================================
    // methods overriding Object class

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof HyperbolaBranch2D))
            return false;
        HyperbolaBranch2D branch = (HyperbolaBranch2D) obj;
        
        if(!hyperbola.equals(branch.hyperbola)) return false;
        return positive==branch.positive;
    }
    
    @Override
    public HyperbolaBranch2D clone() {
        return new HyperbolaBranch2D(hyperbola.clone(), positive);
    }
}
