/* file : EllipseArc2D.java
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
 * Created on 24 avr. 2006
 *
 */

package math.geom2d.conic;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Angle2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Vector2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.SmoothOrientedCurve2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.line.Ray2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.polygon.Polyline2D;

/**
 * An arc of ellipse. It is defined by a supporting ellipse, a starting angle,
 * and a signed angle extent, both in radians. The ellipse arc is oriented
 * counter-clockwise if angle extent is positive, and clockwise otherwise.
 * 
 * @author dlegland
 */
public class EllipseArc2D implements SmoothOrientedCurve2D, Cloneable {

    /** The supporting ellipse */
    protected Ellipse2D ellipse;

    /** The starting position on ellipse, in radians between 0 and +2PI */
    protected double    startAngle  = 0;

    /** The signed angle extent, in radians between -2PI and +2PI. */
    protected double    angleExtent = Math.PI;

    // ====================================================================
    // Constructors

    /**
     * Construct a default Ellipse arc, centered on (0,0), with radii equal to 1
     * and 1, orientation equal to 0, start angle equal to 0, and angle extent
     * equal to PI/2.
     */
    public EllipseArc2D() {
        this(0, 0, 1, 1, 0, 0, Math.PI/2);
    }

    /**
     * Specify supporting ellipse, start angle and angle extent.
     * 
     * @param ell the supporting ellipse
     * @param start the starting angle (angle between 0 and 2*PI)
     * @param extent the angle extent (signed angle)
     */
    public EllipseArc2D(Ellipse2D ell, double start, double extent) {
        this(ell.xc, ell.yc, ell.r1, ell.r2, ell.theta, start, extent);
    }

    /**
     * Specify supporting ellipse, start angle and end angle, and a flag
     * indicating whether the arc is directed or not.
     * 
     * @param ell the supporting ellipse
     * @param start the starting angle
     * @param end the ending angle
     * @param direct flag indicating if the arc is direct
     */
    public EllipseArc2D(Ellipse2D ell, double start, double end, boolean direct) {
        this(ell.xc, ell.yc, ell.r1, ell.r2, ell.theta, start, end, direct);
    }

    /**
     * Specify parameters of supporting ellipse, start angle, and angle extent.
     */
    public EllipseArc2D(double xc, double yc, double a, double b, double theta,
            double start, double extent) {
        this.ellipse = new Ellipse2D(xc, yc, a, b, theta);
        this.startAngle = start;
        this.angleExtent = extent;
    }

    /**
     * Specify parameters of supporting ellipse, bounding angles and flag for
     * direct ellipse.
     */
    public EllipseArc2D(double xc, double yc, double a, double b, double theta,
            double start, double end, boolean direct) {
        this.ellipse = new Ellipse2D(xc, yc, a, b, theta);
        this.startAngle = start;
        this.angleExtent = Angle2D.formatAngle(end-start);
        if (!direct)
            this.angleExtent = this.angleExtent-Math.PI*2;
    }

    // ====================================================================
    // methods specific to EllipseArc2D

    public boolean containsAngle(double angle) {
        return Angle2D.containsAngle(startAngle, startAngle+angleExtent, angle,
                angleExtent>0);
    }

    /** Get angle associated to given position */
    public double getAngle(double position) {
        if (position<0)
            position = 0;
        if (position>Math.abs(angleExtent))
            position = Math.abs(angleExtent);
        if (angleExtent<0)
            position = -position;
        return Angle2D.formatAngle(startAngle+position);
    }

    // ====================================================================
    // methods from interface OrientedCurve2D

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.ContinuousCurve2D#getViewAngle(math.geom2d.Point2D)
     */
    public double getWindingAngle(java.awt.geom.Point2D point) {
        Point2D p1 = getPoint(0);
        Point2D p2 = getPoint(Math.abs(angleExtent));

        // compute angle of point with extreme points
        double angle1 = Angle2D.getHorizontalAngle(point, p1);
        double angle2 = Angle2D.getHorizontalAngle(point, p2);

        // test on which 'side' of the arc the point lie
        boolean b1 = (new StraightLine2D(p1, p2)).isInside(point);
        boolean b2 = ellipse.isInside(point);

        if (angleExtent>0) {
            if (b1||b2) { // inside of ellipse arc
                if (angle2>angle1)
                    return angle2-angle1;
                else
                    return 2*Math.PI-angle1+angle2;
            } else { // outside of ellipse arc
                if (angle2>angle1)
                    return angle2-angle1-2*Math.PI;
                else
                    return angle2-angle1;
            }
        } else {
            if (!b1||b2) {
                if (angle1>angle2)
                    return angle2-angle1;
                else
                    return angle2-angle1-2*Math.PI;
            } else {
                if (angle1>angle2)
                    return angle2-angle1+2*Math.PI;
                else
                    return angle2-angle1;
            }
            // if(b1 || b2){
            // if(angle1>angle2) return angle1 - angle2;
            // else return 2*Math.PI - angle2 + angle1;
            // }else{
            // if(angle1>angle2) return angle1 - angle2 - 2*Math.PI;
            // else return angle1 - angle2;
            // }
        }
    }

