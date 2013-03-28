/* file : ParabolaArc2D.java
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
 * Created on 02 May 2007
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
import math.geom2d.UnboundedShapeException;
import math.geom2d.Vector2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.SmoothOrientedCurve2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.polygon.Polyline2D;

/**
 * An arc of parabola, defined by a parent parabola, and two limits for the
 * parametrization.
 * 
 * @author dlegland
 */
public class ParabolaArc2D implements SmoothOrientedCurve2D, Cloneable {

    protected Parabola2D parabola = new Parabola2D();

    protected double     t0       = -10;
    protected double     t1       = 10;

    private boolean      debug    = false;

    public ParabolaArc2D(Parabola2D parabola, double t0, double t1) {
        this.parabola = parabola;
        this.t0 = t0;
        this.t1 = t1;
    }

    // ==========================================================
    // methods specific to ParabolaArc2D

    /**
     * Returns the polyline approximating this parabola arc, by using
     * <code>N</code> line segments. If Parabola arc is not bounded (i.e. one
     * of the bounds of the parametrization domain is infinite), parametriztion
     * domain is bounded by an arbitrary value.
     */
    public Polyline2D getAsPolyline(int n) {
        Point2D[] points = new Point2D[n+1];

        // avoid the cases where t0 and/or t1 is infinite
        double t0 = Math.max(this.t0, -1000);
        double t1 = Math.min(this.t1, 1000);
        if (debug)
            System.out.println("theta="+Math.toDegrees(parabola.theta)+" t0="
                    +t0+" t1="+t1);

        double dt = (t1-t0)/n;
        points[0] = this.getPoint(t0);
        for (int i = 1; i<n; i++)
            points[i] = this.getPoint((i)*dt+t0);
        points[n] = this.getPoint(t1);

        return new Polyline2D(points);
    }

    public Parabola2D getParabola() {
        return this.parabola;
    }

    // ==========================================================
    // methods implementing the OrientedCurve2D interface

    public double getWindingAngle(java.awt.geom.Point2D point) {
        double angle0, angle1;

        boolean direct = parabola.isDirect();
        boolean inside = this.isInside(point);

        if (Double.isInfinite(t0)) {
            angle0 = parabola.getAngle()+(direct ? +1 : -1)*Math.PI/2;
        } else {
            angle0 = Angle2D.getHorizontalAngle(point, parabola.getPoint(t0));
        }

        if (Double.isInfinite(t1)) {
            angle1 = parabola.getAngle()+(direct ? +1 : -1)*Math.PI/2;
        } else {
            angle1 = Angle2D.getHorizontalAngle(point, parabola.getPoint(t1));
        }

        if (inside) {
            // turn CCW -> return positive angle
            if (angle0>angle1)
                return 2*Math.PI-angle0+angle1;
            else
                return angle1-angle0;
        } else {
            // turn CW -> return negative angle
            if (angle0>angle1)
                return angle1-angle0;
            else
                return (angle1-angle0)-2*Math.PI;
        }
    }

    public double getSignedDistance(java.awt.geom.Point2D p) {
        return getSignedDistance(p.getX(), p.getY());
    }

    public double getSignedDistance(double x, double y) {
        if (isInside(new Point2D(x, y)))
            return -getDistance(x, y);
        return -getDistance(x, y);
    }

    public boolean isInside(java.awt.geom.Point2D point) {
        boolean direct = parabola.isDirect();
        boolean inside = parabola.isInside(point);
        if (inside&&direct)
            return true;
        if (!inside&&!direct)
            return false;

        double pos = parabola.project(point);

        if (pos<t0) {
            Point2D p0 = parabola.getPoint(t0);
            Vector2D v0 = parabola.getTangent(t0);
            StraightLine2D line0 = new StraightLine2D(p0, v0);
            return line0.isInside(point);
        }

        if (pos>t1) {
            Point2D p1 = parabola.getPoint(t1);
            Vector2D v1 = parabola.getTangent(t1);
            StraightLine2D line1 = new StraightLine2D(p1, v1);
            return line1.isInside(point);
        }
        return !direct;
    }

    // ==========================================================
    // methods implementing the SmoothCurve2D interface

    public Vector2D getTangent(double t) {
        return parabola.getTangent(t);
    }

    /**
     * Returns the curvature of the parabola arc.
     */
    public double getCurvature(double t) {
        return parabola.getCurvature(t);
    }

    // ==========================================================
    // methods implementing the ContinuousCurve2D interface

    public Collection<? extends SmoothCurve2D> getSmoothPieces() {
        ArrayList<ParabolaArc2D> list = new ArrayList<ParabolaArc2D>(1);
        list.add(this);
        return list;
    }

    /** Returns false, by definition of a parabola arc */
    public boolean isClosed() {
        return false;
    }

    // ====================================================================
    // methods implementing the Curve2D interface

    /**
     * Returns the position of the first point of the parabola arc.
     */
    public double getT0() {
        return t0;
    }

    /**
     * Returns the position of the last point of the parabola arc.
     */
    public double getT1() {
        return t1;
    }

    public Point2D getPoint(double t) {
        t = Math.min(Math.max(t, t0), t1);
        return parabola.getPoint(t);
    }

