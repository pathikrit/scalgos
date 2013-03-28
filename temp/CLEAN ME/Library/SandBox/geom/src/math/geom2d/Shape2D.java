/* File Shape2D.java 
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

import math.geom2d.AffineTransform2D;

// Imports

/**
 * Main interface for all geometric objects, including points, lines, curves, or
 * planar regions...
 * <p>
 * Depending direct interfaces extending Shape2D are Curve2D and Domain2D. Some
 * direct implementations of Shape2D are Point2D, PointSet2D, or
 * Shape2D.EmptySet2D.
 */
public interface Shape2D {

    // ===================================================================
    // constants

    /**
     * The constant used for testing results.
     */
    public final static double  ACCURACY  = 1e-12;

    /**
     * @deprecated methods returning empty result should return null (0.7.0)
     */
    @Deprecated
    public final static Shape2D EMPTY_SET = new EmptySet2D();

    public abstract boolean contains(double x, double y);

    public abstract boolean contains(java.awt.geom.Point2D p);

    /**
     * get the distance of the shape to the given point, or the distance of
     * point to the frontier of the shape in the case of a plain shape.
     */
    public abstract double getDistance(java.awt.geom.Point2D p);

    /**
     * get the distance of the shape to the given point, specified by x and y,
     * or the distance of point to the frontier of the shape in the case of a
     * plain (i.e. fillable) shape.
     */
    public abstract double getDistance(double x, double y);

    /**
     * Returns true if the shape is bounded, that is if we can draw a finite
     * rectangle enclosing the shape. For example, a straight line or a parabola
     * are not bounded.
     */
    public abstract boolean isBounded();

    /**
     * Returns true if the shape does not contain any point. This is the case
     * for example for PointSet2D without any point.
     * 
     * @return true if the shape does not contain any point.
     */
    public abstract boolean isEmpty();

    /**
     * Returns the bounding box of the shape.
     * 
     * @return the bounding box of the shape.
     */
    public abstract Box2D getBoundingBox();

    /**
     * Clip the shape with the given box, and returns a new shape. The box must
     * be bounded.
     * 
     * @param box the clipping box
     * @return the clipped shape
     */
    public abstract Shape2D clip(Box2D box);

    /**
     * Transforms the shape by an affine transform. Subclasses may override the
     * type of returned shape.
     * 
     * @param trans an affine transform
     * @return the transformed shape
     */
    public abstract Shape2D transform(AffineTransform2D trans);

    /**
     * An empty set is a shape which does not contain any point.
     * 
     * @deprecated empty shapes are represented by null value, reducing the
     *      total number of classes (0.7.0)
     * @author Legland
     */
    @Deprecated
    public class EmptySet2D implements Shape2D {

        protected EmptySet2D() {
        }

        /** returns false. */
        public boolean contains(double arg0, double arg1) {
            return false;
        }

        /** returns false. */
        public boolean contains(java.awt.geom.Point2D arg0) {
            return false;
        }

        /**
         * Not defined for empty set, but returns POSITIVE_INFINITY.
         */
        public double getDistance(java.awt.geom.Point2D p) {
            return Double.POSITIVE_INFINITY;
        }

        /**
         * Not defined for empty set, but returns POSITIVE_INFINITY.
         */
        public double getDistance(double x, double y) {
            return Double.POSITIVE_INFINITY;
        }

        /**
         * Not defined for empty set, but returns FALSE.
         */
        public boolean isBounded() {
            return false;
        }

        /**
         * Returns true by definition.
         */
        public boolean isEmpty() {
            return true;
        }

        public Box2D getBoundingBox() {
            return new Box2D(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        }

        /** returns EmptySet2D. */
        public Shape2D clip(Box2D box) {
            return this;
        }

        /** returns EmptySet2D. */
        public Shape2D transform(AffineTransform2D trans) {
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof EmptySet2D);
        }
    }
}
