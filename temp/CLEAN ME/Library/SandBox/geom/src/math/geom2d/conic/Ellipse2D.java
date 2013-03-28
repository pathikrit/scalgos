/* File Ellipse2D.java 
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
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Vector2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.domain.ContinuousBoundary2D;
import math.geom2d.domain.Domain2D;
import math.geom2d.domain.GenericDomain2D;
import math.geom2d.domain.SmoothOrientedCurve2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.polygon.Polyline2D;
import math.geom2d.polygon.Ring2D;

// Imports

/**
 * An ellipse in the plane. It is defined by the center, the orientation angle,
 * and the lengths of the two axis. No convention is taken about lengths of
 * semiaxis : the second semi axis can be greater than the first one.
 */
public class Ellipse2D implements SmoothOrientedCurve2D, Conic2D,
        ContinuousBoundary2D, Cloneable {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    /** coordinate of center. */
    protected double  xc;
    protected double  yc;

    /** length of major semi-axis */
    protected double  r1;
    
    /** length of minor semi-axis */
    protected double  r2;

    /** orientation of major semi-axis */
    protected double  theta  = 0;

    /** directed ellipse or not */
    protected boolean direct = true;

    /**
     * Create a new Ellipse by specifying the two focii, and the length of the
     * chord. The chord equals the sum of distances between a point of the
     * ellipse and each focus.
     * 
     * @param focus1 the first focus
     * @param focus2 the second focus
     * @param chord the sum of distances to focii
     * @return a new instance of Ellipse2D
     */
    public final static Ellipse2D create(Point2D focus1, Point2D focus2,
            double chord) {
        double x1 = focus1.getX();
        double y1 = focus1.getY();
        double x2 = focus2.getX();
        double y2 = focus2.getY();

        double xc = (x1+x2)/2;
        double yc = (y1+y2)/2;
        double theta = Angle2D.getHorizontalAngle(x1, y1, x2, y2);

        double dist = focus1.getDistance(focus2);
        if (dist<Shape2D.ACCURACY)
            return new Circle2D(xc, yc, chord/2);

        double r1 = chord/2;
        double r2 = Math.sqrt(chord*chord-dist*dist)/2;

        return new Ellipse2D(xc, yc, r1, r2, theta);
    }

    /**
     * Creates a new Ellipse by reducing the conic coefficients, assuming conic
     * type is ellipse, and ellipse is centered.
     * 
     * @param coefs an array of double with at least 3 coefficients containing
     *            coefficients for x^2, xy, and y^2 factors.
     * @return the Ellipse2D corresponding to given coefficients
     */
    public final static Ellipse2D reduceCentered(double[] coefs) {
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

        // extract coefficients f if present
        double f = 1;
        if (coefs2.length>5)
            f = Math.abs(coefs[5]);

        assert Math.abs(coefs2[0]/f)<Shape2D.ACCURACY : "Second conic coefficient should be zero";

        // extract major and minor axis lengths, ensuring r1 is greater
        double r1, r2;
        if (coefs2[0]<coefs2[2]) {
            r1 = Math.sqrt(f/coefs2[0]);
            r2 = Math.sqrt(f/coefs2[2]);
        } else {
            r1 = Math.sqrt(f/coefs2[2]);
            r2 = Math.sqrt(f/coefs2[0]);
            theta = Angle2D.formatAngle(theta+Math.PI/2);
            theta = Math.min(theta, Angle2D.formatAngle(theta+Math.PI));
        }

        // Return either an ellipse or a circle
        if (Math.abs(r1-r2)<Shape2D.ACCURACY)
            return new Circle2D(0, 0, r1);
        else
            return new Ellipse2D(0, 0, r1, r2, theta);
    }

    /**
     * Transform an ellipse, by supposing both the ellipse is centered and the
     * transform has no translation part.
     * 
     * @param ellipse an ellipse
     * @param trans an affine transform
     * @return the transformed ellipse, centered around origin
     */
    public final static Ellipse2D transformCentered(Ellipse2D ellipse,
            AffineTransform2D trans) {
        // Extract inner parameter of ellipse
        double r1 = ellipse.r1;
        double r2 = ellipse.r2;
        double theta = ellipse.theta;

        // precompute some parts
        double r1Sq = r1*r1;
        double r2Sq = r2*r2;
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        double cotSq = cot*cot;
        double sitSq = sit*sit;

        // compute coefficients of the centered conis
        double A = cotSq/r1Sq+sitSq/r2Sq;
        double B = 2*cot*sit*(1/r1Sq-1/r2Sq);
        double C = cotSq/r2Sq+sitSq/r1Sq;
        double[] coefs = new double[] { A, B, C };

        // Compute coefficients of the transformed conic
        double[] coefs2 = Conic2DUtils.transformCentered(coefs, trans);

        // reduce conic coefficients to Ellipse
        return Ellipse2D.reduceCentered(coefs2);
    }

    // ===================================================================
    // constructors

    /**
     * Empty constructor, define ellipse centered at origin with both major and
     * minor semi-axis with length equal to 1.
     */
    public Ellipse2D() {
        this(0, 0, 1, 1, 0, true);
    }

    /** Main constructor: define center by a point plus major and minor smei axis */
    public Ellipse2D(Point2D center, double l1, double l2) {
        this(center.getX(), center.getY(), l1, l2, 0, true);
    }

    /** Define center by coordinate, plus major and minor semi axis */
    public Ellipse2D(double xc, double yc, double l1, double l2) {
        this(xc, yc, l1, l2, 0, true);
    }

    /**
     * Define center by point, major and minor semi axis lengths, and
     * orientation angle.
     */
    public Ellipse2D(Point2D center, double l1, double l2, double theta) {
        this(center.getX(), center.getY(), l1, l2, theta, true);
    }

    /**
     * Define center by coordinate, major and minor semi axis lengths, and
     * orientation angle.
     */
    public Ellipse2D(double xc, double yc, double l1, double l2, double theta) {
        this(xc, yc, l1, l2, theta, true);
    }

    /**
     * Define center by coordinate, major and minor semi axis lengths,
     * orientation angle, and boolean flag for directed ellipse.
     */
    public Ellipse2D(double xc, double yc, double l1, double l2, double theta,
            boolean direct) {
        this.xc = xc;
        this.yc = yc;

        r1 = l1;
        r2 = l2;

        this.theta = theta;
        this.direct = direct;
    }

    /**
     * construct an ellipse from the java.awt.geom class for ellipse.
     */
    public Ellipse2D(java.awt.geom.Ellipse2D ellipse) {
        this(new Point2D(ellipse.getCenterX(), ellipse.getCenterY()), ellipse
                .getWidth()/2, ellipse.getHeight()/2);
    }

    // ===================================================================
    // Methods specific to Ellipse2D

    /**
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setEllipse(double xc, double yc, double r1, double r2,
            double theta) {
        this.setEllipse(xc, yc, r1, r2, theta, true);
    }

    /**
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setEllipse(double xc, double yc, double r1, double r2,
            double theta, boolean direct) {
        this.xc = xc;
        this.yc = yc;
        this.r1 = r1;
        this.r2 = r2;
        this.theta = theta;
        this.direct = direct;
    }

    /**
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setEllipse(Point2D center, double r1, double r2, double theta) {
        this.setEllipse(center.getX(), center.getY(), r1, r2, theta, true);
    }

    /**
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setEllipse(Point2D center, double r1, double r2, double theta,
            boolean direct) {
        this.setEllipse(center.getX(), center.getY(), r1, r2, theta, direct);
    }

    /**
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setCenter(Point2D center) {
        this.setCenter(center.getX(), center.getY());
    }

    /**
     * @deprecated conics will become imutable in a future release
     */
    @Deprecated
    public void setCenter(double x, double y) {
        this.xc = x;
        this.yc = y;
    }

    /**
     * Return the RHO parameter, in a polar representation of the ellipse,
     * centered at the center of ellipse.
     * 
     * @param angle : angle from horizontal
     * @return distance of ellipse from ellipse center in direction theta
     */
    public double getRho(double angle) {
        double cot = Math.cos(angle-theta);
        double sit = Math.cos(angle-theta);
        return Math.sqrt(r1*r1*r2*r2/(r2*r2*cot*cot+r1*r1*sit*sit));
    }

    public Point2D getProjectedPoint(java.awt.geom.Point2D point) {
        Vector2D polar = this.getProjectedVector(point, Shape2D.ACCURACY);
        return new Point2D(point.getX()+polar.getX(), point.getY()+polar.getY());
    }

    /**
     * Compute projection of a point onto an ellipse. Return the polar vector
     * representing the translation from point <code>point</point> to its
     * projection on the ellipse, with the direction parallel to the local 
     * normal to the ellipse. The parameter <code>rho</code> of the
     * PolarVector2D is positive if point lies 
     * Refs : <p>
     * http://www.spaceroots.org/documents/distance/distance-to-ellipse.pdf, 
     * http://www.spaceroots.org/downloads.html
     * @param point
     * @param eMax
     * @return the projection vector
     */
    public Vector2D getProjectedVector(java.awt.geom.Point2D point, double eMax) {

        double ot = 1.0/3.0;

        // center the ellipse
        double x = point.getX()-xc;
        double y = point.getY()-yc;

        double la, lb, theta;
        if (r1>=r2) {
            la = r1;
            lb = r2;
            theta = this.theta;
        } else {
            la = r2;
            lb = r1;
            theta = this.theta+Math.PI/2;
            double tmp = x;
            x = -y;
            y = tmp;
        }

        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        double tmpx = x, tmpy = y;
        x = tmpx*cot-tmpy*sit;
        y = tmpx*sit+tmpy*cot;

        double ae = la;
        double f = 1-lb/la;
        double e2 = f*(2.0-f);
        double g = 1.0-f;
        double g2 = g*g;
        // double e2ae = e2 * ae;
        double ae2 = ae*ae;

        // compute some miscellaneous variables outside of the loop
        double z = y;
        double z2 = y*y;
        double r = x;
        double r2 = x*x;
        double g2r2ma2 = g2*(r2-ae2);
        // double g2r2ma2mz2 = g2r2ma2 - z2;
        double g2r2ma2pz2 = g2r2ma2+z2;
        double dist = Math.sqrt(r2+z2);
        // double threshold = Math.max(1.0e-14 * dist, eMax);
        boolean inside = (g2r2ma2pz2<=0);

        // point at the center
        if (dist<(1.0e-10*ae)) {
            System.out.println("point at the center");
            return Vector2D.createPolar(r, 0);
        }

        double cz = r/dist;
        double sz = z/dist;
        double t = z/(dist+r);

        // distance to the ellipse along the current line
        // as the smallest root of a 2nd degree polynom :
        // a k^2 - 2 b k + c = 0
        double a = 1.0-e2*cz*cz;
        double b = g2*r*cz+z*sz;
        double c = g2r2ma2pz2;
        double b2 = b*b;
        double ac = a*c;
        double k = c/(b+Math.sqrt(b2-ac));
        // double lambda = Math.atan2(cart.y, cart.x);
        double phi = Math.atan2(z-k*sz, g2*(r-k*cz));

        // point on the ellipse
        if (Math.abs(k)<(1.0e-10*dist)) {
            // return new Ellipsoidic(lambda, phi, k);
            return Vector2D.createPolar(k, phi);
        }

        for (int iterations = 0; iterations<100; ++iterations) {

            // 4th degree normalized polynom describing
            // circle/ellipse intersections
            // tau^4 + b tau^3 + c tau^2 + d tau + e = 0
            // (there is no need to compute e here)
            a = g2r2ma2pz2+g2*(2.0*r+k)*k;
            b = -4.0*k*z/a;
            c = 2.0*(g2r2ma2pz2+(1.0+e2)*k*k)/a;
            double d = b;

            // reduce the polynom to degree 3 by removing
            // the already known real root
            // tau^3 + b tau^2 + c tau + d = 0
            b += t;
            c += t*b;
            d += t*c;

            // find the other real root
            b2 = b*b;
            double Q = (3.0*c-b2)/9.0;
            double R = (b*(9.0*c-2.0*b2)-27.0*d)/54.0;
            double D = Q*Q*Q+R*R;
            double tildeT, tildePhi;
            if (D>=0) {
                double rootD = Math.sqrt(D);
                double rMr = R-rootD;
                double rPr = R+rootD;
                tildeT = ((rPr>0) ? Math.pow(rPr, ot) : -Math.pow(-rPr, ot))
                        +((rMr>0) ? Math.pow(rMr, ot) : -Math.pow(-rMr, ot))-b
                        *ot;
                double tildeT2 = tildeT*tildeT;
                double tildeT2P1 = 1.0+tildeT2;
                tildePhi = Math.atan2(z*tildeT2P1-2*k*tildeT, g2
                        *(r*tildeT2P1-k*(1.0-tildeT2)));
            } else {
                Q = -Q;
                double qRoot = Math.sqrt(Q);
                double alpha = Math.acos(R/(Q*qRoot));
                tildeT = 2*qRoot*Math.cos(alpha*ot)-b*ot;
                double tildeT2 = tildeT*tildeT;
                double tildeT2P1 = 1.0+tildeT2;
                tildePhi = Math.atan2(z*tildeT2P1-2*k*tildeT, g2
                        *(r*tildeT2P1-k*(1.0-tildeT2)));
                if ((tildePhi*phi)<0) {
                    tildeT = 2*qRoot*Math.cos((alpha+2*Math.PI)*ot)-b*ot;
                    tildeT2 = tildeT*tildeT;
                    tildeT2P1 = 1.0+tildeT2;
                    tildePhi = Math.atan2(z*tildeT2P1-2*k*tildeT, g2
                            *(r*tildeT2P1-k*(1.0-tildeT2)));
                    if (tildePhi*phi<0) {
                        tildeT = 2*qRoot*Math.cos((alpha+4*Math.PI)*ot)-b*ot;
                        tildeT2 = tildeT*tildeT;
                        tildeT2P1 = 1.0+tildeT2;
                        tildePhi = Math.atan2(z*tildeT2P1-2*k*tildeT, g2
                                *(r*tildeT2P1-k*(1.0-tildeT2)));
                    }
                }
            }

            // midpoint on the ellipse
            double dPhi = Math.abs(0.5*(tildePhi-phi));
            phi = 0.5*(phi+tildePhi);
            double cPhi = Math.cos(phi);
            double sPhi = Math.sin(phi);
            double coeff = Math.sqrt(1.0-e2*sPhi*sPhi);

            // Eventually display result of iterations
            if (false)
                System.out.println(iterations+": phi = "+Math.toDegrees(phi)
                        +" +/- "+Math.toDegrees(dPhi)+", k = "+k);

            b = ae/coeff;
            double dR = r-cPhi*b;
            double dZ = z-sPhi*b*g2;
            k = Math.sqrt(dR*dR+dZ*dZ);
            if (inside) {
                k = -k;
            }
            t = dZ/(k+dR);

            if (dPhi<1.0e-14) {
                if (this.r1>=this.r2)
                    return Vector2D.createPolar(-k, phi+theta);
                // -(r * cPhi + z * sPhi - ae * coeff), phi+theta);
                else
                    return Vector2D.createPolar(-k, phi+theta-Math.PI/2);
                // -(r * cPhi + z * sPhi - ae * coeff),
                // phi+theta-Math.PI/2);
            }
        }

        return null;
    }

    /**
     * Return the parallel ellipse located at a distance d from this ellipse.
     * For direct ellipse, distance is positive outside of the ellipse, and
     * negative inside
     */
    public Ellipse2D getParallel(double d) {
        return new Ellipse2D(xc, yc, Math.abs(r1+d), Math.abs(r2+d), theta,
                direct);
    }

    /**
     * return true if ellipse has a direct orientation.
     */
    public boolean isDirect() {
        return direct;
    }

    public boolean isCircle() {
        return Math.abs(r1-r2)<Shape2D.ACCURACY;
    }

    // ===================================================================
    // methods of Conic2D

    public Conic2D.Type getConicType() {
        if (Math.abs(r1-r2)<Shape2D.ACCURACY)
            return Conic2D.Type.CIRCLE;
        else
            return Conic2D.Type.ELLIPSE;
    }

    /**
     * Returns the conic coefficients of the ellipse. Algorithm taken from
     * http://tog.acm.org/GraphicsGems/gemsv/ch2-6/conmat.c
     */
    public double[] getConicCoefficients() {

        /* common coefficients */
        double r1Sq = this.r1*this.r1;
        double r2Sq = this.r2*this.r2;

        // angle of ellipse, and trigonometric formulas
        double sint = Math.sin(this.theta);
        double cost = Math.cos(this.theta);
        double sin2t = 2.0*sint*cost;
        double sintSq = sint*sint;
        double costSq = cost*cost;

        // coefs from ellipse center
        double xcSq = xc*xc;
        double ycSq = yc*yc;
        double r1SqInv = 1.0/r1Sq;
        double r2SqInv = 1.0/r2Sq;

        /*
         * Compute the coefficients. These formulae are the transformations on
         * the unit circle written out long hand
         */

        double a = costSq/r1Sq+sintSq/r2Sq;
        double b = (r2Sq-r1Sq)*sin2t/(r1Sq*r2Sq);
        double c = costSq/r2Sq+sintSq/r1Sq;
        double d = -yc*b-2*xc*a;
        double e = -xc*b-2*yc*c;
        double f = -1.0+(xcSq+ycSq)*(r1SqInv+r2SqInv)/2.0+(costSq-sintSq)
                *(xcSq-ycSq)*(r1SqInv-r2SqInv)/2.0+xc*yc*(r1SqInv-r2SqInv)
                *sin2t;

        // Return array of results
        return new double[] { a, b, c, d, e, f };
    }

    /**
     * Returns the length of the major semi-axis of the ellipse.
     */
    public double getSemiMajorAxisLength() {
        return r1;
    }

    /**
     * Returns the length of the minor semi-axis of the ellipse.
     */
    public double getSemiMinorAxisLength() {
        return r2;
    }

    /**
     * Computes eccentricity of ellipse, depending on the lengths of the
     * semi-axes. Eccentricity is 0 for a circle (r1==r2), and tends to 1 when
     * ellipse elongates.
     */
    public double getEccentricity() {
        double a = Math.max(r1, r2);
        double b = Math.min(r1, r2);
        double r = b/a;
        return Math.sqrt(1-r*r);
    }

    /**
     * Returns center of the ellipse.
     */
    public Point2D getCenter() {
        return new Point2D(xc, yc);
    }

    /**
     * Return the first focus. It is defined as the first focus on the Major
     * axis, in the direction given by angle theta.
     */
    public Point2D getFocus1() {
        double a, b, theta;
        if (r1>r2) {
            a = r1;
            b = r2;
            theta = this.theta;
        } else {
            a = r2;
            b = r1;
            theta = this.theta+Math.PI/2;
        }
        return Point2D.createPolar(xc, yc, Math.sqrt(a*a-b*b), theta+Math.PI);
    }

    /**
     * Returns the second focus. It is defined as the second focus on the Major
     * axis, in the direction given by angle theta.
     */
    public Point2D getFocus2() {
        double a, b, theta;
        if (r1>r2) {
            a = r1;
            b = r2;
            theta = this.theta;
        } else {
            a = r2;
            b = r1;
            theta = this.theta+Math.PI/2;
        }
        return Point2D.createPolar(xc, yc, Math.sqrt(a*a-b*b), theta);
    }

    public Vector2D getVector1() {
        return new Vector2D(Math.cos(theta), Math.sin(theta));
    }

    public Vector2D getVector2() {
        if (direct)
            return new Vector2D(-Math.sin(theta), Math.cos(theta));
        else
            return new Vector2D(Math.sin(theta), -Math.cos(theta));
    }

    /**
     * return the angle of the ellipse first axis with the Ox axis.
     */
    public double getAngle() {
        return theta;
    }

    // ===================================================================
    // methods of Boundary2D interface

    public Collection<ContinuousBoundary2D> getBoundaryCurves() {
        ArrayList<ContinuousBoundary2D> list = new ArrayList<ContinuousBoundary2D>(
                1);
        list.add(this);
        return list;
    }

    public Domain2D getDomain() {
        return new GenericDomain2D(this);
    }

    public void fill(Graphics2D g2) {
        java.awt.geom.Ellipse2D.Double ellipse = new java.awt.geom.Ellipse2D.Double(
                xc-r1, yc-r2, 2*r1, 2*r2);
        java.awt.geom.AffineTransform trans = java.awt.geom.AffineTransform
                .getRotateInstance(theta, xc, yc);
        g2.fill(trans.createTransformedShape(ellipse));
    }

    // ===================================================================
    // methods of OrientedCurve2D interface

    /**
     * Return either 0, 2*PI or -2*PI, depending whether the point is located
     * inside the interior of the ellipse or not.
     */
    public double getWindingAngle(java.awt.geom.Point2D point) {
        if (this.getSignedDistance(point)>0)
            return 0;
        else
            return direct ? Math.PI*2 : -Math.PI*2;
    }

    /**
     * Test whether the point is inside the ellipse. The test is performed by
     * rotating the ellipse and the point to align with axis, rescaling in each
     * direction, then computing distance to origin.
     */
    public boolean isInside(java.awt.geom.Point2D point) {
        AffineTransform2D rot = AffineTransform2D.createRotation(this.xc,
                this.yc, -this.theta);
        Point2D pt = rot.transform(point);
        double xp = (pt.getX()-this.xc)/this.r1;
        double yp = (pt.getY()-this.yc)/this.r2;
        return (xp*xp+yp*yp<1)^!direct;
    }

    public double getSignedDistance(java.awt.geom.Point2D point) {
        Vector2D vector = this.getProjectedVector(point, 1e-10);
        if (isInside(point))
            return -vector.getNorm();
        else
            return vector.getNorm();
    }

    public double getSignedDistance(double x, double y) {
        return getSignedDistance(new Point2D(x, y));
    }

    // ===================================================================
    // methods of SmoothCurve2D interface

    public Vector2D getTangent(double t) {
        if (!direct)
            t = -t;
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);

        if (direct)
            return new Vector2D(
                    -r1*Math.sin(t)*cot-r2*Math.cos(t)*sit,
                    -r1*Math.sin(t)*sit+r2*Math.cos(t)*cot);
        else
            return new Vector2D(
                    r1*Math.sin(t)*cot+r2*Math.cos(t)*sit,
                    r1*Math.sin(t)*sit-r2*Math.cos(t)*cot);
    }

    /**
     * returns the curvature of the ellipse.
     */
    public double getCurvature(double t) {
        if (!direct)
            t = -t;
        double cot = Math.cos(t);
        double sit = Math.sin(t);
        return r1*r2/Math.pow(r2*r2*cot*cot+r1*r1*sit*sit, 1.5);
    }

    // ===================================================================
    // methods of ContinuousCurve2D interface

    /**
     * Returns as a closed polyline with <code>n</code> line segments.
     * 
     * @param n the number of line segments
     * @return a closed polyline with <code>n</code> line segments.
     */
    public Polyline2D getAsPolyline(int n) {
        Point2D[] points = new Point2D[n];
        double t0 = this.getT0();
        double t1 = this.getT1();
        double dt = (t1-t0)/n;
        if (this.direct)
            for (int i = 0; i<n; i++)
                points[i] = this.getPoint(i*dt+t0);
        else
            for (int i = 0; i<n; i++)
                points[i] = this.getPoint(-(double) i*dt+t0);
        return new Ring2D(points);
    }

    /**
     * Returns a set of smooth curves, which contains only the ellipse.
     */
    public Collection<? extends SmoothCurve2D> getSmoothPieces() {
        ArrayList<Ellipse2D> list = new ArrayList<Ellipse2D>(1);
        list.add(this);
        return list;
    }

    /**
     * return true, as an ellipse is always closed.
     */
    public boolean isClosed() {
        return true;
    }

    // ===================================================================
    // methods of Curve2D interface

    /**
     * Returns the parameter of the first point of the ellipse, set to 0.
     */
    public double getT0() {
        return 0;
    }

    /**
     * Returns the parameter of the last point of the ellipse, set to 2*PI.
     */
    public double getT1() {
        return 2*Math.PI;
    }

    /**
     * get the position of the curve from internal parametric representation,
     * depending on the parameter t. This parameter is between the two limits 0
     * and 2*Math.PI.
     */
    public Point2D getPoint(double t) {
        if (!direct)
            t = -t;
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new Point2D(xc+r1*Math.cos(t)*cot-r2*Math.sin(t)*sit, yc+r1
                *Math.cos(t)*sit+r2*Math.sin(t)*cot);
    }

    /**
     * Get the first point of the ellipse, which is the same as the last point.
     * 
     * @return the first point of the curve
     */
    public Point2D getFirstPoint() {
        return new Point2D(xc+r1*Math.cos(theta), yc+r1*Math.sin(theta));
    }

    /**
     * Get the last point of the ellipse, which is the same as the first point.
     * 
     * @return the last point of the curve.
     */
    public Point2D getLastPoint() {
        return new Point2D(xc+r1*Math.cos(theta), yc+r1*Math.sin(theta));
    }

    public Collection<Point2D> getSingularPoints() {
        return new ArrayList<Point2D>(0);
    }

    /**
     * Always returns false, as an ellipse does not have any singular point.
     */
    public boolean isSingular(double pos) {
        return false;
    }

    public double getPosition(java.awt.geom.Point2D point) {
        double xp = point.getX();
        double yp = point.getY();

        // translate
        xp = xp-this.xc;
        yp = yp-this.yc;

        // rotate
        double xp1 = xp*Math.cos(theta)+yp*Math.sin(theta);
        double yp1 = -xp*Math.sin(theta)+yp*Math.cos(theta);
        xp = xp1;
        yp = yp1;

        // scale
        xp = xp/this.r1;
        yp = yp/this.r2;

        if (!direct)
            yp = -yp;

        // compute angle
        double angle = Angle2D.getHorizontalAngle(xp, yp);

        if (Math.abs(Math.hypot(xp, yp)-1)<Shape2D.ACCURACY)
            return angle;
        else
            return Double.NaN;
    }

    /**
     * Computes the approximate projection position of the point on the ellipse.
     * The ellipse is first converted to a unit circle, then the angular
     * position of the point is computed in the transformed basis.
     */
    public double project(java.awt.geom.Point2D point) {
        double xp = point.getX();
        double yp = point.getY();

        // translate
        xp = xp-this.xc;
        yp = yp-this.yc;

        // rotate
        double xp1 = xp*Math.cos(theta)+yp*Math.sin(theta);
        double yp1 = -xp*Math.sin(theta)+yp*Math.cos(theta);
        xp = xp1;
        yp = yp1;

        // scale
        xp = xp/this.r1;
        yp = yp/this.r2;

        // compute angle
        double angle = Angle2D.getHorizontalAngle(xp, yp);

        return angle;
    }

    /**
     * Returns the ellipse with same center and same radius, but with the other
     * orientation.
     */
    public Ellipse2D getReverseCurve() {
        return new Ellipse2D(xc, yc, r1, r2, theta, !direct);
    }

    public Collection<ContinuousCurve2D> getContinuousCurves() {
        ArrayList<ContinuousCurve2D> list = new ArrayList<ContinuousCurve2D>(1);
        list.add(this);
        return list;
    }

    /**
     * return a new EllipseArc2D.
     */
    public EllipseArc2D getSubCurve(double t0, double t1) {
        double startAngle, extent;
        if (this.direct) {
            startAngle = t0;
            extent = Angle2D.formatAngle(t1-t0);
        } else {
            extent = -Angle2D.formatAngle(t1-t0);
            startAngle = Angle2D.formatAngle(-t0);
        }
        return new EllipseArc2D(this, startAngle, extent);
    }

    // ===================================================================
    // methods of Shape2D interface

    /** Always returns true, because an ellipse is bounded. */
    public boolean isBounded() {
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    public double getDistance(java.awt.geom.Point2D point) {
        // PolarVector2D vector = this.getProjectedVector(point, 1e-10);
        // return Math.abs(vector.getRho());
        return this.getAsPolyline(128).getDistance(point);
    }

    public double getDistance(double x, double y) {
        return getDistance(new Point2D(x, y));
    }

    /**
     * Clip the ellipse by a box. The result is an instance of CurveSet2D<ContinuousOrientedCurve2D>,
     * which contains only instances of Ellipse2D or EllipseArc2D. If the
     * ellipse is not clipped, the result is an instance of CurveSet2D<ContinuousOrientedCurve2D>
     * which contains 0 curves.
     */
    public CurveSet2D<? extends SmoothOrientedCurve2D> clip(Box2D box) {
        // Clip the curve
        CurveSet2D<SmoothCurve2D> set = Curve2DUtils.clipSmoothCurve(this, box);

        // Stores the result in appropriate structure
        CurveSet2D<SmoothOrientedCurve2D> result = new CurveSet2D<SmoothOrientedCurve2D>();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof EllipseArc2D)
                result.addCurve((EllipseArc2D) curve);
            if (curve instanceof Ellipse2D)
                result.addCurve((Ellipse2D) curve);
        }
        return result;
    }

    /**
     * Return more precise bounds for the ellipse. Return an instance of Box2D.
     */
    public Box2D getBoundingBox() {
        // we consider the two parametric equations x(t) and y(t). From the
        // ellipse
        // definition, x(t)=r1*cos(t), y(t)=r2*sin(t), and the result is moved
        // (rotated with angle theta, and translated with (xc,yc) ).
        // Each equation can then be written in the form : x(t) =
        // Xm*cos(t+theta_X).
        // We compute Xm and Ym, and use it to calculate bounds.
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        double xm = Math.sqrt(r1*r1*cot*cot+r2*r2*sit*sit);
        double ym = Math.sqrt(r1*r1*sit*sit+r2*r2*cot*cot);
        return new Box2D(xc-xm, xc+xm, yc-ym, yc+ym);
    }

    /**
     * Compute intersections of the ellipse with a straight object (line, line
     * segment, ray...).
     * <p>
     * Principle of the algorithm is to transform line and ellipse such that
     * ellipse becomes a circle, then using the intersections computation from
     * circle.
     */
    public Collection<Point2D> getIntersections(LinearShape2D line) {
        // Compute the transform2D which transforms ellipse into unit circle
        AffineTransform2D sca, rot, tra;
        sca = AffineTransform2D.createScaling(r1, r2);
        rot = AffineTransform2D.createRotation(theta);
        tra = AffineTransform2D.createTranslation(xc, yc);
        AffineTransform2D toUnit = sca.chain(rot).chain(tra).invert();

        // transform the line accordingly
        LinearShape2D line2 = line.transform(toUnit);

        // The list of intersections
        Collection<Point2D> points;

        // Compute intersection points with circle
        Circle2D circle = new Circle2D(0, 0, 1);
        points = circle.getIntersections(line2);
        if (points.size()==0)
            return points;

        // convert points on circle as angles
        ArrayList<Point2D> res = new ArrayList<Point2D>(points.size());
        for (Point2D point : points)
            res.add(this.getPoint(circle.getPosition(point)));

        // return the result
        return res;
    }

    /**
     * Transforms this ellipse by an affine transform. If the transformed shape
     * is a circle (ellipse with equal axis lengths), returns an instance of
     * Circle2D. The resulting ellipse is direct if this ellipse and the
     * transform are either both direct or both indirect.
     */
    public Ellipse2D transform(AffineTransform2D trans) {
        Ellipse2D result = Ellipse2D.transformCentered(this, trans);
        result.setCenter(this.getCenter().transform(trans));
        result.direct = !(this.direct^trans.isDirect());
        return result;
    }

    // ===================================================================
    // methods implementing the Shape interface

    /**
     * Return true if the point p lies on the ellipse, with precision given by
     * Shape2D.ACCURACY.
     */
    public boolean contains(java.awt.geom.Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * Return true if the point (x, y) lies on the ellipse, with precision given
     * by Shape2D.ACCURACY.
     */
    public boolean contains(double x, double y) {
        return this.getDistance(x, y)<Shape2D.ACCURACY;
    }

    /**
     * Add the path of the ellipse to the given path.
     * 
     * @param path the path to be completed
     * @return the completed path
     */
    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);

        // draw each line of the boundary
        if (direct)
            for (double t = .1; t<=2*Math.PI; t += .1)
                path.lineTo((float) (xc+r1*Math.cos(t)*cot-r2*Math.sin(t)*sit),
                        (float) (yc+r2*Math.sin(t)*cot+r1*Math.cos(t)*sit));
        else
            for (double t = .1; t<=2*Math.PI; t += .1)
                path.lineTo((float) (xc+r1*Math.cos(t)*cot+r2*Math.sin(t)*sit),
                        (float) (yc-r2*Math.sin(t)*cot+r1*Math.cos(t)*sit));

        // loop to the first/last point
        path.lineTo((float) (xc+r1*cot), (float) (yc+r1*sit));

        return path;
    }

    public void draw(Graphics2D g2) {
        java.awt.geom.Ellipse2D.Double ellipse = new java.awt.geom.Ellipse2D.Double(
                xc-r1, yc-r2, 2*r1, 2*r2);
        java.awt.geom.AffineTransform trans = java.awt.geom.AffineTransform
                .getRotateInstance(theta, xc, yc);
        g2.draw(trans.createTransformedShape(ellipse));
    }

    // ===================================================================
    // methods of Object superclass

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ellipse2D))
            return false;

        Ellipse2D ell = (Ellipse2D) obj;

        if (!ell.getCenter().equals(this.getCenter()))
            return false;
        if (Math.abs(ell.r1-this.r1)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(ell.r2-this.r2)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(Angle2D.formatAngle(ell.getAngle()-this.getAngle()))>Shape2D.ACCURACY)
            return false;
        if (ell.isDirect()!=this.isDirect())
            return false;
        return true;
    }

    @Override
    public Ellipse2D clone() {
        return new Ellipse2D(xc, yc, r1, r2, theta, direct);
    }
    
    @Override
    public String toString() {
        return String.format(
                "%f %f %f %f %f", xc, yc, r1, r2, Math.toDegrees(theta));
    }

    // /**
    // * A class to compute shortest distance of a point to an ellipse.
    // * @author dlegland
    // */
    // private class Ellipsoidic {
    // /** angle of the line joining current point to ref point.*/
    // public final double lambda;
    //		
    // /** normal angle of ellipse at the cuurent point */
    // public final double phi;
    //		
    // /** shortest signed distance of the point to the ellipse
    // * (negative if inside ellipse). */
    // public final double h;
    //		
    // public Ellipsoidic (double lambda, double phi, double h) {
    // this.lambda = lambda;
    // this.phi = phi;
    // this.h = h;
    // }
    // }
}