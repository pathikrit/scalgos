/* file : PolyBezierCurve2D.java
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
 * Created on 8 mai 2006
 *
 */

package math.geom2d.spline;

import java.util.Collection;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.Curve2DUtils;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.curve.PolyCurve2D;

/**
 * A set of Bezier curves, making a continuous curve.
 * 
 * @author dlegland
 */
public class PolyBezierCurve2D extends PolyCurve2D<BezierCurve2D> {

    public PolyBezierCurve2D() {
        super();
    }

    public PolyBezierCurve2D(BezierCurve2D[] curves) {
        super(curves);
    }

    public PolyBezierCurve2D(Collection<BezierCurve2D> curves) {
        super(curves);
    }

    /**
     * returns a new PolyBezierCurve2D.
     */
    @Override
    public PolyBezierCurve2D clip(Box2D box) {
        // Clip the curve
        CurveSet2D<Curve2D> set = Curve2DUtils.clipCurve(this, box);

        // Stores the result in appropriate structure
        PolyBezierCurve2D result = new PolyBezierCurve2D();

        // convert the result
        for (Curve2D curve : set.getCurves()) {
            if (curve instanceof BezierCurve2D)
                result.addCurve((BezierCurve2D) curve);
        }
        return result;
    }

    @Override
    public PolyBezierCurve2D transform(AffineTransform2D trans) {
        PolyBezierCurve2D result = new PolyBezierCurve2D();
        for (BezierCurve2D curve : curves)
            result.addCurve(curve.transform(trans));
        return result;
    }

}
