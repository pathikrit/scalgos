/**
 * 
 */

package math.geom3d;

/**
 * @author dlegland
 */
public class Box3D {

    // ===================================================================
    // class variables

    private double xmin = 0;
    private double xmax = 0;
    private double ymin = 0;
    private double ymax = 0;
    private double zmin = 0;
    private double zmax = 0;

    /** Empty constructor (size and position zero) */
    public Box3D() {
        this(0, 0, 0, 0, 0, 0);
    }

    /**
     * Main constructor, given bounds for x coord, bounds for y coord, and
     * bounds for z coord. A check is performed to ensure first bound is lower
     * than second bound.
     */
    public Box3D(double x0, double x1, double y0, double y1, double z0,
            double z1) {
        xmin = Math.min(x0, x1);
        xmax = Math.max(x0, x1);
        ymin = Math.min(y0, y1);
        ymax = Math.max(y0, y1);
        zmin = Math.min(z0, z1);
        zmax = Math.max(z0, z1);
    }

    /** Constructor from 2 points, giving extreme coordinates of the box. */
    public Box3D(Point3D p1, Point3D p2) {
        this(p1.getX(), p2.getX(), p1.getY(), p2.getY(), p1.getZ(), p2.getZ());
    }

    // ===================================================================
    // accessors to Box2D fields

    public double getMinX() {
        return xmin;
    }

    public double getMaxX() {
        return xmax;
    }

    public double getMinY() {
        return ymin;
    }

    public double getMaxY() {
        return ymax;
    }

    public double getMinZ() {
        return zmin;
    }

    public double getMaxZ() {
        return zmax;
    }

    /** Returns the width, i.e. the difference between the min and max x coord */
    public double getWidth() {
        return xmax-xmin;
    }

    /** Returns the height, i.e. the difference between the min and max y coord */
    public double getHeight() {
        return ymax-ymin;
    }

    /** Returns the depth, i.e. the difference between the min and max z coord */
    public double getDepth() {
        return zmax-zmin;
    }

    /**
     * Returns the Box2D which contains both this box and the specified box.
     * 
     * @param box the bounding box to include
     * @return this
     */
    public Box3D union(Box3D box) {
        double xmin = Math.min(this.xmin, box.xmin);
        double xmax = Math.max(this.xmax, box.xmax);
        double ymin = Math.min(this.ymin, box.ymin);
        double ymax = Math.max(this.ymax, box.ymax);
        double zmin = Math.min(this.zmin, box.zmin);
        double zmax = Math.max(this.zmax, box.zmax);
        return new Box3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }

    /**
     * Returns the Box2D which is contained both by this box and by the
     * specified box.
     * 
     * @param box the bounding box to include
     * @return this
     */
    public Box3D intersection(Box3D box) {
        double xmin = Math.max(this.xmin, box.xmin);
        double xmax = Math.min(this.xmax, box.xmax);
        double ymin = Math.max(this.ymin, box.ymin);
        double ymax = Math.min(this.ymax, box.ymax);
        double zmin = Math.max(this.zmin, box.zmin);
        double zmax = Math.min(this.zmax, box.zmax);
        return new Box3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }

}
