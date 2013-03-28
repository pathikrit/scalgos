/**
 * File: 	PointSet2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 5 févr. 09
 */
package math.geom2d;

import java.awt.geom.Point2D;
import java.util.Collection;

import math.geom2d.point.PointArray2D;


/**
 * @author dlegland
 * @deprecated use math.geom2d.point.PointArray2D instead (0.7.0)
 */
@Deprecated
public class PointSet2D extends PointArray2D {

    /**
     * 
     */
    public PointSet2D() {
        super();
    }

    /**
     * @param n
     */
    public PointSet2D(int n) {
        super(n);
    }

    /**
     * @param points
     */
    public PointSet2D(Point2D[] points) {
        super(points);
    }

    /**
     * @param points
     */
    public PointSet2D(Collection<? extends Point2D> points) {
        super(points);
    }

}
