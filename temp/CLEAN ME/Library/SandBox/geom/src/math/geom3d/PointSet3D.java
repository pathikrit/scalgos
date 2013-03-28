/**
 * 
 */

package math.geom3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import math.geom3d.transform.AffineTransform3D;

/**
 * @author dlegland
 */
public class PointSet3D implements Shape3D, Iterable<Point3D> {

    protected Collection<Point3D> points = new ArrayList<Point3D>();

    public PointSet3D() {
    }

    /**
     * Instances of Point3D are directly added, other Point are converted to
     * Point3D with the same location.
     */
    public PointSet3D(Point3D[] points) {
        for (Point3D element : points)
            this.points.add(element);
    }

    /**
     * Points must be a collection of java.awt.Point. Instances of Point3D are
     * directly added, other Point are converted to Point3D with the same
     * location.
     * 
     * @param points
     */
    public PointSet3D(Collection<? extends Point3D> points) {
        for (Point3D point : points) {
            this.points.add(point);
        }
    }

    /**
     * Adds a new point to the set of point. If point is not an instance of
     * Point3D, a Point3D with same location is added instead of point.
     * 
     * @param point
     */
    public void addPoint(Point3D point) {
        this.points.add(point);
    }

    /**
     * Add a series of points
     * 
     * @param points an array of points
     */
    public void addPoints(Point3D[] points) {
        for (Point3D element : points)
            this.addPoint(element);
    }

    public void addPoints(Collection<Point3D> points) {
        this.points.addAll(points);
    }

    /**
     * Returns an iterator on the internal point collection.
     * 
     * @return the collection of points
     */
    public Iterator<Point3D> getPoints() {
        return points.iterator();
    }

    /**
     * Removes all points of the set.
     */
    public void clearPoints() {
        this.points.clear();
    }

    /**
     * Returns the number of points in the set.
     * 
     * @return the number of points
     */
    public int getPointsNumber() {
        return points.size();
    }

    // ===================================================================
    // methods implementing the Shape3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#clip(math.geom3d.Box3D)
     */
    public Shape3D clip(Box3D box) {
        PointSet3D res = new PointSet3D();
        Shape3D clipped;
        for (Point3D point : points) {
            clipped = point.clip(box);
            if (clipped!=Shape3D.EMPTY_SET)
                res.addPoint((Point3D) clipped);
        }
        return res;
    }

    public Box3D getBoundingBox() {
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double zmin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;
        double zmax = Double.MIN_VALUE;

        for (Point3D point : points) {
            xmin = Math.min(xmin, point.getX());
            ymin = Math.min(ymin, point.getY());
            zmin = Math.min(zmin, point.getZ());
            xmax = Math.max(xmax, point.getX());
            ymax = Math.max(ymax, point.getY());
            zmax = Math.max(zmax, point.getZ());
        }
        return new Box3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getDistance(math.geom3d.Point3D)
     */
    public double getDistance(Point3D p) {
        if (points.isEmpty())
            return Double.POSITIVE_INFINITY;
        double dist = Double.POSITIVE_INFINITY;
        for (Point3D point : points)
            dist = Math.min(dist, point.getDistance(p));
        return dist;
    }

    public boolean contains(Point3D point) {
        for (Point3D p : points)
            if (point.getDistance(p)<Shape3D.ACCURACY)
                return true;
        return false;
    }

    public boolean isEmpty() {
        return points.size()==0;
    }

    public boolean isBounded() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#transform(math.geom3d.AffineTransform3D)
     */
    public Shape3D transform(AffineTransform3D trans) {
        PointSet3D res = new PointSet3D();
        for (Point3D point : points)
            res.addPoint(point.transform(trans));
        return res;
    }

    // ===================================================================
    // methods implementing the Iterable interface

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Point3D> iterator() {
        return points.iterator();
    }
}
