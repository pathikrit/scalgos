/* file : CircleArc2D.java
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
 * Created on 29 avr. 2006
 *
 */

package math.geom2d.conic;

import java.util.*;

import math.geom2d.AffineTransform2D;
import math.geom2d.Angle2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Vector2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.line.Ray2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.line.LinearShape2D;

/**
 * A circle arc, defined by the center and the radius of the containing circle,
 * by a starting angle, and by a (signed) angle extent.
 * <p>
 * A circle arc is directed: if angle extent is positive, the arc is counter
 * clockwise. Otherwise, it is clockwise.
 * <p>
 * A circle arc is parameterized using angle from center. The arc contains all
 * points with a parametric equation of t, for each t between 0 and the angle
 * extent.
 * 
 * @author dlegland
 */
public class CircleArc2D extends EllipseArc2D implements Cloneable {

    protected Circle2D circle;

    // ====================================================================
    // constructors

    /**
     * Create a circle arc whose support circle is centered on (0,0) and has a
     * radius equal to 1. Start angle is 0, and angle extent is PI/2.
     */
    public CircleArc2D() {
        this(0, 0, 1, 0, Math.PI/2);
    }

    // Constructors based on Circles

    /**
     * create a new circle arc based on an already existing circle.
     */
    public CircleArc2D(Circle2D circle, double startAngle, double angleExtent) {
        this(circle.xc, circle.yc, circle.r, startAngle, angleExtent);
    }

    /**
     * create a new circle arc based on an already existing circle, specifying
     * if arc is direct or not.
     */
    public CircleArc2D(Circle2D circle, double startAngle, double endAngle,
            boolean direct) {
        this(circle.xc, circle.yc, circle.r, startAngle, endAngle, direct);
    }

    // Constructors based on points

    /** Create a new circle arc with specified point center and radius */
    public CircleArc2D(Point2D center, double radius, double startAngle,
            double angleExtent) {
        this(center.getX(), center.getY(), radius, startAngle, angleExtent);
    }

    /**
     * Create a new circle arc with specified point center and radius, start and
     * end angles, and by specifying whether arc is direct or not.
     */
    public CircleArc2D(Point2D center, double radius, double start, double end,
            boolean direct) {
        this(center.getX(), center.getY(), radius, start, end, direct);
    }

    // Constructors based on doubles

    /**
     * Base constructor, for constructiong arc from circle parameters, start and
     * end angles, and by specifying whether arc is direct or not.
     */
    public CircleArc2D(double xc, double yc, double r, double start,
            double end, boolean direct) {
        super(xc, yc, r, r, 0, start, end, direct);
        this.circle = new Circle2D(xc, yc, r);
        this.ellipse = this.circle;
    }

    /** Base constructor with all parameters specified */
    public CircleArc2D(double xc, double yc, double r, double start,
            double extent) {
        super(xc, yc, r, r, 0, start, extent);
        this.circle = new Circle2D(xc, yc, r);
        this.ellipse = this.circle;
        // this.startAngle = start;
        // this.angleExtent = extent;
    }

    // ====================================================================
    // methods specific to CircleArc2D

    /**
     * convert position on curve to angle with circle center.
     */
    private double positionToAngle(double t) {
        if (t>Math.abs(angleExtent))
            t = Math.abs(angleExtent);
        if (t<0)
            t = 0;
        if (angleExtent<0)
            t = -t;
        t = t+startAngle;
        return t;
    }

    /**
     * Returns the circle which contains the circle arc.
     */
    public Circle2D getSupportCircle() {
        return circle;
    }

