/* File Bijection2D.java 
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

package math.geom2d.transform;

// Imports

/**
 * Interface for all bijective transformations in the euclidean plane. A
 * transformation is a bijection if there is a direct relation from sources to
 * output. In this case, this means we can find the inverse transformation for
 * each bijection.
 */
public interface Bijection2D extends Transform2D {

    // ===================================================================
    // constants

    // ===================================================================
    // class variables

    // ===================================================================
    // constructors

    // ===================================================================
    // accessors

    // ===================================================================
    // modifiers

    // ===================================================================
    // general methods

    public abstract Bijection2D getInverseTransform();
}