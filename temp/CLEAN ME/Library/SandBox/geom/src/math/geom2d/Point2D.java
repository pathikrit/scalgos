/* File Point2D.java 
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

package math.geom2d;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import math.geom2d.AffineTransform2D;
import math.geom2d.point.PointShape2D;

/**
 * <p>
 * A point in the plane defined by its 2 Cartesian coordinates x and y.
 * The class provides static methods to compute distance between two points.
 * </p>
 * <p>
 * Important note: in a future release, Point2D will not extend 
 * <code>java.awt.geom.Point2D.Double<code> any more.
 * </p>
 */
public class Point2D extends java.awt.geom.Point2D.Double
implements PointShape2D, Cloneable {

    // ===================================================================
    // constants

    private static final long   serialVersionUID = 1L;

    /**
     * The point located at the infinity. This point is virtual, it is contained
     * in every infinite shape, such as straight lines, hyperbolas and
     * parabolas.
     * @deprecated it is recommended to use exceptions instead (0.7.0)
     */
    @Deprecated
    public final static Point2D INFINITY_POINT   = new Point2D(
                                                         java.lang.Double.POSITIVE_INFINITY,
                                                         java.lang.Double.POSITIVE_INFINITY);

    // ===================================================================
    // class variables

    // coordinates are inherited from java class for Point

    // ===================================================================
    // constructors

    /** construct a new Point2D at position (0,0). */
    public Point2D() {
        super(0, 0);
    }

    /** constructor with given position. */
    public Point2D(double x, double y) {
        super(x, y);
    }

    /**
     * Constructor from a java awt.geom Point2D, included for compatibility.
     */
    public Point2D(java.awt.geom.Point2D point) {
        super(point.getX(), point.getY());
    }

    /**
     * Constructor from two java awt.geom Point2D, summing their coordinates.
     * @deprecated since 0.7.0
     */
    @Deprecated
    public Point2D(java.awt.geom.Point2D point1, java.awt.geom.Point2D point2) {
        super(point1.getX()+point2.getX(), point1.getY()+point2.getY());
    }

    /**
     * Constructor from a java awt.geom Point2D, and two double. The (x,y)
     * coordinates are added to the coordinates of given point.
     * @deprecated use Point2D.createPolar() instead (0.7.0)
     */
    @Deprecated
    public Point2D(java.awt.geom.Point2D point1, double x, double y) {
        super(point1.getX()+x, point1.getY()+y);
    }

    // ===================================================================
    // static methods

    /**
     * Creates a new point from polar coordinates <code>rho</code> and
     * <code>theta</code>.
     */
    public final static Point2D createPolar(double rho, double theta) {
        return new Point2D(rho*Math.cos(theta), rho*Math.sin(theta));
    }

    /**
     * Creates a new point from polar coordinates <code>rho</code> and
     * <code>theta</code>, from the given point.
     */
    public final static Point2D createPolar(Point2D point, double rho,
            double theta) {
        return new Point2D(
                point.getX()+rho*Math.cos(theta),
                point.getY()+rho*Math.sin(theta));
    }

    /**
     * Creates a new point from polar coordinates <code>rho</code> and
     * <code>theta</code>, from the position (x0,y0).
     */
    public final static Point2D createPolar(double x0, double y0, double rho,
            double theta) {
        return new Point2D(x0+rho*Math.cos(theta), y0+rho*Math.sin(theta));
    }

    public final static double getDistance(double x1, double y1, double x2,
            double y2) {
        return Math.hypot(x2-x1, y2-y1);
    }

    /**
     * Computes the euclidean distance between two points.
     * Uses robust computation (via Math.hypot() method).
     * @param p1 the first point
     * @param p2 the second point
     * @return the euclidean distance between p1 and p2.
     */
    public final static double getDistance(java.awt.geom.Point2D p1,
            java.awt.geom.Point2D p2) {
        return Math.hypot(p1.getX()-p2.getX(), p1.getY()-p2.getY());
    }

    /**
     * Tests if the three points are colinear.
     * 
     * @return true if three points lie on the same line.
     */
    public final static boolean isColinear(java.awt.geom.Point2D p1,
            java.awt.geom.Point2D p2, java.awt.geom.Point2D p3) {
        double dx1, dx2, dy1, dy2;
        dx1 = p2.getX()-p1.getX();
        dy1 = p2.getY()-p1.getY();
        dx2 = p3.getX()-p1.getX();
        dy2 = p3.getY()-p1.getY();

        // tests if the two lines are parallel
        return Math.abs(dx1*dy2-dy1*dx2)<Shape2D.ACCURACY;
    }

    /**
     * Computes the orientation of the 3 points: returns +1 is the path
     * P0->P1->P2 turns Counter-Clockwise, -1 if the path turns Clockwise, and 0
     * if the point P2 is located on the line segment [P0 P1]. Algorithm taken
     * from Sedgewick.
     * 
     * @param p0 the initial point
     * @param p1 the middle point
     * @param p2 the last point
     * @return +1, 0 or -1, depending on the relative position of the points
     */
    public final static int ccw(Point2D p0, Point2D p1, Point2D p2) {
        double x0 = p0.getX();
        double y0 = p0.getY();
        double dx1 = p1.getX()-x0;
        double dy1 = p1.getY()-y0;
        double dx2 = p2.getX()-x0;
        double dy2 = p2.getY()-y0;

        if (dx1*dy2>dy1*dx2)
            return +1;
        if (dx1*dy2<dy1*dx2)
            return -1;
        if ((dx1*dx2<0)||(dy1*dy2<0))
            return -1;
        if ((dx1*dx1+dy1*dy1)<(dx2*dx2+dy2*dy2))
            return +1;
        return 0;
    }

    public final static Point2D midPoint(java.awt.geom.Point2D p1,
            java.awt.geom.Point2D p2) {
        return new Point2D((p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2);
    }

    /**
     * Computes the centroid, or center of mass, of an array of points.
     * 
     * @param points an array of points
     * @return the centroid of the points
     */
    public final static Point2D centroid(java.awt.geom.Point2D[] points) {
        int n = points.length;
        double sx = 0, sy = 0;
        for (int i = 0; i<n; i++) {
            sx += points[i].getX();
            sy += points[i].getY();
        }
        return new Point2D(sx/n, sy/n);
    }

    /**
     * Computes the centroid, or center of mass, of a collection of points.
     * 
     * @param points a collection of points
     * @return the centroid of the points
     */
    public final static Point2D centroid(Collection<? extends Point2D> points) {
        int n = points.size();
        double sx = 0, sy = 0;
        for (Point2D point : points) {
            sx += point.getX();
            sy += point.getY();
        }
        return new Point2D(sx/n, sy/n);
    }

    /**
     * Compute the centroid of three points.
     * 
     * @param pt1 the first point
     * @param pt2 the second point
     * @param pt3 the third point
     * @return the centroid of the 3 points
     */
    public final static Point2D centroid(java.awt.geom.Point2D pt1,
            java.awt.geom.Point2D pt2, java.awt.geom.Point2D pt3) {
        return new Point2D(
                (pt1.getX()+pt2.getX()+pt3.getX())/3, 
                (pt1.getY()+pt2.getY()+pt3.getY())/3);
    }

    
    // ===================================================================
    // Methods specific to Point2D

    public Point2D plus(java.awt.geom.Point2D p) {
        return new Point2D(p.getX()+x, p.getY()+y);
    }

    public Point2D minus(java.awt.geom.Point2D p) {
        return new Point2D(x-p.getX(), y-p.getY());
    }

    /**
     * Returns the new point translated by amount given in each direction.
     * @param tx the translation in x direction
     * @param ty the translation in y direction
     * @return the translated point
     */
    public Point2D translate(double tx, double ty) {
        return new Point2D(this.x+tx, this.y+ty);
    }
    
    /**
     * Returns the new point scaled by amount given in each direction.
     * @param kx the scale factor in x direction
     * @param ky the scale factor in y direction
     * @return the scaled point
     */
    public Point2D scale(double kx, double ky) {
        return new Point2D(this.x*kx, this.y*ky);
    }
    
    /**
     * Returns the new point scaled by the same amount in each direction.
     * @param k the scale factor
     * @return the scaled point
     */
    public Point2D scale(double k) {
        return new Point2D(this.x*k, this.y*k);
    }
    
    /**
     * Rotates the point by a given angle around the origin.
     * @param theta the angle of rotation
     * @return the rotated point.
     */
    public Point2D rotate(double theta){
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new Point2D(x*cot-y*sit, x*sit+y*cot);
    }
      
    /**
     * Rotates the point by a given angle around an arbitrary center.
     * @param center the center of the rotation
     * @param theta the angle of rotation
     * @return the rotated point.
     */
    public Point2D rotate(Point2D center, double theta){
        double cx = center.getX();
        double cy = center.getY();
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new Point2D(
                x*cot-y*sit+(1-cot)*cx+sit*cy, 
                x*sit+y*cot+(1-cot)*cy-sit*cx);
    }
    
    // ===================================================================
    // Methods specific to Point2D

    /**
     * Convert point to an integer version. Coordinates are rounded to the
     * nearest integer.
     * 
     * @return an instance of java.awt.Point
     */
    public java.awt.Point getAsInt() {
        return new java.awt.Point((int) x, (int) y);
    }

    /**
     * Convert point to an double version.
     */
    public java.awt.geom.Point2D.Double getAsDouble() {
        return new java.awt.geom.Point2D.Double(x, y);
    }

    /**
     * Convert point to a float version. Coordinates are rounded to the nearest
     * float.
     */
    public java.awt.geom.Point2D.Float getAsFloat() {
        return new java.awt.geom.Point2D.Float((float) x, (float) y);
    }

    /**
     * Set location specified as polar coordinate : distance from origin + angle
     * with horizontal.
     * @deprecated use Point2D.createPolar() instead (0.7.0)
     */
    @Deprecated
    public void setPolarLocation(double rho, double theta) {
        x = rho*Math.cos(theta);
        y = rho*Math.sin(theta);
    }

    /**
     * Set location at distance 'rho' from given point, and making an angle
     * 'theta' with horizontal.
     * @deprecated use Point2D.createPolar() instead (0.7.0)
     */
    @Deprecated
    public void setPolarLocation(java.awt.geom.Point2D point, double rho,
            double theta) {
        x = point.getX()+rho*Math.cos(theta);
        y = point.getY()+rho*Math.sin(theta);
    }

    // ===================================================================
    // Methods implementing Shape2D interface

    /**
     * Compute the distance between this and the point <code>point</code>.
     */
    public double getDistance(java.awt.geom.Point2D point) {
        return getDistance(point.getX(), point.getY());
    }

    /**
     * Compute the distance between current point and point with coordinate
     * <code>(x,y)</code>. Uses the <code>Math.hypot()</code> function for
     * better robustness than simple square root.
     */
    public double getDistance(double x, double y) {
        return Math.hypot(getX()-x, getY()-y);
    }

    /**
     * Returns true if the point is bounded. A point is unbounded if at least
     * one of its coordinates is infinite or NaN.
     * 
     * @return true if both coordinates of the point are finite
     */
    public boolean isBounded() {
        if (java.lang.Double.isInfinite(this.x))
            return false;
        if (java.lang.Double.isInfinite(this.y))
            return false;
        if (java.lang.Double.isNaN(this.x))
            return false;
        if (java.lang.Double.isNaN(this.y))
            return false;
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    /**
     * return true if the two points are equal.
     */
    public boolean contains(double x, double y) {
        return this.equals(new Point2D(x, y));
    }

    /**
     * return true if the two points are equal.
     */
    public boolean contains(java.awt.geom.Point2D p) {
        return this.equals(p);
    }

    /**
     * Returns either the point itself, or the shape EMPTY_SET, depending on
     * whether the point lies inside the specified box.
     */
    public Point2D clip(Box2D box) {
        if (x<box.getMinX())
            return null;
        if (y<box.getMinY())
            return null;
        if (x>box.getMaxX())
            return null;
        if (y>box.getMaxY())
            return null;

        return this;
    }

    /**
     * Returns a bounding box with zero width and zero height, whose coordinates
     * limits are point coordinates.
     */
    public Box2D getBoundingBox() {
        return new Box2D(getX(), getX(), getY(), getY());
    }

    /**
     * Returns the transformed point.
     */
    public Point2D transform(AffineTransform2D trans) {
        double[] tab = trans.getCoefficients();
        return new Point2D(
                x*tab[0]+y*tab[1]+tab[2], 
                x*tab[3]+y*tab[4]+tab[5]);
    }

    // ===================================================================
    // Graphical methods

    /**
     * Draws the point on the specified Graphics2D, using default radius equal
     * to 1.
     * 
     * @param g2 the graphics to draw the point
     */
    public void draw(Graphics2D g2) {
        this.draw(g2, 1);
    }

    /**
     * Draws the point on the specified Graphics2D, by filling a disc with a
     * given radius.
     * 
     * @param g2 the graphics to draw the point
     */
    public void draw(Graphics2D g2, double r) {
        g2.fill(new java.awt.geom.Ellipse2D.Double(x-r, y-r, 2*r, 2*r));
    }

    // ===================================================================
    // Methods implementing the PointShape2D interface

    /* (non-Javadoc)
     * @see math.geom2d.point.PointShape2D#getPointNumber()
     */
    public int getPointNumber() {
        return 1;
    }

    /* (non-Javadoc)
     * @see math.geom2d.point.PointShape2D#getPoints()
     */
    public Collection<Point2D> getPoints() {
        ArrayList<Point2D> array = new ArrayList<Point2D>(1);
        array.add(this);
        return array;
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Point2D> iterator() {
        return this.getPoints().iterator();
    }

    // ===================================================================
    // Override of Object methods

    @Override
    public String toString() {
        return new String("Point2D[" + x + ", "+y+"]");
    }
    
    /**
     * Two points are considered equal if their Euclidean distance is less than
     * Shape2D.ACCURACY.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof java.awt.geom.Point2D))
            return false;
        java.awt.geom.Point2D p = (java.awt.geom.Point2D) obj;
        return this.distance(p.getX(), p.getY())<Shape2D.ACCURACY;
    }
    
    @Override
    public Point2D clone() {
        return new Point2D(x, y);
    }
}