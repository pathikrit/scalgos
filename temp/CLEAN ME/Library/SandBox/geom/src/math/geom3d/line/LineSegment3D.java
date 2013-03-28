/**
 * 
 */

package math.geom3d.line;

import java.util.ArrayList;
import java.util.Collection;

import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.transform.AffineTransform3D;

/**
 * @author dlegland
 */
public class LineSegment3D implements ContinuousCurve3D {

    // ===================================================================
    // class variables

    protected double x1 = 0;
    protected double y1 = 0;
    protected double z1 = 0;
    protected double x2 = 1;
    protected double y2 = 0;
    protected double z2 = 0;

    // ===================================================================
    // constructors

    public LineSegment3D(Point3D p1, Point3D p2) {
        this.x1 = p1.getX();
        this.y1 = p1.getY();
        this.z1 = p1.getZ();
        this.x2 = p2.getX();
        this.y2 = p2.getY();
        this.z2 = p2.getZ();
    }

    // ===================================================================
    // methods specific to StraightLine3D

    public StraightLine3D getSupportingLine() {
        return new StraightLine3D(x1, y1, z1, x2-x1, y2-y1, z2-z1);
    }

    // ===================================================================
    // methods implementing the Curve3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getContinuousCurves()
     */
    public Collection<LineSegment3D> getContinuousCurves() {
        ArrayList<LineSegment3D> array = new ArrayList<LineSegment3D>(1);
        array.add(this);
        return array;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getFirstPoint()
     */
    public Point3D getFirstPoint() {
        return new Point3D(x1, y1, z1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getLastPoint()
     */
    public Point3D getLastPoint() {
        return new Point3D(x2, y2, z2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getPoint(double)
     */
    public Point3D getPoint(double t) {
        return getPoint(t, new Point3D());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getPoint(double, math.geom3d.Point3D)
     */
    public Point3D getPoint(double t, Point3D point) {
        if (point==null)
            point = new Point3D();
        t = Math.max(Math.min(t, 1), 0);
        point.setLocation(x1+(x2-x1)*t, y1+(y2-y1)*t, z1+(z2-z1)*t);
        return point;
    }

    /**
     * If point does not project on the line segment, return Double.NaN.
     * 
     * @see math.geom3d.curve.Curve3D#getPosition(math.geom3d.Point3D)
     */
    public double getPosition(Point3D point) {
        double t = this.getSupportingLine().getPosition(point);
        if (t>1)
            return Double.NaN;
        if (t<0)
            return Double.NaN;
        return t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getReverseCurve()
     */
    public Curve3D getReverseCurve() {
        return new StraightLine3D(getLastPoint(), getFirstPoint());
    }

    /**
     * Returns the2 end points.
     * 
     * @see math.geom3d.curve.Curve3D#getSingularPoints()
     */
    public Collection<Point3D> getSingularPoints() {
        ArrayList<Point3D> points = new ArrayList<Point3D>(2);
        points.add(getFirstPoint());
        points.add(getLastPoint());
        return points;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getSubCurve(double, double)
     */
    public LineSegment3D getSubCurve(double t0, double t1) {
        t0 = Math.max(t0, 0);
        t1 = Math.min(t1, 1);
        return new LineSegment3D(getPoint(t0), getPoint(t1));
    }

    /**
     * Return 0, by definition of LineSegment.
     * 
     * @see math.geom3d.curve.Curve3D#getT0()
     */
    public double getT0() {
        return 0;
    }

    /**
     * Return 1, by definition of LineSegment.
     * 
     * @see math.geom3d.curve.Curve3D#getT1()
     */
    public double getT1() {
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#project(math.geom3d.Point3D)
     */
    public double project(Point3D point) {
        double t = getSupportingLine().project(point);
        return Math.min(Math.max(t, 0), 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#transform(math.geom3d.transform.AffineTransform3D)
     */
    public Curve3D transform(AffineTransform3D trans) {
        return new LineSegment3D(new Point3D(x1, y1, z1).transform(trans),
                new Point3D(x2, y2, z2).transform(trans));
    }

    // ===================================================================
    // methods implementing the Shape3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#clip(math.geom3d.Box3D)
     */
    public Shape3D clip(Box3D box) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#contains(math.geom3d.Point3D)
     */
    public boolean contains(Point3D point) {
        StraightLine3D line = this.getSupportingLine();
        if (!line.contains(point))
            return false;
        double t = line.getPosition(point);
        if (t<-Shape3D.ACCURACY)
            return false;
        if (t>1+Shape3D.ACCURACY)
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getBoundingBox()
     */
    public Box3D getBoundingBox() {
        return new Box3D(x1, x2, y1, y2, z1, z2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getDistance(math.geom3d.Point3D)
     */
    public double getDistance(Point3D point) {
        double t = this.project(point);
        return getPoint(t).getDistance(point);
    }

    /**
     * Returns true, as a LineSegment3D is always bounded.
     * 
     * @see math.geom3d.Shape3D#isBounded()
     */
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns false, as a LineSegment3D is never empty.
     * 
     * @see math.geom3d.Shape3D#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }

}
