/* file : Point3D.java
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

package math.geom3d;

import math.geom3d.transform.AffineTransform3D;

/**
 * @author dlegland
 */
public class Point3D implements Shape3D {

    private double x = 0;
    private double y = 0;
    private double z = 0;

    /**
     * Initialize at coordinate (0,0,0).
     */
    public Point3D() {
        this(0, 0, 0);
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setLocation(Point3D point) {
        x = point.getX();
        y = point.getY();
        z = point.getZ();
    }

    public void setLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getDistance(Point3D point) {
        double dx = point.x-x;
        double dy = point.y-y;
        double dz = point.z-z;

        return Math.hypot(Math.hypot(dx, dy), dz);
    }

    /**
     * A point 'contains' another point if their euclidean distance is less than
     * the accuracy.
     */
    public boolean contains(Point3D point) {
        if (getDistance(point)>ACCURACY)
            return false;
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isBounded() {
        return true;
    }

    public Box3D getBoundingBox() {
        return new Box3D(x, x, y, y, z, z);
    }

    public Shape3D clip(Box3D box) {
        if (x<box.getMinX()||x>box.getMaxX())
            return Shape3D.EMPTY_SET;
        if (y<box.getMinY()||y>box.getMaxY())
            return Shape3D.EMPTY_SET;
        if (z<box.getMinZ()||z>box.getMaxZ())
            return Shape3D.EMPTY_SET;
        return this;
    }

    public Point3D transform(AffineTransform3D trans) {
        Point3D res = new Point3D();
        trans.transformPoint(this, res);
        return res;
    }

    // ===================================================================
    // methods overriding Object superclass

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point3D))
            return false;
        Point3D point = (Point3D) obj;

        if (Math.abs(point.x-this.x)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(point.y-this.y)>Shape3D.ACCURACY)
            return false;
        if (Math.abs(point.z-this.z)>Shape3D.ACCURACY)
            return false;
        return true;
    }
}
