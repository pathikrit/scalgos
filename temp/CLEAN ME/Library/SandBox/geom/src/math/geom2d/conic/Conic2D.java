/* File Conic2D.java 
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

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.domain.Boundary2D;
import math.geom2d.domain.ContinuousOrientedCurve2D;

// Imports

/**
 * Interface for all conic curves: parametric conics, or ellipses, parabolas,
 * and hyperbolas. Degenerate conics are also encompassed by this interface.
 */
public interface Conic2D extends Boundary2D {

    // ===================================================================
    // constants

    /**
     * The different types of conic.
     */
    public enum Type {
        /**
         * Degenerate conic, for example a conic given by the equation
         * <code>x^2+1=0</code>)
         */
        NOT_A_CONIC,
        /** Ellipse */
        ELLIPSE,
        /** Hyperbola */
        HYPERBOLA,
        /** Parabola */
        PARABOLA,
        /** Circle */
        CIRCLE,
        /** Straight Line */
        STRAIGHT_LINE,
        /** Union of two lines */
        TWO_LINES,
        /** Single point */
        POINT;
    }

    // ===================================================================
    // class variables

    // ===================================================================
    // constructors

    // ===================================================================
    // accessors

    // type accessors ------------

    public abstract Type getConicType();

    /**
     * Returns the coefficient of the Cartesian representation of the conic.
     * Cartesian equation has the form :
     * <p>
     * a*x^2 + b*x*y + c*y^2 + d*x + e*y + f
     * <p>
     * The length of the array is then of size 6.
     */
    public abstract double[] getConicCoefficients();

    /**
     * Returns the eccentricity of the conic.
     */
    public abstract double getEccentricity();

    // ===================================================================
    // modifiers

    public abstract Conic2D getReverseCurve();

    public abstract Conic2D transform(AffineTransform2D trans);

    public abstract CurveSet2D<? extends ContinuousOrientedCurve2D> clip(
            Box2D box);
}