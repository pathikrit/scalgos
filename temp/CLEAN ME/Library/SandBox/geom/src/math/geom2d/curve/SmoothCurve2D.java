/* File SmoothCurve2D.java 
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

package math.geom2d.curve;

// Imports
import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Vector2D;

/**
 * Interface for smooth and continuous curves. They accept first and second
 * derivative at every point, and can be drawn with a parametric representation
 * for every values of t comprised between T0 and T1. Every Curve2D is a
 * compound of several SmoothCurve2D.
 */
public interface SmoothCurve2D extends ContinuousCurve2D {

    public abstract Vector2D getTangent(double t);

    public abstract double getCurvature(double t);

    public abstract SmoothCurve2D getReverseCurve();

    public abstract SmoothCurve2D getSubCurve(double t0, double t1);

    public abstract CurveSet2D<? extends SmoothCurve2D> clip(Box2D box);

    public abstract SmoothCurve2D transform(AffineTransform2D trans);
}