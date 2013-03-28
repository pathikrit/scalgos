/* file : Angle2D.java
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
 * Created on 1 déc. 2006
 *
 */

package math.geom2d;

import math.geom2d.line.LinearShape2D;

/**
 * This class is only devoted to static computations.
 * 
 * @author dlegland
 */
public class Angle2D {

    public final static double M_PI    = Math.PI;
    public final static double M_2PI   = Math.PI*2;
    public final static double M_PI_2  = Math.PI/2;
    public final static double M_3PI_2 = 3*Math.PI/2;
    public final static double M_PI_4  = Math.PI/4;

    /**
     * Formats an angle between 0 and 2*PI.
     * 
     * @param angle the angle before formatting
     * @return the same angle, between 0 and 2*PI.
     */
    public final static double formatAngle(double angle) {
        return ((angle%M_2PI)+M_2PI)%M_2PI;
    }

    /**
     * Returns the horizontal angle formed by the line joining the origin and
     * the given point.
     */
    public final static double getHorizontalAngle(java.awt.geom.Point2D point) {
        return (Math.atan2(point.getY(), point.getX())+M_2PI)%(M_2PI);
    }

    /**
     * Returns the horizontal angle formed by the line joining the origin and
     * the point with given coordinate.
     */
    public final static double getHorizontalAngle(double x, double y) {
        return (Math.atan2(y, x)+M_2PI)%(M_2PI);
    }

    /**
     * Returns the horizontal angle formed by the line joining the origin and
     * the point with given coordinate.
     */
    public final static double getHorizontalAngle(Vector2D vect) {
        return (Math.atan2(vect.getY(), vect.getX())+M_2PI)%(M_2PI);
    }

    /**
     * Returns the horizontal angle formed by the line joining the two given
     * points.
     */
    public final static double getHorizontalAngle(LinearShape2D object) {
        Vector2D vect = object.getSupportingLine().getVector();
        return (Math.atan2(vect.getY(), vect.getX())+M_2PI)%(M_2PI);
    }

    /**
     * Returns the horizontal angle formed by the line joining the two given
     * points.
     */
    public final static double getHorizontalAngle(java.awt.geom.Point2D p1,
            java.awt.geom.Point2D p2) {
        return (Math.atan2(p2.getY()-p1.getY(), p2.getX()-p1.getX())+M_2PI)
                %(M_2PI);
    }

    /**
     * Returns the horizontal angle formed by the line joining the two given
     * points.
     */
    public final static double getHorizontalAngle(double x1, double y1,
            double x2, double y2) {
        return (Math.atan2(y2-y1, x2-x1)+M_2PI)%(M_2PI);
    }

    /**
     * <p>Computes the pseudo-angle of a line joining the 2 points. The
     * pseudo-angle has same ordering property has natural angle, but is 
     * expected to be computed faster. The result is given between 0 and 360.
     * </p>
     * @param p1 the initial point
     * @param p2 the final point
     * @return the pseudo angle of line joining p1 to p2
     */public final static double getPseudoAngle(java.awt.geom.Point2D p1,
             java.awt.geom.Point2D p2) {
         double dx =  p2.getX()-p1.getX();
         double dy =  p2.getY()-p1.getY();
         double s = Math.abs(dx)+Math.abs(dy);
         double t = (s==0) ? 0.0 : dy/s;
         if(dx<0) {
             t = 2-t;
         } else 
             if (dy<0) {
                 t += 4;
             }
         return t*90;
    }

    /**
     * Gets angle between two (directed) straight objects. Result is given in
     * radians, between 0 and 2*PI.
     */
    public final static double getAngle(LinearShape2D obj1, LinearShape2D obj2) {
        double angle1 = obj1.getHorizontalAngle();
        double angle2 = obj2.getHorizontalAngle();
        return (angle2-angle1+M_2PI)%(M_2PI);
    }

    /**
     * Gets angle between two vectors. Result is given in radians, between 0 and
     * 2*PI.
     */
    public final static double getAngle(Vector2D vect1, Vector2D vect2) {
        double angle1 = getHorizontalAngle(vect1);
        double angle2 = getHorizontalAngle(vect2);
        return (angle2-angle1+M_2PI)%(M_2PI);
    }

    /**
     * Gets the angle between the ray formed by (p2, p1) and the ray formed by
     * (p2, p3). Result is given in radians, between 0 and 2*PI.
     */
    public final static double getAngle(java.awt.geom.Point2D p1,
            java.awt.geom.Point2D p2, java.awt.geom.Point2D p3) {
        double angle1 = getHorizontalAngle(p2, p1);
        double angle2 = getHorizontalAngle(p2, p3);
        return (angle2-angle1+M_2PI)%(M_2PI);
    }