    /**
     * Change the center of the support circle.
     * 
     * @param point the new center of the arc.
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setCenter(Point2D point) {
        circle.xc = point.getX();
        circle.yc = point.getY();
    }

    /**
     * Change the radius of the support circle
     * 
     * @param r the new radius
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setRadius(double r) {
        circle.r = r;
    }

    /**
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setArc(Point2D center, double radius, double start,
            double extent) {
        circle.xc = center.getX();
        circle.yc = center.getY();
        circle.r = radius;
        startAngle = start;
        angleExtent = extent;
    }

    public boolean isDirect() {
        return angleExtent>0;
    }

    public double getLength() {
        return circle.r*Math.abs(angleExtent);
    }

    // ====================================================================
    // methods from interface OrientedCurve2D

    @Override
    public double getWindingAngle(java.awt.geom.Point2D point) {
        Point2D p1 = getFirstPoint();
        Point2D p2 = getLastPoint();

        // compute angle of point with extreme points
        double angle1 = Angle2D.getHorizontalAngle(point, p1);
        double angle2 = Angle2D.getHorizontalAngle(point, p2);

        // boolean b0 = circle.isInside(point);
        // boolean b1 = new StraightLine2D(p1,
        // this.getTangent(0)).isInside(point);
        // boolean b2 = new StraightLine2D(p2,
        // this.getTangent(Math.abs(angleExtent))).isInside(point);
        //		
        // // True if point is located in 'triangular' area formed by arc edge
        // // and tangents at extremities
        // boolean bl = new StraightLine2D(p2, p1).isInside(point) && b1 && b2;
        //		
        // if(angleExtent>0){
        // if(b0 || bl){
        // if(angle2>angle1) return angle2 - angle1;
        // else return 2*Math.PI - angle1 + angle2;
        // }else{
        // if(angle2>angle1) return angle2 - angle1 - 2*Math.PI;
        // else return angle2 - angle1;
        // }
        // }else{
        // if(b0 || bl){
        // if(angle1>angle2) return angle1 - angle2;
        // else return 2*Math.PI - angle2 + angle1;
        // }else{
        // if(angle1>angle2) return angle1 - angle2 - 2*Math.PI;
        // else return angle1 - angle2;
        // }
        // }
        // test on which 'side' of the arc the point lie
        boolean b1 = (new StraightLine2D(p1, p2)).isInside(point);
        boolean b2 = ellipse.isInside(point);

        if (angleExtent>0) {
            if (b1||b2) {
                if (angle2>angle1)
                    return angle2-angle1;
                else
                    return 2*Math.PI-angle1+angle2;
            } else {
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
            // }else{
            // if(b1 || b2){
            // if(angle1>angle2) return angle1 - angle2;
            // else return 2*Math.PI - angle2 + angle1;
            // }else{
            // if(angle1>angle2) return angle1 - angle2 - 2*Math.PI;
            // else return angle1 - angle2;
            // }
        }
    }

    @Override
    public boolean isInside(java.awt.geom.Point2D point) {
        return getSignedDistance(point.getX(), point.getY())<0;
    }

    @Override
    public double getSignedDistance(java.awt.geom.Point2D p) {
        return getSignedDistance(p.getX(), p.getY());
    }

    @Override
    public double getSignedDistance(double x, double y) {
        double dist = getDistance(x, y);
        Point2D point = new Point2D(x, y);

        boolean direct = angleExtent>0;
        // boolean inCircle = Point2D.getDistance(x, y, xc, yc)<=r;
        boolean inCircle = circle.isInside(point);
        if (inCircle)
            return angleExtent>0 ? -dist : dist;

        Point2D p1 = circle.getPoint(startAngle);
        Point2D p2 = circle.getPoint(startAngle+angleExtent);
        boolean onLeft = (new StraightLine2D(p1, p2)).isInside(point);

        if (direct&&!onLeft)
            return dist;
        if (!direct&&onLeft)
            return -dist;

        boolean left1 = (new Ray2D(p1, circle.getTangent(startAngle)))
                .isInside(point);
        if (direct&&!left1)
            return dist;
        if (!direct&&left1)
            return -dist;

        boolean left2 = (new Ray2D(p2, circle
                .getTangent(startAngle+angleExtent))).isInside(point);
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

    @Override
    public Vector2D getTangent(double t) {
        t = this.positionToAngle(t);

        double r = circle.getRadius();
        if (angleExtent>0)
            return new Vector2D(-r*Math.sin(t), r*Math.cos(t));
        else
            return new Vector2D(r*Math.sin(t), -r*Math.cos(t));
    }

    // ===================================================================
    // methods from interface ContinuousCurve2D

    @Override
    public Collection<? extends SmoothCurve2D> getSmoothPieces() {
        ArrayList<CircleArc2D> list = new ArrayList<CircleArc2D>(1);
        list.add(this);
        return list;
    }

    /**
     * a circle arc is never closed by definition.
     */
    @Override
    public boolean isClosed() {
        return false;
    }

    // ====================================================================
    // methods from interface Curve2D

    /** Always return 0 */
    @Override
    public double getT0() {
        return 0;
    }

    /**
     * return the last position of the circle are, which is given by the angle
     * extent of the arc.
     */
    @Override
    public double getT1() {
        return Math.abs(this.angleExtent);
    }

    /**
     * Returns the position of a point form the curvilinear position.
     */
    @Override
    public Point2D getPoint(double t) {
        t = this.positionToAngle(t);
        return circle.getPoint(t);
    }

