/* File Domain2D.java 
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

package math.geom2d.domain;

import java.awt.Graphics2D;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Shape2D;

// Imports

/**
 * Interface for shapes that draws an 'interior' and an 'exterior'. An
 * AbstractDomain2D can be defined with a non-self intersecting set of Curve2D,
 * and contains all points lying 'on the left' of the parent curve.
 * <p>
 * Some Shape may seem very similar, for example Conic2D and ConicCurve2D. The
 * reason is that a point can be contained in a Conic2D but not in the
 * ConicCurve2D.
 */
public interface Domain2D extends Shape2D {

    /**
     * @deprecated empty shapes are represented by null value, reducing the
     *      total number of classes
     */
    @Deprecated
    public final static Domain2D EMPTY_DOMAIN2D = new EmptyDomain2D();

    /**
     * Returns the boundary of the set. This boundary is either a continuous non
     * intersecting curve (connected domain), or a set of non intersecting
     * continuous curve (one continuous non-intersection for each connected part
     * of the domain).
     * <p>
     * The returned curve is oriented, with an interior and an exterior.
     * 
     * @return the boundary of the domain
     */
    public abstract Boundary2D getBoundary();

    /**
     * Returns the domain which complements this domain in the plane.
     * 
     * @return the complement of this domain.
     * @since 0.6.3
     */
    public abstract Domain2D complement();

    public abstract Domain2D transform(AffineTransform2D transform);

    public abstract Domain2D clip(Box2D box);

    /**
     * Draws the boundary of the domain, using current Stroke and color.
     * 
     * @param g2 the Graphics to draw on
     * @since 0.6.3
     */
    public abstract void draw(Graphics2D g2);

    /**
     * Fills the interior of the domain, using the Graphics current Paint.
     * 
     * @param g2 the Graphics to fill on
     * @since 0.6.3
     */
    public abstract void fill(Graphics2D g2);

    /**
     * Definition of an empty domain. Should preferably be accessed through the
     * EMPTY_DOMAIN static variable.
     * 
     * @deprecated empty shapes are represented by null value, reducing the
     *      total number of classes
     * @author dlegland
     */
    @Deprecated
    public static class EmptyDomain2D extends Shape2D.EmptySet2D implements
            Domain2D {

        public EmptyDomain2D() {
        }

        public Domain2D complement() {
            // TODO: return full domain
            return this;
        }

        public void draw(Graphics2D g2) {
        }

        public void fill(Graphics2D g2) {
        }

        public Boundary2D getBoundary() {
            return null;
        }

        @Override
        public Domain2D transform(AffineTransform2D trans) {
            return this;
        }

        @Override
        public Domain2D clip(Box2D box) {
            return this;
        }
    }
}