    /**
     * Gets the angle between the ray formed by (p2, p1) and the ray formed by
     * (p2, p3), where pi = (xi,yi), i=1,2,3. Result is given in radians,
     * between 0 and 2*PI.
     */
    public final static double getAngle(double x1, double y1, double x2,
            double y2, double x3, double y3) {
        double angle1 = getHorizontalAngle(x2, y2, x1, y1);
        double angle2 = getHorizontalAngle(x2, y2, x1, y1);
        return (angle2-angle1+M_2PI)%(M_2PI);
    }

    /**
     * Gets the absolute angle between the ray formed by (p2, p1) and the ray
     * formed by (p2, p3). Result is given in radians, between 0 and PI.
     */
    public final static double getAbsoluteAngle(java.awt.geom.Point2D p1,
            java.awt.geom.Point2D p2, java.awt.geom.Point2D p3) {
        double angle1 = Angle2D.getHorizontalAngle(new Vector2D(p2, p1));
        double angle2 = Angle2D.getHorizontalAngle(new Vector2D(p2, p3));
        angle1 = (angle2-angle1+M_2PI)%(M_2PI);
        if (angle1<Math.PI)
            return angle1;
        else
            return M_2PI-angle1;
    }

    /**
     * Gets the absolute angle between the ray formed by (p2, p1) and the ray
     * formed by (p2, p3), where pi = (xi,yi), i=1,2,3. Result is given in
     * radians, between 0 and PI.
     */
    public final static double getAbsoluteAngle(double x1, double y1,
            double x2, double y2, double x3, double y3) {
        double angle1 = getHorizontalAngle(x2, y2, x1, y1);
        double angle2 = getHorizontalAngle(x2, y2, x3, y3);
        angle1 = (angle2-angle1+M_2PI)%(M_2PI);
        if (angle1<Math.PI)
            return angle1;
        else
            return M_2PI-angle1;
    }

    /**
     * Checks whether two angles are equal.
     * 
     * @param angle1 first angle to compare
     * @param angle2 second angle to compare
     * @return true if the two angle are equal modulo 2*PI
     */
    public final static boolean equals(double angle1, double angle2) {
        angle1 = Angle2D.formatAngle(angle1);
        angle2 = Angle2D.formatAngle(angle2);
        double diff = Angle2D.formatAngle(angle1-angle2);
        if (diff<Shape2D.ACCURACY)
            return true;
        if (Math.abs(diff-Math.PI*2)<Shape2D.ACCURACY)
            return true;
        return false;
    }

    /**
     * Tests if an angle belongs to an angular interval, defined by two limit
     * angle, counted Counter-clockwise.
     * 
     * @param startAngle the beginning of the angular domain
     * @param endAngle the end of the angular domain
     * @param angle the angle to test
     * @return true if angle is between the 2 limits
     */
    public final static boolean containsAngle(double startAngle,
            double endAngle, double angle) {
        startAngle = Angle2D.formatAngle(startAngle);
        endAngle = Angle2D.formatAngle(endAngle);
        angle = Angle2D.formatAngle(angle);
        if (startAngle<endAngle)
            return angle>=startAngle&&angle<=endAngle;
        else
            return angle<=endAngle||angle>=startAngle;
    }

    /**
     * Tests if an angle belongs to an angular interval, defined by two limit
     * angle, and an orientation flag.
     * 
     * @param startAngle the beginning of the angular domain
     * @param endAngle the end of the angular domain
     * @param angle the angle to test
     * @param direct is true if angular domain is oriented Counter clockwise,
     *            and false if angular domain is oriented clockwise.
     * @return true if angle is between the 2 limits
     */
    public final static boolean containsAngle(double startAngle,
            double endAngle, double angle, boolean direct) {
        startAngle = Angle2D.formatAngle(startAngle);
        endAngle = Angle2D.formatAngle(endAngle);
        angle = Angle2D.formatAngle(angle);
        if (direct) {
            if (startAngle<endAngle)
                return angle>=startAngle&&angle<=endAngle;
            else
                return angle<=endAngle||angle>=startAngle;
        } else {
            if (startAngle<endAngle)
                return angle<=startAngle||angle>=endAngle;
            else
                return angle>=endAngle&&angle<=startAngle;
        }
    }
}