    public boolean isInside(java.awt.geom.Point2D p) {
        return getSignedDistance(p.getX(), p.getY())<0;
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
        boolean direct = angleExtent>0;

        double dist = getDistance(x, y);
        Point2D point = new Point2D(x, y);

        boolean inside = ellipse.isInside(point);
        if (inside)
            return angleExtent>0 ? -dist : dist;

        Point2D p1 = getPoint(startAngle);
        Point2D p2 = getPoint(startAngle+angleExtent);
        boolean onLeft = (new StraightLine2D(p1, p2)).isInside(point);

        if (direct&&!onLeft)
            return dist;
        if (!direct&&onLeft)
            return -dist;

        boolean left1 = (new Ray2D(p1, -Math.sin(startAngle), Math
                .cos(startAngle))).isInside(point);
        if (direct&&!left1)
            return dist;
        if (!direct&&left1)
            return -dist;

        boolean left2 = (new Ray2D(p2, -Math.sin(startAngle+angleExtent), Math
                .cos(startAngle+angleExtent))).isInside(point);
        if (direct&&!left2)
            return dist;
        if (!direct&&left2)
            return -dist;

        if (direct)
            return -dist;
        else
            return dist;
    }

    // ====================================================================
    // methods from interface SmoothCurve2D

    public Vector2D getTangent(double t) {
        // convert position to angle
        if (angleExtent<0)
            t = startAngle-t;
        else
            t = startAngle+t;
        return ellipse.getTangent(t);
    }

    /**
     * returns the curvature of the ellipse arc.
     */
    public double getCurvature(double t) {
        // convert position to angle
        if (angleExtent<0)
            t = startAngle-t;
        else
            t = startAngle+t;
        return ellipse.getCurvature(t);
    }

    // ====================================================================
    // methods from interface ContinuousCurve2D

    public Polyline2D getAsPolyline(int n) {
        Point2D[] points = new Point2D[n+1];

        double dt = this.angleExtent/n;
        if (this.angleExtent>0)
            for (int i = 0; i<n+1; i++)
                points[i] = this.getPoint((i)*dt+startAngle);
        else
            for (int i = 0; i<n+1; i++)
                points[i] = this.getPoint(-((double) i)*dt+startAngle);

        return new Polyline2D(points);
    }

    /** Returns false, as an ellipse arc is never closed. */
    public boolean isClosed() {
        return false;
    }

    /**
     * Returns a SmoothCurve array containing this ellipse arc.
     * 
     * @see math.geom2d.curve.ContinuousCurve2D#getSmoothPieces()
     */
    public Collection<? extends SmoothCurve2D> getSmoothPieces() {
        ArrayList<EllipseArc2D> list = new ArrayList<EllipseArc2D>(1);
        list.add(this);
        return list;
    }

    // ====================================================================
    // methods from interface Curve2D

    /** Always returns 0 */
    public double getT0() {
        return 0;
    }

