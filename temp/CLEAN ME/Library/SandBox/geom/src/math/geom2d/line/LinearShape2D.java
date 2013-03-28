/**
 * 
 */

package math.geom2d.line;

import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.curve.Curve2D;

/**
 * A curve that can be inscribed in a straight line, line a ray, a straight
 * line, or a line segment.
 * 
 * @author dlegland
 */
public interface LinearShape2D extends Curve2D {

    public abstract StraightLine2D getSupportingLine();

    /**
     * Gets Angle with axis (O,i), counted counter-clockwise. Result is given
     * between 0 and 2*pi.
     */
    public abstract double getHorizontalAngle();

    /**
     * Returns a point in the linear shape.
     * 
     * @return a point in the linear shape.
     */
    public abstract Point2D getOrigin();

    /**
     * Return one direction vector of the linear shape.
     * 
     * @return a direction vector
     */
    public abstract Vector2D getVector();

    /**
     * Returns the unique intersection with a linear shape. If the intersection
     * doesn't exist (parallel lines), returns null.
     */
    public abstract Point2D getIntersection(LinearShape2D line);

    public LinearShape2D transform(AffineTransform2D trans);
}
