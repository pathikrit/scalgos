/* File Hyperbola2D.java 
 *
 * Project : Java Geometry Library
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

// package

package math.geom2d.conic;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.UnboundedShapeException;
import math.geom2d.Vector2D;
import math.geom2d.domain.BoundarySet2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.line.StraightLine2D;

// Imports

/**
 * An Hyperbola, which is represented as a curve set of two boundary curves
 * which are instances of HyperbolaBranch2D.
 */
public class Hyperbola2D extends BoundarySet2D<HyperbolaBranch2D> implements
        Conic2D, Cloneable {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    /** Center of the hyperbola */
    double            xc      = 0;
    double            yc      = 0;

    /** first focal parameter */
    double            a       = 1;

    /** second focal parameter */
    double            b       = 1;

    /** angle of rotation of the hyperbola */
    double            theta   = 0;

    /** a flag indicating whether the hyperbola is direct or not */
    boolean           direct  = true;

    HyperbolaBranch2D branch1 = null;
    HyperbolaBranch2D branch2 = null;

    /**
     * Creates a new Hyperbola by reducing the conic coefficients, assuming
     * conic type is Hyperbola, and hyperbola is centered.
     * 
     * @param coefs an array of double with at least 3 coefficients containing
     *            coefficients for x^2, xy, and y^2 factors.
     * @return the Hyperbola2D corresponding to given coefficients
     */
    public final static Hyperbola2D reduceCentered(double[] coefs) {
        double A = coefs[0];
        double B = coefs[1];
        double C = coefs[2];

        // Compute orientation angle of the ellipse
        double theta;
        if (Math.abs(A-C)<Shape2D.ACCURACY) {
            theta = Math.PI/4;
        } else {
            theta = Math.atan2(B, (A-C))/2.0;
            if (B<0)
                theta -= Math.PI;
            theta = Angle2D.formatAngle(theta);
        }

        // compute ellipse in isothetic basis
        double[] coefs2 = Conic2DUtils.transformCentered(coefs,
                AffineTransform2D.createRotation(-theta));

        // extract coefficient f if present
        double f = 1;
        if (coefs2.length>5)
            f = Math.abs(coefs[5]);

        assert Math.abs(coefs2[0]/f)<Shape2D.ACCURACY : "Second conic coefficient should be zero";

        if (coefs2[0]*coefs2[2]>0) {
            System.err.println("Transformed conic is not an Hyperbola");
        }

        // extract major and minor axis lengths, ensuring r1 is greater
        double r1, r2;
        if (coefs2[0]>0) {
            // East-West hyperbola
            r1 = Math.sqrt(f/coefs2[0]);
            r2 = Math.sqrt(-f/coefs2[2]);
        } else {
            // North-South hyperbola
            r1 = Math.sqrt(f/coefs2[2]);
            r2 = Math.sqrt(-f/coefs2[0]);
            theta = Angle2D.formatAngle(theta+Math.PI/2);
            theta = Math.min(theta, Angle2D.formatAngle(theta+Math.PI));
        }

        // Return the new Hyperbola
        return new Hyperbola2D(0, 0, r1, r2, theta, true);
    }

    /**
     * Transform an hyperbole, by supposing both the hyperbole is centered and
     * the transform has no translation part.
     * 
     * @param hyper an hyperbole
     * @param trans an affine transform
     * @return the transformed hyperbole, centered around origin
     */
    public final static Hyperbola2D transformCentered(Hyperbola2D hyper,
            AffineTransform2D trans) {
        // Extract inner parameter of ellipse
        double a = hyper.a;
        double b = hyper.b;
        double theta = hyper.theta;

        // precompute some parts
        double aSq = a*a;
        double bSq = b*b;
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        double cotSq = cot*cot;
        double sitSq = sit*sit;

        // compute coefficients of the centered conis
        double A = cotSq/aSq-sitSq/bSq;
        double B = 2*cot*sit*(1/aSq+1/bSq);
        double C = sitSq/aSq-cotSq/bSq;
        double[] coefs = new double[] { A, B, C };

        // Compute coefficients of the transformed conic
        double[] coefs2 = Conic2DUtils.transformCentered(coefs, trans);

        // reduce conic coefficients to Ellipse
        return Hyperbola2D.reduceCentered(coefs2);
    }

    // ===================================================================
    // constructors

    /**
     * Assume centered hyperbola, with a = b = 1 (orthogonal hyperbola), theta=0
     * (hyperbola is oriented East-West), and direct orientation.
     */
    public Hyperbola2D() {
        this(0, 0, 1, 1, 0, true);
    }

    public Hyperbola2D(Point2D center, double a, double b, double theta,
            boolean d) {
        this(center.getX(), center.getY(), a, b, theta, d);
    }

    public Hyperbola2D(Point2D center, double a, double b, double theta) {
        this(center.getX(), center.getY(), a, b, theta, true);
    }

    public Hyperbola2D(double xc, double yc, double a, double b, double theta) {
        this(xc, yc, a, b, theta, true);
    }

    /** Main constructor */
    public Hyperbola2D(double xc, double yc, double a, double b, double theta,
            boolean d) {
        this.xc = xc;
        this.yc = yc;
        this.a = a;
        this.b = b;
        this.theta = theta;
        this.direct = d;

        branch1 = new HyperbolaBranch2D(this, false);
        branch2 = new HyperbolaBranch2D(this, true);
        this.addCurve(branch1);
        this.addCurve(branch2);
    }

    // ===================================================================
    // methods specific to Hyperbola2D

    /**
     * transform a point in local coordinate (ie orthogonal centered hyberbola
     * with a=b=1) to global coordinate system.
     */
    public Point2D toGlobal(Point2D point) {
        point = point.transform(AffineTransform2D.createScaling(a, b));
        point = point.transform(AffineTransform2D.createRotation(theta));
        point = point.transform(AffineTransform2D.createTranslation(xc, yc));
        return point;
    }

    public Point2D toLocal(Point2D point) {
        point = point.transform(AffineTransform2D.createTranslation(-xc, -yc));
        point = point.transform(AffineTransform2D.createRotation(-theta));
        point = point.transform(AffineTransform2D.createScaling(1/a, 1/b));
        return point;
    }

    /**
     * Change coordinate of the line to correspond to a standard hyperbola.
     * Standard hyperbola is such that x^2-y^2=1 for every point.
     * 
     * @param point
     * @return
     */
    private LinearShape2D formatLine(LinearShape2D line) {
        line = line.transform(AffineTransform2D.createTranslation(-xc, -yc));
        line = line.transform(AffineTransform2D.createRotation(-theta));
        line = line.transform(AffineTransform2D.createScaling(1.0/a, 1.0/b));
        return line;
    }

    // ===================================================================
    // methods inherited from Conic2D interface

    public double getAngle() {
        return theta;
    }

    public double[] getConicCoefficients() {
        // scaling coefficients
        double aSq = this.a*this.a;
        double bSq = this.b*this.b;
        double aSqInv = 1.0/aSq;
        double bSqInv = 1.0/bSq;

        // angle of hyperbola with horizontal, and trigonometric formulas
        double sint = Math.sin(this.theta);
        double cost = Math.cos(this.theta);
        double sin2t = 2.0*sint*cost;
        double sintSq = sint*sint;
        double costSq = cost*cost;

        // coefs from hyperbola center
        double xcSq = xc*xc;
        double ycSq = yc*yc;

        /*
         * Compute the coefficients. These formulae are the transformations on
         * the unit hyperbola written out long hand
         */

        double a = costSq/aSq-sintSq/bSq;
        double b = (bSq+aSq)*sin2t/(aSq*bSq);
        double c = sintSq/aSq-costSq/bSq;
        double d = -yc*b-2*xc*a;
        double e = -xc*b-2*yc*c;
        double f = -1.0+(xcSq+ycSq)*(aSqInv-bSqInv)/2.0+(costSq-sintSq)
                *(xcSq-ycSq)*(aSqInv+bSqInv)/2.0+xc*yc*(aSqInv+bSqInv)*sin2t;
        // Equivalent to:
        // double f = (xcSq*costSq + xc*yc*sin2t + ycSq*sintSq)*aSqInv
        // - (xcSq*sintSq - xc*yc*sin2t + ycSq*costSq)*bSqInv - 1;

        // Return array of results
        return new double[] { a, b, c, d, e, f };
    }

    public Point2D getCenter() {
        return new Point2D(xc, yc);
    }

    public Conic2D.Type getConicType() {
        return Conic2D.Type.HYPERBOLA;
    }

    public double getEccentricity() {
        return Math.hypot(1, b*b/a/a);
    }

    public Point2D getFocus1() {
        double c = Math.hypot(a, b);
        return new Point2D(c*Math.cos(theta)+xc, c*Math.sin(theta)+yc);
    }

    public Point2D getFocus2() {
        double c = Math.hypot(a, b);
        return new Point2D(-c*Math.cos(theta)+xc, -c*Math.sin(theta)+yc);
    }

    /** Return a */
    public double getLength1() {
        return a;
    }

    /** Return b */
    public double getLength2() {
        return b;
    }

    public Vector2D getVector1() {
        return new Vector2D(Math.cos(theta), Math.sin(theta));
    }

    public Vector2D getVector2() {
        return new Vector2D(-Math.sin(theta), Math.cos(theta));
    }

    public boolean isDirect() {
        return direct;
    }

    @Override
    public boolean contains(java.awt.geom.Point2D point) {
        return this.contains(point.getX(), point.getY());
    }

    @Override
    public boolean contains(double x, double y) {
        Point2D point = toLocal(new Point2D(x, y));
        double xa = point.getX()/a;
        double yb = point.getY()/b;
        double res = xa*xa-yb*yb-1;
        // double res = x*x*b*b - y*y*a*a - a*a*b*b;
        return Math.abs(res)<1e-6;
    }

    @Override
    public Hyperbola2D getReverseCurve() {
        return new Hyperbola2D(this.xc, this.yc, this.a, this.b, this.theta,
                !this.direct);
    }

    @Override
    public Collection<Point2D> getIntersections(LinearShape2D line) {

        Collection<Point2D> points = new ArrayList<Point2D>();

        // format to 'standard' hyperbola
        LinearShape2D line2 = formatLine(line);

        // Extract formatted line parameters
        Point2D origin = line2.getOrigin();
        double dx = line2.getVector().getX();
        double dy = line2.getVector().getY();

        // extract line parameters
        // different strategy depending if line is more horizontal or more
        // vertical
        if (Math.abs(dx)>Math.abs(dy)) {
            // Line is mainly horizontal

            // slope and intercept of the line: y(x) = k*x + yi
            double k = dy/dx;
            double yi = origin.getY()-k*origin.getX();

            // compute coefficients of second order equation
            double a = 1-k*k;
            double b = -2*k*yi;
            double c = -yi*yi-1;

            double delta = b*b-4*a*c;
            if (delta<=0) {
                System.out.println(
                        "Intersection with horizontal line should alays give positive delta");
                return points;
            }

            // x coordinate of intersection points
            double x1 = (-b-Math.sqrt(delta))/(2*a);
            double x2 = (-b+Math.sqrt(delta))/(2*a);

            // support line of formatted line
            StraightLine2D support = line2.getSupportingLine();

            // check first point is on the line
            double pos1 = support.project(new Point2D(x1, k*x1+yi));
            if (line2.contains(support.getPoint(pos1)))
                points.add(line.getPoint(pos1));

            // check second point is on the line
            double pos2 = support.project(new Point2D(x2, k*x2+yi));
            if (line2.contains(support.getPoint(pos2)))
                points.add(line.getPoint(pos2));

        } else {
            // Line is mainly vertical

            // slope and intercept of the line: x(y) = k*y + xi
            double k = dx/dy;
            double xi = origin.getX()-k*origin.getY();

            // compute coefficients of second order equation
            double a = k*k-1;
            double b = 2*k*xi;
            double c = xi*xi-1;

            double delta = b*b-4*a*c;
            if (delta<=0) {
                // No intersection with the hyperbola
                return points;
            }

            // x coordinate of intersection points
            double y1 = (-b-Math.sqrt(delta))/(2*a);
            double y2 = (-b+Math.sqrt(delta))/(2*a);

            // support line of formatted line
            StraightLine2D support = line2.getSupportingLine();

            // check first point is on the line
            double pos1 = support.project(new Point2D(k*y1+xi, y1));
            if (line2.contains(support.getPoint(pos1)))
                points.add(line.getPoint(pos1));

            // check second point is on the line
            double pos2 = support.project(new Point2D(k*y2+xi, y2));
            if (line2.contains(support.getPoint(pos2)))
                points.add(line.getPoint(pos2));
        }

        return points;
    }

    /**
     * Transforms this Hyperbola by an affine transform.
     */
    @Override
    public Hyperbola2D transform(AffineTransform2D trans) {
        Hyperbola2D result = Hyperbola2D.transformCentered(this, trans);
        Point2D center = this.getCenter().transform(trans);
        result.xc = center.getX();
        result.yc = center.getY();
        result.direct = this.direct^!trans.isDirect();
        return result;
    }

    /** Throws an UnboundedShapeException */
    @Override
    public void draw(Graphics2D g) {
        throw new UnboundedShapeException();
    }

    /**
     * Tests whether this hyperbola equals another object.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Hyperbola2D))
            return false;

        Hyperbola2D that = (Hyperbola2D) obj;

        double eps = 1e-6;
        if (Math.abs(that.xc-this.xc)>eps)
            return false;
        if (Math.abs(that.yc-this.yc)>eps)
            return false;
        if (Math.abs(that.a-this.a)>eps)
            return false;
        if (Math.abs(that.b-this.b)>eps)
            return false;
        if (Math.abs(that.theta-this.theta)>eps)
            return false;
        if (this.direct!=that.direct)
            return false;

        return true;
    }

    @Override
    public Hyperbola2D clone() {
        return new Hyperbola2D(xc, yc, a, b, theta, direct);
    }
}