/**
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
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.domain.BoundarySet2D;
import math.geom2d.domain.ContinuousBoundary2D;
import math.geom2d.domain.ContinuousOrientedCurve2D;
import math.geom2d.domain.Domain2D;
import math.geom2d.line.StraightLine2D;

/**
 * Generic class providing utilities for manipulating conics. Provides in
 * particular methods for reducing a conic.
 * 
 * @author dlegland
 */
public class Conic2DUtils {

    public final static Conic2D reduceConic(double[] coefs) {
        if (coefs.length<6) {
            System.err
                    .println("Conic2DUtils.reduceConic: must provide 6 coefficients");
            return null;
        }
        boolean debug = false;

        // precision for tests
        double eps = Shape2D.ACCURACY;

        // Extract coefficients
        double a = coefs[0];
        double b = coefs[1];
        double c = coefs[2];
        double d = coefs[3];
        double e = coefs[4];
        double f = coefs[5];

        // Transform the generic conic into a conic symmetric with respect to
        // one of the basis axes.
        // This results in fixing the coefficient b to 0.

        // coefficients of transformed conic;
        double a1, b1, c1, d1, e1, f1;

        double theta0 = 0;
        // Check if b is zero
        if (Math.abs(b)<eps) {
            // Simply keep the same coefficients
            a1 = a;
            b1 = b;
            c1 = c;
            d1 = d;
            e1 = e;
            f1 = f;
            theta0 = 0;
        } else {
            // determine rotation angle (between 0 and PI/2).
            if (Math.abs(a-c)<eps)
                theta0 = Math.PI/4; // conic symmetric wrt diagonal
            else
                theta0 = Angle2D.formatAngle(Math.atan2(b, a-c)/2);

            // computation shortcuts
            double cot = Math.cos(theta0);
            double sit = Math.sin(theta0);
            double co2t = Math.cos(2*theta0);
            double si2t = Math.sin(2*theta0);
            double cot2 = cot*cot;
            double sit2 = sit*sit;

            // Compute coefficients of the conic rotated around origin
            a1 = a*cot2+b*sit*cot+c*sit2;
            b1 = si2t*(c-a)+b*co2t; // should be equal to zero
            c1 = a*sit2-b*sit*cot+c*cot2;
            d1 = d*cot+e*sit;
            e1 = -d*sit+e*cot;
            f1 = f;
        }

        // small control on the value of b1
        if (Math.abs(b1)>eps) {
            System.err
                    .println("Conic2DUtils.reduceConic: conic was not correctly transformed");
            return null;
        }

        // Test degenerate cases
        if (Math.abs(a)<eps&&Math.abs(c)<eps) {
            if (Math.abs(d)>eps||Math.abs(e)>eps)
                return new ConicStraightLine2D(d, e, f);
            else
                return new EmptyConic2D(coefs);
        }

        // Case of a parabola
        if (Math.abs(a1)<eps) {
            // case of a1 close to 0 -> parabola parallel to horizontal axis
            if (debug)
                System.out.println("horizontal parabola");

            // Check degenerate case d=1
            if (Math.abs(d1)<eps) {
                double delta = e1*e1-4*c1*f1;
                if (delta>=0) {
                    // find the 2 roots
                    double ys = -e1/2.0/c1;
                    double dist = Math.sqrt(delta)/2.0/c1;
                    Point2D center = new Point2D(0, ys)
                            .transform(AffineTransform2D.createRotation(theta0));
                    return new ConicTwoLines2D(center, dist, theta0);
                } else
                    return new EmptyConic2D(coefs);
            }

            // compute reduced coefficients
            double c2 = -c1/d1;
            double e2 = -e1/d1;
            double f2 = -f1/d1;

            // vertex of parabola
            double xs = -e2/c2;
            double ys = -(e2*e2-c2*f2)/c2;

            // create and return result
            return new Parabola2D(xs, ys, c2, theta0-Math.PI/2);

        } else if (Math.abs(c1)<eps) {
            // Case of c1 close to 0 -> parabola parallel to vertical axis
            if (debug)
                System.out.println("vertical parabola");

            // Check degenerate case d=1
            if (Math.abs(e1)<eps) {
                double delta = d1*d1-4*a1*f1;
                if (delta>=0) {
                    // find the 2 roots
                    double xs = -d1/2.0/a1;
                    double dist = Math.sqrt(delta)/2.0/a1;
                    Point2D center = new Point2D(0, xs)
                            .transform(AffineTransform2D.createRotation(theta0));
                    return new ConicTwoLines2D(center, dist, theta0);
                } else
                    return new EmptyConic2D(coefs);
            }

            // compute reduced coefficients
            double a2 = -a1/e1;
            double d2 = -d1/e1;
            double f2 = -f1/e1;

            // vertex of parabola
            double xs = -d2/a2;
            double ys = -(d2*d2-a2*f2)/a2;

            // create and return result
            return new Parabola2D(xs, ys, a2, theta0);
        }

        // Remaining cases: ellipse or hyperbola

        // compute coordinate of conic center
        Point2D center = new Point2D(-d1/(2*a1), -e1/(2*c1));
        center = center.transform(AffineTransform2D.createRotation(theta0));

        // length of semi axes
        double num = (c1*d1*d1+a1*e1*e1-4*a1*c1*f1)/(4*a1*c1);
        double at = num/a1;
        double bt = num/c1;

        if (at<0&&bt<0) {
            System.err.println("Conic2DUtils.reduceConic(): found A<0 and C<0");
            return null;
        }

        // Case of an ellipse
        if (at>0&&bt>0) {
            if (debug)
                System.out.println("ellipse");
            if (at>bt)
                return new Ellipse2D(center, Math.sqrt(at), Math.sqrt(bt),
                        theta0);
            else
                return new Ellipse2D(center, Math.sqrt(bt), Math.sqrt(at),
                        Angle2D.formatAngle(theta0+Math.PI/2));
        }

        // remaining case is the hyperbola

        // Case of east-west hyperbola
        if (at>0) {
            if (debug)
                System.out.println("east-west hyperbola");
            return new Hyperbola2D(center, Math.sqrt(at), Math.sqrt(-bt),
                    theta0);
        } else {
            if (debug)
                System.out.println("north-south hyperbola");
            return new Hyperbola2D(center, Math.sqrt(bt), Math.sqrt(-at),
                    theta0+Math.PI/2);
        }
    }