    /** Always returns the absolute value of the angle extent */
    public double getT1() {
        return Math.abs(angleExtent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve2D#getPoint(double, math.geom2d.Point2D)
     */
    public Point2D getPoint(double t) {
        // check bounds
        t = Math.max(t, 0);
        t = Math.min(t, Math.abs(angleExtent));

        // convert position to angle
        if (angleExtent<0)
            t = startAngle-t;
        else
            t = startAngle+t;

        // return corresponding point
        return ellipse.getPoint(t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve2D#getPosition(math.geom2d.Point2D)
     */
    public double getPosition(java.awt.geom.Point2D point) {
        double angle = Angle2D.getHorizontalAngle(ellipse.getCenter(), point);
        if (containsAngle(angle))
            if (angleExtent>0)
                return Angle2D.formatAngle(angle-startAngle);
            else
                return Angle2D.formatAngle(startAngle-angle);

        // return either 0 or 1, depending on which extremity is closer.
        return getFirstPoint().distance(point)<getLastPoint().distance(point) ? 0
                : Math.abs(angleExtent);
    }

    public double project(java.awt.geom.Point2D point) {
        double angle = ellipse.project(point);

        // Case of an angle contained in the ellipse arc
        if (Angle2D.containsAngle(startAngle, startAngle+angleExtent, angle,
                angleExtent>0)) {
            if (angleExtent>0)
                return Angle2D.formatAngle(angle-startAngle);
            else
                return Angle2D.formatAngle(startAngle-angle);
        }

        Point2D p1 = this.getFirstPoint();
        Point2D p2 = this.getLastPoint();
        if (p1.getDistance(point)<p2.getDistance(point))
            return 0;
        else
            return Math.abs(angleExtent);

        // // convert to arc parameterization
        // if(angleExtent>0)
        // angle = Angle2D.formatAngle(angle-startAngle);
        // else
        // angle = Angle2D.formatAngle(startAngle-angle);
        //		
        // // ensure projection lies on the arc
        // if(angle<0) return 0;
        // if(angle>Math.abs(angleExtent)) return Math.abs(angleExtent);
        //		
        // return angle;
    }

    /**
     * Get the first point of the curve.
     * 
     * @return the first point of the curve
     */
    public Point2D getFirstPoint() {
        return this.getPoint(0);
    }

    /**
     * Get the last point of the curve.
     * 
     * @return the last point of the curve.
     */
    public Point2D getLastPoint() {
        return this.getPoint(Math.abs(angleExtent));
    }

    public Collection<Point2D> getSingularPoints() {
        ArrayList<Point2D> list = new ArrayList<Point2D>(2);
        list.add(this.getFirstPoint());
        list.add(this.getLastPoint());
        return list;
    }

    public boolean isSingular(double pos) {
        if (Math.abs(pos)<Shape2D.ACCURACY)
            return true;
        if (Math.abs(pos-Math.abs(angleExtent))<Shape2D.ACCURACY)
            return true;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve2D#getIntersections(math.geom2d.LinearShape2D)
     */
    public Collection<Point2D> getIntersections(LinearShape2D line) {

        // check point contained in it
        ArrayList<Point2D> array = new ArrayList<Point2D>();
        for (Point2D point : ellipse.getIntersections(line))
            if (contains(point))
                array.add(point);

        return array;
    }

    /**
     * Returns the ellipse arc which refers to the reversed parent ellipse, with
     * same start angle, and with opposite angle extent.
     */
    public EllipseArc2D getReverseCurve() {
        return new EllipseArc2D(ellipse, Angle2D.formatAngle(startAngle
                +angleExtent), -angleExtent);
    }

    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    /**
     * Returns a new EllipseArc2D.
     */
    public EllipseArc2D getSubCurve(double t0, double t1) {
        // convert position to angle
        t0 = Angle2D.formatAngle(startAngle+t0);
        t1 = Angle2D.formatAngle(startAngle+t1);

        // check bounds of angles
        if (!Angle2D.containsAngle(startAngle, startAngle+angleExtent, t0,
                angleExtent>0))
            t0 = startAngle;
        if (!Angle2D.containsAngle(startAngle, startAngle+angleExtent, t1,
                angleExtent>0))
            t1 = angleExtent;

        // create new arc
        return new EllipseArc2D(ellipse, t0, t1, angleExtent>0);
    }

    // ====================================================================
    // methods from interface Shape2D

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Shape2D#getDistance(math.geom2d.Point2D)
     */
    public double getDistance(java.awt.geom.Point2D point) {
        return getDistance(point.getX(), point.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Shape2D#getDistance(double, double)
     */
    public double getDistance(double x, double y) {
        Point2D p = getPoint(project(new Point2D(x, y)));
        return p.getDistance(x, y);
    }

    /** Always return true: an ellipse arc is bounded by definition */
    public boolean isBounded() {
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    /**
     * Clip the ellipse arc by a box. The result is an instance of CurveSet2D<EllipseArc2D>,
     * which contains only instances of EllipseArc2D. If the ellipse arc is not
     * clipped, the result is an instance of CurveSet2D<EllipseArc2D> which
     * contains 0 curves.
     */
    public CurveSet2D<? extends EllipseArc2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<SmoothCurve2D> set = Curve2DUtils.clipSmoothCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<EllipseArc2D> result = new CurveSet2D<EllipseArc2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof EllipseArc2D)
                result.addCurve((EllipseArc2D) curve);
        }
        return result;
    }

    public Box2D getBoundingBox() {

        // first get ending points
        Point2D p0 = getFirstPoint();
        Point2D p1 = getLastPoint();

        // get coordinate of ending points
        double x0 = p0.getX();
        double y0 = p0.getY();
        double x1 = p1.getX();
        double y1 = p1.getY();

        // intialize min and max coords
        double xmin = Math.min(x0, x1);
        double xmax = Math.max(x0, x1);
        double ymin = Math.min(y0, y1);
        double ymax = Math.max(y0, y1);

        // check cases arc contains one maximum
        Point2D center = ellipse.getCenter();
        double xc = center.getX();
        double yc = center.getY();
        if (Angle2D.containsAngle(startAngle, startAngle+angleExtent, Math.PI/2
                +ellipse.theta, angleExtent>=0))
            ymax = Math.max(ymax, yc+ellipse.r1);
        if (Angle2D.containsAngle(startAngle, startAngle+angleExtent, 3*Math.PI
                /2+ellipse.theta, angleExtent>=0))
            ymin = Math.min(ymin, yc-ellipse.r1);
        if (Angle2D.containsAngle(startAngle, startAngle+angleExtent,
                ellipse.theta, angleExtent>=0))
            xmax = Math.max(xmax, xc+ellipse.r2);
        if (Angle2D.containsAngle(startAngle, startAngle+angleExtent, Math.PI
                +ellipse.theta, angleExtent>=0))
            xmin = Math.min(xmin, xc-ellipse.r2);

        // return a bounding with computed limits
        return new Box2D(xmin, xmax, ymin, ymax);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Shape2D#transform(math.geom2d.AffineTransform2D)
     */
    public EllipseArc2D transform(AffineTransform2D trans) {
        // transform supporting ellipse
        Ellipse2D ell = ellipse.transform(trans);

        // ensure ellipse is direct
        if (!ell.isDirect())
            ell = ell.getReverseCurve();

        // Compute position of end points on the transformed ellipse
        double startPos = ell.project(this.getFirstPoint().transform(trans));
        double endPos = ell.project(this.getLastPoint().transform(trans));

        // Compute the new arc
        boolean direct = !(angleExtent>0^trans.isDirect());
        return new EllipseArc2D(ell, startPos, endPos, direct);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(double, double)
     */
    public boolean contains(double x, double y) {
        return getDistance(x, y)>Shape2D.ACCURACY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(java.awt.geom.Point2D)
     */
    public boolean contains(java.awt.geom.Point2D point) {
        return contains(point.getX(), point.getY());
    }

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        // TODO: should be better to replace be cubic arcs

        int N = 60;
        Point2D point;

        double dt = Math.abs(angleExtent)/N;
        for (int i = 1; i<N; i++) {
            point = this.getPoint(i*dt);
            path.lineTo((float) point.getX(), (float) point.getY());
        }

        point = this.getLastPoint();
        path.lineTo((float) point.getX(), (float) point.getY());

        return path;
    }

    /**
     * @deprecated 
     */
    @Deprecated
    public java.awt.geom.GeneralPath getGeneralPath() {
        // create new path
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();

        // move to the first point
        Point2D point = this.getFirstPoint();
        path.moveTo((float) point.getX(), (float) point.getY());

        // append the curve
        path = this.appendPath(path);

        // return the final path
        return path;
    }

    public void draw(Graphics2D g2) {
        g2.draw(this.getGeneralPath());
    }

    // ====================================================================
    // methods from interface Object

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EllipseArc2D))
            return false;
        EllipseArc2D arc = (EllipseArc2D) obj;

        // test whether supporting ellipses have same support
        if (Math.abs(ellipse.xc-arc.ellipse.xc)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(ellipse.yc-arc.ellipse.yc)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(ellipse.r1-arc.ellipse.r1)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(ellipse.r2-arc.ellipse.r2)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(ellipse.theta-arc.ellipse.theta)>Shape2D.ACCURACY)
            return false;

        // test if angles are the same
        if (!Angle2D.equals(startAngle, arc.startAngle))
            return false;
        if (!Angle2D.equals(angleExtent, arc.angleExtent))
            return false;

        return true;
    }
    
    @Override
    public EllipseArc2D clone() {
        return new EllipseArc2D(ellipse, startAngle, angleExtent);
    }
}