    /**
     * Get the first point of the curve.
     * 
     * @return the first point of the curve
     */
    @Override
    public Point2D getFirstPoint() {
        return circle.getPoint(startAngle);
    }

    /**
     * Get the last point of the curve.
     * 
     * @return the last point of the curve.
     */
    @Override
    public Point2D getLastPoint() {
        return circle.getPoint(startAngle+angleExtent);
    }

    /**
     * return relative position between 0 and the angle extent.
     */
    @Override
    public double getPosition(java.awt.geom.Point2D point) {
        double angle = Angle2D.getHorizontalAngle(circle.getCenter(), point);
        if (containsAngle(angle))
            if (angleExtent>0)
                return Angle2D.formatAngle(angle-startAngle);
            else
                return Angle2D.formatAngle(startAngle-angle);

        // return either 0 or 1, depending on which extremity is closer.
        return getFirstPoint().distance(point)<getLastPoint().distance(point) ? 0
                : Math.abs(angleExtent);
    }

    /**
     * Compute intersections of the circle arc with a line. Return an array of
     * Point2D, of size 0, 1 or 2 depending on the distance between circle and
     * line. If there are 2 intersections points, the first one in the array is
     * the first one on the line.
     */
    @Override
    public Collection<Point2D> getIntersections(LinearShape2D line) {
        // extract intersection with supporting circle
        Collection<Point2D> points = circle.getIntersections(line);

        // if no intersection, return empty array
        if (points.size()==0)
            return points;

        // prepare iteration on points
        // double[] angle = new double[points.length];
        Point2D center = circle.getCenter();
        ArrayList<Point2D> list = new ArrayList<Point2D>();

        // iteration for each point
        for (Point2D point : points) {
            // angle of current point with horizontal
            double angle = Angle2D.getHorizontalAngle(center, point);

            // keep only points on the line, and with angle condition
            if (this.containsAngle(angle)&&line.contains(point))
                list.add(point);
        }

        // return result;
        return list;
    }

    @Override
    public double project(java.awt.geom.Point2D point) {
        double angle = circle.project(point);

        // Case of an angle contained in the circle arc
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

    // ====================================================================
    // methods from interface Shape2D

    @Override
    public double getDistance(java.awt.geom.Point2D p) {
        return getDistance(p.getX(), p.getY());
    }

    @Override
    public double getDistance(double x, double y) {
        double angle = Angle2D.getHorizontalAngle(circle.xc, circle.yc, x, y);

        if (containsAngle(angle))
            return Math.abs(Point2D.getDistance(circle.xc, circle.yc, x, y)
                    -circle.r);
        else
            return Math.min(getFirstPoint().getDistance(x, y), getLastPoint()
                    .getDistance(x, y));
    }

    /** Always return true */
    @Override
    public boolean isBounded() {
        return true;
    }

    /**
     * return a new CircleArc2D. Variables t0 and t1 must be comprised between 0
     * and the angle extent of the arc.
     */
    @Override
    public CircleArc2D getSubCurve(double t0, double t1) {
        // convert position to angle
        if (angleExtent>0) {
            t0 = Angle2D.formatAngle(startAngle+t0);
            t1 = Angle2D.formatAngle(startAngle+t1);
        } else {
            t0 = Angle2D.formatAngle(startAngle-t0);
            t1 = Angle2D.formatAngle(startAngle-t1);
        }

        // check bounds of angles
        if (!Angle2D.containsAngle(startAngle, startAngle+angleExtent, t0,
                angleExtent>0))
            t0 = startAngle;
        if (!Angle2D.containsAngle(startAngle, startAngle+angleExtent, t1,
                angleExtent>0))
            t1 = Angle2D.formatAngle(startAngle+angleExtent);

        // create new arc
        return new CircleArc2D(circle, t0, t1, angleExtent>0);
    }

    /**
     * Returns the circle arc which refers to the same parent circle, with same
     * start angle, and with opposite angle extent.
     */
    @Override
    public CircleArc2D getReverseCurve() {
        return new CircleArc2D(this.circle, Angle2D.formatAngle(startAngle
                +angleExtent), -angleExtent);
    }

    /**
     * Clip the circle arc by a box. The result is a CurveSet2D, which contains
     * only instances of CircleArc2D. If circle arc is not clipped, the result
     * is an instance of CurveSet2D with zero curves.
     */
    @Override
    public CurveSet2D<CircleArc2D> clip(Box2D box) {
        // Clip he curve
        CurveSet2D<SmoothCurve2D> set = Curve2DUtils.clipSmoothCurve(this, box);

        // create a new structure for storing result
        CurveSet2D<CircleArc2D> result = new CurveSet2D<CircleArc2D>();

        // convert result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof CircleArc2D)
                result.addCurve((CircleArc2D) curve);
        }
        return result;
    }