    /**
     * Transforms a conic centered around the origin, by dropping the
     * translation part of the transform. The array must be contains at least 3
     * elements. If it contains 3 elements, the 3 remaining elements are
     * supposed to be 0, 0, and -1 in that order.
     * 
     * @param coefs an array of double with at least 3 coefficients
     * @param trans an affine transform
     * @return an array of double with as many elements as the input array
     */
    public final static double[] transformCentered(double[] coefs,
            AffineTransform2D trans) {
        // Extract transform coefficients
        double[][] mat = trans.getAffineMatrix();
        double a = mat[0][0];
        double b = mat[1][0];
        double c = mat[0][1];
        double d = mat[1][1];

        // Extract first conic coefficients
        double A = coefs[0];
        double B = coefs[1];
        double C = coefs[2];

        // compute matrix determinant
        double delta = a*d-b*c;
        delta = delta*delta;

        double A2 = (A*d*d+C*b*b-B*b*d)/delta;
        double B2 = (B*(a*d+b*c)-2*(A*c*d+C*a*b))/delta;
        double C2 = (A*c*c+C*a*a-B*a*c)/delta;

        // return only 3 parameters if needed
        if (coefs.length==3)
            return new double[] { A2, B2, C2 };

        // Compute other coefficients
        double D = coefs[3];
        double E = coefs[4];
        double F = coefs[5];
        double D2 = D*d-E*b;
        double E2 = E*a-D*c;
        return new double[] { A2, B2, C2, D2, E2, F };
    }

    /**
     * Transforms a conic by an affine transform.
     * 
     * @param coefs an array of double with 6 coefficients
     * @param trans an affine transform
     * @return the coefficients of the transformed conic
     */
    public final static double[] transform(double[] coefs,
            AffineTransform2D trans) {
        // Extract coefficients of the inverse transform
        double[][] mat = trans.invert().getAffineMatrix();
        double a = mat[0][0];
        double b = mat[1][0];
        double c = mat[0][1];
        double d = mat[1][1];
        double e = mat[0][2];
        double f = mat[1][2];

        // Extract conic coefficients
        double A = coefs[0];
        double B = coefs[1];
        double C = coefs[2];
        double D = coefs[3];
        double E = coefs[4];
        double F = coefs[5];

        // Compute coefficients of the transformed conic
        double A2 = A*a*a+B*a*b+C*b*b;
        double B2 = 2*(A*a*c+C*b*d)+B*(a*d+b*c);
        double C2 = A*c*c+B*c*d+C*d*d;
        double D2 = 2*(A*a*e+C*b*f)+B*(a*f+b*e)+D*a+E*b;
        double E2 = 2*(A*c*e+C*d*f)+B*(c*f+d*e)+D*c+E*d;
        double F2 = A*e*e+B*e*f+C*f*f+D*e+E*f+F;

        // Return the array of coefficients
        return new double[] { A2, B2, C2, D2, E2, F2 };
    }