    /**
     * return the first point of the parabola arc.
     */
    public Point2D getFirstPoint() {
        return this.getPoint(t0);
    }

    /**
     * return the last point of the parabola arc.
     */
    public Point2D getLastPoint() {
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

    public double getPosition(java.awt.geom.Point2D point) {
        if (!this.parabola.contains(point))
            return Double.NaN;
        double t = this.parabola.getPosition(point);
        if (t-t0<-ACCURACY)
            return Double.NaN;
        if (t1-t<ACCURACY)
            return Double.NaN;
        return t;
    }

    public double project(java.awt.geom.Point2D point) {
        double t = this.parabola.project(point);
        return Math.min(Math.max(t, t0), t1);
    }

    public Collection<Point2D> getIntersections(LinearShape2D line) {
        Collection<Point2D> inters0 = this.parabola.getIntersections(line);
        ArrayList<Point2D> inters = new ArrayList<Point2D>();
        for (Point2D point : inters0) {
            double pos = this.parabola.getPosition(point);
            if (pos>this.t0&&pos<this.t1)
                inters.add(point);
        }

        return inters;
    }

    /**
     * Returns the parabola arc which refers to the reversed parent parabola,
     * and with inverted parametrization bounds.
     */
    public ParabolaArc2D getReverseCurve() {
        return new ParabolaArc2D(this.parabola.getReverseCurve(), -t1, -t0);
    }

    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    public ParabolaArc2D getSubCurve(double t0, double t1) {
        if (t1<t0)
            return null;
        t0 = Math.max(this.t0, t0);
        t1 = Math.min(this.t1, t1);
        return new ParabolaArc2D(parabola, t0, t1);
    }

    // ====================================================================
    // methods implementing the Shape2D interface

    public double getDistance(java.awt.geom.Point2D p) {
        return getDistance(p.getX(), p.getY());
    }

    public double getDistance(double x, double y) {
        // TODO Auto-generated method stub
        return this.getAsPolyline(100).getDistance(x, y);
    }

    /**
     * Returns true if the arc is bounded, i.e. if both limits are finite.
     */
    public boolean isBounded() {
        if (t0==Double.NEGATIVE_INFINITY)
            return false;
        if (t1==Double.POSITIVE_INFINITY)
            return false;
        return true;
    }

    /**
     * Return true if t1<t0.
     */
    public boolean isEmpty() {
        return t1<=t0;
    }

    /**
     * Clip the parabola arc by a box. The result is an instance of CurveSet2D<ParabolaArc2D>,
     * which contains only instances of ParabolaArc2D. If the parabola arc is
     * not clipped, the result is an instance of CurveSet2D<ParabolaArc2D>
     * which contains 0 curves.
     */
    public CurveSet2D<? extends ParabolaArc2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<SmoothCurve2D> set = Curve2DUtils.clipSmoothCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<ParabolaArc2D> result = new CurveSet2D<ParabolaArc2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof ParabolaArc2D)
                result.addCurve((ParabolaArc2D) curve);
        }
        return result;
    }

    public Box2D getBoundingBox() {
        // TODO Auto-generated method stub
        return this.getAsPolyline(100).getBoundingBox();
    }

    public ParabolaArc2D transform(AffineTransform2D trans) {
        Parabola2D par = parabola.transform(trans);

        // Compute position of end points on the transformed parabola
        double startPos = Double.isInfinite(t0) ? Double.NEGATIVE_INFINITY
                : par.project(this.getFirstPoint().transform(trans));
        double endPos = Double.isInfinite(t1) ? Double.POSITIVE_INFINITY : par
                .project(this.getLastPoint().transform(trans));

        // Compute the new arc
        return new ParabolaArc2D(par, startPos, endPos);
    }

    // ====================================================================
    // methods implementing the Shape interface

    public boolean contains(double x, double y) {
        // Check on parent parabola
        if (!parabola.contains(x, y))
            return false;
        
        // Check if position of point is inside of bounds
        double t = parabola.getPosition(new Point2D(x, y));
        if (t<this.t0)
            return false;
        if (t>this.t1)
            return false;

        return true;
    }

    public boolean contains(java.awt.geom.Point2D point) {
        return contains(point.getX(), point.getY());
    }

    // ====================================================================
    // Drawing methods

    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        if (!this.isBounded())
            throw new UnboundedShapeException();
        return this.getAsPolyline(32).appendPath(path);
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        if (!this.isBounded())
            throw new UnboundedShapeException();
        return this.getAsPolyline(32).getGeneralPath();
    }

    public void draw(Graphics2D g) {
        g.draw(this.getGeneralPath());
    }

    // ====================================================================
    // Methods inherited from object interface

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParabolaArc2D))
            return false;
        ParabolaArc2D arc = (ParabolaArc2D) obj;

        if (!this.parabola.equals(arc.parabola))
            return false;
        if (Math.abs(this.t0-arc.t0)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(this.t1-arc.t1)>Shape2D.ACCURACY)
            return false;

        return true;
    }
    
    @Override
    public ParabolaArc2D clone() {
        return new ParabolaArc2D(parabola.clone(), t0, t1);
    }
}