    /**
     * Returns an instance of EllipseArc2D, or CircleArc2D if transform is a
     * similarity.
     */
    @Override
    public EllipseArc2D transform(AffineTransform2D trans) {
        if (!AffineTransform2D.isSimilarity(trans))
            return super.transform(trans);

        // System.out.println("transform a circle");

        // extract the control points
        Point2D center = circle.getCenter();
        Point2D point1 = this.getFirstPoint();
        Point2D point2 = this.getLastPoint();

        // transform each point
        center = center.transform(trans);
        point1 = point1.transform(trans);
        point2 = point2.transform(trans);

        // compute new angles
        double angle1 = Angle2D.getHorizontalAngle(center, point1);
        double angle2 = Angle2D.getHorizontalAngle(center, point2);

        // compute factor of transform
        double[] coefs = trans.getCoefficients();
        double factor = Math.sqrt(coefs[0]*coefs[0]+coefs[3]*coefs[3]);

        // compute parameters of new circle arc
        double xc = center.getX(), yc = center.getY();
        double r2 = circle.getRadius()*factor;
        double startAngle = angle1;
        double angleExtent = Angle2D.formatAngle(angle2-angle1);

        boolean b1 = AffineTransform2D.isDirect(trans);
        boolean b2 = this.isDirect();
        if (b1&!b2|!b1&b2)
            angleExtent = angleExtent-2*Math.PI;

        // return new CircleArc
        return new CircleArc2D(xc, yc, r2, startAngle, angleExtent);
    }

    // // following are inherited from EllipseArc2D
    // public java.awt.Rectangle getBounds() {
    // java.awt.geom.Rectangle2D bounds = this.getBounds2D();
    // int xmin = (int) bounds.getMinX();
    // int ymin = (int) bounds.getMinY();
    // int xmax = (int) Math.ceil(bounds.getMaxX());
    // int ymax = (int) Math.ceil(bounds.getMaxY());
    // return new java.awt.Rectangle(xmin, ymin, xmax-xmin, ymax-ymin);
    // }
    //
    // /**
    // * Returns more precise bounds for the shape. Result is an instance of Box2D.
    // */
    // public java.awt.geom.Rectangle2D getBounds2D() {
    // Point2D p; double x, y;
    //		
    // double xc = circle.xc;
    // double yc = circle.yc;
    // double r = circle.r;
    //
    // p = getFirstPoint(); x = p.getX(); y = p.getY();
    // double xmin = x; double ymin = y;
    // double xmax = x; double ymax = y;
    //
    // p = getLastPoint(); x = p.getX(); y = p.getY();
    // xmin = Math.min(xmin, x); ymin = Math.min(ymin, y);
    // xmax = Math.max(xmax, x); ymax = Math.max(ymax, y);
    //
    // if(containsAngle(0)){
    // x = xc+r; y = yc;
    // xmin = Math.min(xmin, x); ymin = Math.min(ymin, y);
    // xmax = Math.max(xmax, x); ymax = Math.max(ymax, y);
    // }
    //
    // if(containsAngle(Math.PI)){
    // x = xc-r; y = yc;
    // xmin = Math.min(xmin, x); ymin = Math.min(ymin, y);
    // xmax = Math.max(xmax, x); ymax = Math.max(ymax, y);
    // }
    //
    // if(containsAngle(Math.PI/2)){
    // x = xc; y = yc+r;
    // xmin = Math.min(xmin, x); ymin = Math.min(ymin, y);
    // xmax = Math.max(xmax, x); ymax = Math.max(ymax, y);
    // }
    //
    // if(containsAngle(3*Math.PI/2)){
    // x = xc; y = yc-r;
    // xmin = Math.min(xmin, x); ymin = Math.min(ymin, y);
    // xmax = Math.max(xmax, x); ymax = Math.max(ymax, y);
    // }
    //		
    // return new Box2D(xmin, ymin, (xmax-xmin), (ymax-ymin));
    // }

    @Override
    public boolean contains(java.awt.geom.Point2D p) {
        return contains(p.getX(), p.getY());
    }

