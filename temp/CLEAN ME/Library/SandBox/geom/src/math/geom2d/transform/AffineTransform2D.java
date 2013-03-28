/**
 * File: 	AffineTransform2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 5 févr. 09
 */
package math.geom2d.transform;



/**
 * @author dlegland
 * @deprecated use math.geom2d.AffineTransform2D instead (0.7.0)
 */
@Deprecated
public class AffineTransform2D extends math.geom2d.AffineTransform2D {

    /**
     * 
     */
    public AffineTransform2D() {
    }

    /**
     */
    public AffineTransform2D(AffineTransform2D trans) {
        super(trans);
    }

    /**
     */
    public AffineTransform2D(double[] coefs) {
        super(coefs);
    }

    /**
     */
    public AffineTransform2D(double xx, double yx, double tx, double xy,
            double yy, double ty) {
        super(xx, yx, tx, xy, yy, ty);
    }

}