    // -----------------------------------------------------------------
    // Some special conics

    static class ConicStraightLine2D extends StraightLine2D implements Conic2D {

        double[] coefs = new double[] { 0, 0, 0, 1, 0, 0 };

        public ConicStraightLine2D(StraightLine2D line) {
            super(line);
            coefs = new double[] { 0, 0, 0, dy, -dx, dx*y0-dy*x0 };
        }

        public ConicStraightLine2D(double a, double b, double c) {
            super(StraightLine2D.createCartesian(a, b, c));
            coefs = new double[] { 0, 0, 0, a, b, c };
        }

        public double[] getConicCoefficients() {
            return coefs;
        }

        public Type getConicType() {
            return Conic2D.Type.STRAIGHT_LINE;
        }

        /** Return NaN. */
        public double getEccentricity() {
            return Double.NaN;
        }

        @Override
        public ConicStraightLine2D getReverseCurve() {
            return new ConicStraightLine2D(super.getReverseCurve());
        }

        @Override
        public ConicStraightLine2D transform(AffineTransform2D trans) {
            return new ConicStraightLine2D(super.transform(trans));
        }
    }

    /**
     * @deprecated empty shapes are represented by null value, reducing the
     *      total number of classes
     */
    @Deprecated
    static class EmptyConic2D extends Curve2D.EmptyCurve2D implements Conic2D {

        double[] coefs;

        public EmptyConic2D(double[] coefs) {
        }

        public EmptyConic2D() {
            this(new double[] { 0, 0, 0, 0, 0, 1 });
        }

        public double[] getCartesianEquation() {
            return getConicCoefficients();
        }

        public double[] getConicCoefficients() {
            return coefs;
        }

        public Type getConicType() {
            return Conic2D.Type.NOT_A_CONIC;
        }

        public double getEccentricity() {
            return Double.NaN;
        }

        public double getSignedDistance(java.awt.geom.Point2D point) {
            return Double.NaN;
        }

        public double getSignedDistance(double x, double y) {
            return Double.NaN;
        }

        public double getWindingAngle(java.awt.geom.Point2D point) {
            return Double.NaN;
        }

        public boolean isInside(java.awt.geom.Point2D pt) {
            return false;
        }

        @Override
        public CurveSet2D<? extends ContinuousOrientedCurve2D> clip(Box2D box) {
            return null;
        }

        @Override
        public Conic2D getReverseCurve() {
            return this;
        }

        @Override
        public Conic2D transform(AffineTransform2D trans) {
            return this;
        }

        public void fill(Graphics2D g2) {
        }

        public Collection<ContinuousBoundary2D> getBoundaryCurves() {
            return new ArrayList<ContinuousBoundary2D>(0);
        }

        public Domain2D getDomain() {
            return null;
        }
    }

    static class ConicTwoLines2D extends BoundarySet2D<StraightLine2D>
            implements Conic2D {

        double xc = 0, yc = 0, d = 1, theta = 0;

        public ConicTwoLines2D(Point2D point, double d, double theta) {
            this(point.x, point.y, d, theta);
        }

        public ConicTwoLines2D(double xc, double yc, double d, double theta) {
            super();

            this.xc = xc;
            this.yc = yc;
            this.d = d;
            this.theta = theta;

            StraightLine2D baseLine = StraightLine2D.create(
                    new Point2D(xc, yc), theta);
            this.addCurve(baseLine.getParallel(d));
            this.addCurve(baseLine.getParallel(-d).getReverseCurve());
        }

        public double[] getConicCoefficients() {
            double[] coefs = { 0, 0, 1, 0, 0, -1 };
            AffineTransform2D sca = AffineTransform2D.createScaling(0, d), rot = AffineTransform2D
                    .createRotation(theta), tra = AffineTransform2D
                    .createTranslation(xc, yc);
            // AffineTransform2D trans = tra.compose(rot).compose(sca);
            AffineTransform2D trans = sca.chain(rot).chain(tra);
            return Conic2DUtils.transform(coefs, trans);
        }

        public Type getConicType() {
            return Conic2D.Type.TWO_LINES;
        }

        public double getEccentricity() {
            return Double.NaN;
        }

        @Override
        public ConicTwoLines2D transform(AffineTransform2D trans) {
            Point2D center = new Point2D(xc, yc).transform(trans);
            StraightLine2D line = this.getFirstCurve().transform(trans);

            return new ConicTwoLines2D(center, line.getDistance(center), line
                    .getHorizontalAngle());
        }

        @Override
        public ConicTwoLines2D getReverseCurve() {
            return new ConicTwoLines2D(xc, yc, -d, theta);
        }
    }

    // TODO: add CrossConic2D
}