    @Override
    public boolean contains(double x, double y) {
        // Check if radius is correct
        if (Math.abs(Point2D.getDistance(circle.xc, circle.yc, x, y)-circle.r)>Shape2D.ACCURACY)
            return false;

        // check if angle is contained in interval [startAngle-angleExtent]
        double angle = Angle2D.getHorizontalAngle(circle.xc, circle.yc, x, y);
        if (angleExtent>0)
            return Angle2D.formatAngle(angle-startAngle)<=Angle2D
                    .formatAngle(angleExtent);
        else
            return Angle2D.formatAngle(angle-startAngle)>=Angle2D
                    .formatAngle(angleExtent);
    }

    // public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path){
    // double cot = Math.cos(circle.theta);
    // double sit = Math.sin(circle.theta);
    // double xc = circle.xc;
    // double yc = circle.yc;
    // double r = circle.r;
    // double endAngle = startAngle+angleExtent;
    //		
    // if(angleExtent>0)
    // for(double t=startAngle; t<endAngle; t+=angleExtent/100)
    // path.lineTo((float)(xc+r*Math.cos(t)*cot-r*Math.sin(t)*sit),
    // (float)(yc+r*Math.cos(t)*sit+r*Math.sin(t)*cot));
    // else
    // for(double t=startAngle; t>endAngle; t+=angleExtent/100)
    // path.lineTo((float)(xc+r*Math.cos(t)*cot-r*Math.sin(t)*sit),
    // (float)(yc+r*Math.cos(t)*sit+r*Math.sin(t)*cot));
    //
    // // position to the last point
    // path.lineTo((float)(xc+r*Math.cos(endAngle)*cot-r*Math.sin(endAngle)*sit),
    // (float)(yc+r*Math.cos(endAngle)*sit+r*Math.sin(endAngle)*cot));
    //
    // return path;
    // }

    // /* (non-Javadoc)
    // * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform, double)
    // */
    // public java.awt.geom.GeneralPath getInnerPath() {
    //		
    // // Creates the path
    // java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
    //		
    // double cot = Math.cos(circle.theta);
    // double sit = Math.sin(circle.theta);
    // double xc = circle.xc;
    // double yc = circle.yc;
    // double r = circle.r;
    // double endAngle = startAngle+angleExtent;
    //		
    // // position to the first point
    // path.lineTo((float)(xc+r*Math.cos(startAngle)*cot-r*Math.sin(startAngle)*sit),
    // (float)(yc+r*Math.cos(startAngle)*sit+r*Math.sin(startAngle)*cot));
    //
    // if(angleExtent>0)
    // for(double t=startAngle; t<endAngle; t+=angleExtent/100)
    // path.lineTo((float)(xc+r*Math.cos(t)*cot-r*Math.sin(t)*sit),
    // (float)(yc+r*Math.cos(t)*sit+r*Math.sin(t)*cot));
    // else
    // for(double t=startAngle; t>endAngle; t+=angleExtent/100)
    // path.lineTo((float)(xc+r*Math.cos(t)*cot-r*Math.sin(t)*sit),
    // (float)(yc+r*Math.cos(t)*sit+r*Math.sin(t)*cot));
    //		
    // return path;
    // }

    /**
     * @deprecated 
     */
    @Deprecated
    public java.awt.geom.GeneralPath getGeneralPath() {
        // Creates the path
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        Point2D point = getFirstPoint();
        path.moveTo((float) point.getX(), (float) point.getY());
        this.appendPath(path);
        return path;
    }

    /**
     * Two circle arc are equal if the have same center, same radius, same
     * starting and ending angles, and same orientation.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EllipseArc2D))
            return false;

        if (!(obj instanceof CircleArc2D))
            return super.equals(obj);

        CircleArc2D arc = (CircleArc2D) obj;
        // test whether supporting ellipses have same support
        if (Math.abs(circle.xc-arc.circle.xc)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(circle.yc-arc.circle.yc)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(circle.r-arc.circle.r)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(circle.r1-arc.circle.r1)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(circle.r2-arc.circle.r2)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(circle.theta-arc.circle.theta)>Shape2D.ACCURACY)
            return false;

        // test is angles are the same
        if (Math.abs(Angle2D.formatAngle(startAngle)
                -Angle2D.formatAngle(arc.startAngle))>Shape2D.ACCURACY)
            return false;
        if (Math.abs(Angle2D.formatAngle(angleExtent)
                -Angle2D.formatAngle(arc.angleExtent))>Shape2D.ACCURACY)
            return false;

        // if no difference, this is the same
        return true;
    }

    @Override
    public CircleArc2D clone() {
        return new CircleArc2D(circle.clone(), startAngle, angleExtent);
    }
}
