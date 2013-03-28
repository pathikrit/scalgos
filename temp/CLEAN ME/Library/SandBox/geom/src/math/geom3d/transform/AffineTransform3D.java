/* file : AffineTransform3D.java
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
 * Created on 27 nov. 2005
 *
 */

package math.geom3d.transform;

import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;

/**
 * @author dlegland
 */
public class AffineTransform3D implements Bijection3D {

    // TODO make the class immutable

    // coefficients for x coordinate.
    protected double m00, m01, m02, m03;

    // coefficients for y coordinate.
    protected double m10, m11, m12, m13;

    // coefficients for y coordinate.
    protected double m20, m21, m22, m23;

    // ===================================================================
    // public static methods

    public final static AffineTransform3D createTranslation(Vector3D vec) {
        return createTranslation(vec.getX(), vec.getY(), vec.getZ());
    }

    public final static AffineTransform3D createTranslation(double x, double y,
            double z) {
        return new AffineTransform3D(1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z);
    }

    public final static AffineTransform3D createRotationOx(double theta) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new AffineTransform3D(1, 0, 0, 0, 0, cot, -sit, 0, 0, sit, cot,
                0);
    }

    public final static AffineTransform3D createRotationOy(double theta) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new AffineTransform3D(cot, 0, sit, 0, 0, 1, 0, 0, -sit, 0, cot,
                0);
    }

    public final static AffineTransform3D createRotationOz(double theta) {
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        return new AffineTransform3D(cot, -sit, 0, 0, sit, cot, 0, 0, 0, 0, 1,
                0);
    }

    AffineTransform3D createScaling(double s) {
        return createScaling(s, s, s);
    }

    AffineTransform3D createScaling(double sx, double sy, double sz) {
        return new AffineTransform3D(sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, 0);
    }

    // ===================================================================
    // constructors

    /** Creates a new affine transform3D set to identity */
    public AffineTransform3D() {
        // init to identity matrix
        m00 = m11 = m22 = 1;
        m01 = m02 = m03 = 0;
        m10 = m12 = m13 = 0;
        m20 = m21 = m23 = 0;
    }

    public AffineTransform3D(double[] coefs) {
        if (coefs.length==9) {
            m00 = coefs[0];
            m01 = coefs[1];
            m02 = coefs[2];
            m10 = coefs[3];
            m11 = coefs[4];
            m12 = coefs[5];
            m20 = coefs[6];
            m21 = coefs[7];
            m22 = coefs[8];
        } else if (coefs.length==12) {
            m00 = coefs[0];
            m01 = coefs[1];
            m02 = coefs[2];
            m03 = coefs[3];
            m10 = coefs[4];
            m11 = coefs[5];
            m12 = coefs[6];
            m13 = coefs[7];
            m20 = coefs[8];
            m21 = coefs[9];
            m22 = coefs[10];
            m23 = coefs[11];
        }
    }

    public AffineTransform3D(double xx, double yx, double zx, double tx,
            double xy, double yy, double zy, double ty, double xz, double yz,
            double zz, double tz) {
        m00 = xx;
        m01 = yx;
        m02 = zx;
        m03 = tx;
        m10 = xy;
        m11 = yy;
        m12 = zy;
        m13 = ty;
        m20 = xz;
        m21 = yz;
        m22 = zz;
        m23 = tz;
    }

    // ===================================================================
    // accessors

    public boolean isIdentity() {
        if (m00!=1)
            return false;
        if (m11!=1)
            return false;
        if (m22!=0)
            return false;
        if (m01!=0)
            return false;
        if (m02!=0)
            return false;
        if (m03!=0)
            return false;
        if (m10!=0)
            return false;
        if (m12!=0)
            return false;
        if (m13!=0)
            return false;
        if (m20!=0)
            return false;
        if (m21!=0)
            return false;
        if (m23!=0)
            return false;
        return true;
    }

    /**
     * Returns the affine coefficients of the transform. Result is an array of
     * 12 double.
     */
    public double[] getCoefficients() {
        double[] tab = { m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22,
                m23 };
        return tab;
    }

    /**
     * Computes the determinant of this transform. Can be zero.
     * 
     * @return the determinant of the transform.
     */
    private double getDeterminant() {
        return +m00*(m11*m22-m12*m21)-m01*(m10*m22-m20*m12)+m02
                *(m10*m21-m20*m11);
    }

    /**
     * Computes the inverse affine transform.
     */
    public AffineTransform3D getInverseTransform() {
        double det = this.getDeterminant();
        return new AffineTransform3D((m11*m22-m21*m12)/det, (m21*m01-m01*m22)
                /det, (m01*m12-m11*m02)/det, (m01*(m22*m13-m12*m23)+m02
                *(m11*m23-m21*m13)-m03*(m11*m22-m21*m12))
                /det, (m20*m12-m10*m22)/det, (m00*m22-m20*m02)/det,
                (m10*m02-m00*m12)/det, (m00*(m12*m23-m22*m13)-m02
                        *(m10*m23-m20*m13)+m03*(m10*m22-m20*m12))
                        /det, (m10*m21-m20*m11)/det, (m20*m01-m00*m21)/det,
                (m00*m11-m10*m01)/det, (m00*(m21*m13-m11*m23)+m01
                        *(m10*m23-m20*m13)-m03*(m10*m21-m20*m11))
                        /det);
    }

    // ===================================================================
    // mutators

    /**
     * @deprecated AffineTransform3d is immutable (0.6.3)
     */
    @Deprecated
    public void setTransform(double n00, double n01, double n02, double n03,
            double n10, double n11, double n12, double n13, double n20,
            double n21, double n22, double n23) {
        m00 = n00;
        m01 = n01;
        m02 = n02;
        m03 = n03;
        m10 = n10;
        m11 = n11;
        m12 = n12;
        m13 = n13;
        m20 = n20;
        m21 = n21;
        m22 = n22;
        m23 = n23;
    }

    /**
     * @deprecated AffineTransform3d is immutable (0.6.3)
     */
    @Deprecated
    public void setTransform(AffineTransform3D trans) {
        m00 = trans.m00;
        m01 = trans.m01;
        m02 = trans.m02;
        m03 = trans.m03;
        m10 = trans.m10;
        m11 = trans.m11;
        m12 = trans.m12;
        m13 = trans.m13;
        m20 = trans.m20;
        m21 = trans.m21;
        m22 = trans.m22;
        m23 = trans.m23;
    }

    /**
     * @deprecated AffineTransform3d is immutable (0.6.3)
     */
    @Deprecated
    public void setToIdentity() {
        m00 = m11 = m22 = 1;
        m01 = m02 = m03 = 0;
        m10 = m12 = m13 = 0;
        m20 = m21 = m23 = 0;
    }

    // ===================================================================
    // general methods

    // TODO: add methods to concatenate affine transforms.

    /**
     * Combine this transform with another AffineTransform.
     */
    public void transform(AffineTransform3D trans) {
        double n00 = m00*trans.m00+m10*trans.m01;
        double n10 = m00*trans.m10+m10*trans.m11;
        double n01 = m01*trans.m00+m11*trans.m01;
        double n11 = m01*trans.m10+m11*trans.m11;
        double n02 = m02*trans.m00+m12*trans.m01+trans.m02;
        double n12 = m02*trans.m10+m12*trans.m11+trans.m12;
        m00 = n00;
        m01 = n01;
        m02 = n02;
        m10 = n10;
        m11 = n11;
        m12 = n12;
    }

    /**
     * Combine this transform with another AffineTransform.
     */
    public void preConcatenate(AffineTransform3D trans) {
        double n00 = trans.m00*m00+trans.m10*m01;
        double n10 = trans.m00*m10+trans.m10*m11;
        double n01 = trans.m01*m00+trans.m11*m01;
        double n11 = trans.m01*m10+trans.m11*m11;
        double n02 = trans.m02*m00+trans.m12*m01+m02;
        double n12 = trans.m02*m10+trans.m12*m11+m12;
        m00 = n00;
        m01 = n01;
        m02 = n02;
        m10 = n10;
        m11 = n11;
        m12 = n12;
    }

    /**
     * @deprecated shapes are responsible of their transform (0.6.3)
     */
    @Deprecated
    public Shape3D transform(Shape3D shape) {
        return shape.transform(this);
    }

    public Point3D[] transformPoints(Point3D[] src, Point3D[] dst) {
        if (dst==null)
            dst = new Point3D[src.length];
        if (dst[0]==null)
            for (int i = 0; i<src.length; i++)
                dst[i] = new Point3D();

        double coef[] = getCoefficients();

        for (int i = 0; i<src.length; i++)
            dst[i].setLocation(new Point3D(src[i].getX()*coef[0]+src[i].getY()
                    *coef[1]+src[i].getZ()*coef[2]+coef[3], src[i].getX()
                    *coef[4]+src[i].getY()*coef[5]+src[i].getZ()*coef[6]
                    +coef[7], src[i].getX()*coef[8]+src[i].getY()*coef[9]
                    +src[i].getZ()*coef[10]+coef[12]));
        return dst;
    }

    public Point3D transformPoint(Point3D src, Point3D dst) {
        double coef[] = getCoefficients();
        if (dst==null)
            dst = new Point3D();
        dst.setLocation(new Point3D(src.getX()*coef[0]+src.getY()*coef[1]
                +src.getZ()*coef[2]+coef[3], src.getX()*coef[4]+src.getY()
                *coef[5]+src.getZ()*coef[6]+coef[7], src.getX()*coef[8]
                +src.getY()*coef[9]+src.getZ()*coef[10]+coef[12]));
        return dst;
    }

    /**
     * Compares two transforms. Returns true if all inner fields are equal up to
     * the precision given by Shape3D.ACCURACY.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AffineTransform3D))
            return false;

        double tab[] = ((AffineTransform3D) obj).getCoefficients();

        if (Math.abs(tab[0]-m00)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[1]-m01)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[2]-m02)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[3]-m03)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[4]-m10)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[5]-m11)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[6]-m12)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[7]-m13)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[8]-m20)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[9]-m21)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[10]-m22)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(tab[11]-m23)>Shape3D.ACCURACY)
            return false;
        return true;
    }

}
