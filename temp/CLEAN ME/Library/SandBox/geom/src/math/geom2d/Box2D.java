/* File Box2D.java 
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
 * 
 * Created on 05 mar. 2007
 */

// package

package math.geom2d;

// Imports
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;

import math.geom2d.domain.Boundary2D;
import math.geom2d.domain.BoundaryPolyCurve2D;
import math.geom2d.domain.BoundarySet2D;
import math.geom2d.line.AbstractLine2D;
import math.geom2d.line.LineArc2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.polygon.HRectangle2D;
import math.geom2d.polygon.Ring2D;

/**
 * This class defines bounds of a shape. It stores limits in each direction:
 * <code>x</code> and <code>y</code>. It also provides methods for clipping
 * others shapes, depending on their type.
 */
public class Box2D implements Cloneable {

    // ===================================================================
    // class variables

    private double xmin = 0;
    private double xmax = 0;
    private double ymin = 0;
    private double ymax = 0;

    // ===================================================================
    // constructors

    /** Empty constructor (size and position zero) */
    public Box2D() {
        this(0, 0, 0, 0);
    }

    /**
     * Main constructor, given bounds for x coord, then bounds for y coord. A
     * check is performed to ensure first bound is lower than second bound.
     */
    public Box2D(double x0, double x1, double y0, double y1) {
        xmin = Math.min(x0, x1);
        xmax = Math.max(x0, x1);
        ymin = Math.min(y0, y1);
        ymax = Math.max(y0, y1);
    }

    /** Constructor from awt, to allow easy construction from existing apps. */
    public Box2D(java.awt.geom.Rectangle2D rect) {
        this(rect.getX(), rect.getX()+rect.getWidth(), rect.getY(), rect.getY()
                +rect.getHeight());
    }

    /** Constructor from 2 points, giving extreme coordinates of the box. */
    public Box2D(Point2D p1, Point2D p2) {
        this(p1.getX(), p2.getX(), p1.getY(), p2.getY());
    }

    /** Constructor from a point, a width and an height */
    public Box2D(Point2D point, double w, double h) {
        this(point.getX(), point.getX()+w, point.getY(), point.getY()+h);
    }

    // ===================================================================
    // accessors to Box2D fields

    public double getMinX() {
        return xmin;
    }

    public double getMinY() {
        return ymin;
    }

    public double getMaxX() {
        return xmax;
    }

    public double getMaxY() {
        return ymax;
    }

    public double getWidth() {
        return xmax-xmin;
    }

    public double getHeight() {
        return ymax-ymin;
    }

    /** Returns true if all bounds are finite. */
    public boolean isBounded() {
        if (Double.isInfinite(xmin))
            return false;
        if (Double.isInfinite(ymin))
            return false;
        if (Double.isInfinite(xmax))
            return false;
        if (Double.isInfinite(ymax))
            return false;
        return true;
    }

    // ===================================================================
    // tests of inclusion

    public boolean contains(java.awt.geom.Point2D point) {
        double x = point.getX();
        double y = point.getY();
        if (x<xmin)
            return false;
        if (y<ymin)
            return false;
        if (x>xmax)
            return false;
        if (y>ymax)
            return false;
        return true;
    }

    public boolean contains(double x, double y) {
        if (x<xmin)
            return false;
        if (y<ymin)
            return false;
        if (x>xmax)
            return false;
        if (y>ymax)
            return false;
        return true;
    }

    /**
     * Test if the specified Shape is totally contained in this Box2D. Note that
     * the test is performed on the bounding box of the shape, then for rotated
     * rectangles, this method can return false with a shape totally contained
     * in the rectangle. The problem does not exist for horizontal rectangle,
     * since edges of rectangle and bounding box are parallel.
     */
    public boolean containsBounds(Shape2D shape) {
        if (!shape.isBounded())
            return false;
        for (Point2D point : shape.getBoundingBox().getVertices())
            if (!contains(point))
                return false;

        return true;
    }

    // ===================================================================
    // information on the boundary

    /**
     * Returns a set of straight of lines defining half-planes, that all contain
     * the box. If the box is bounded, the number of straight lines is 4,
     * otherwise it can be less.
     * 
     * @return a set of straight lines
     */
    public Collection<StraightLine2D> getClippingLines() {
        ArrayList<StraightLine2D> lines = new ArrayList<StraightLine2D>(4);

        if (!(Double.isInfinite(ymin)||Double.isNaN(ymin)))
            lines.add(new StraightLine2D(0, ymin, 1, 0));
        if (!(Double.isInfinite(xmax)||Double.isNaN(xmax)))
            lines.add(new StraightLine2D(xmax, 0, 0, 1));
        if (!(Double.isInfinite(ymax)||Double.isNaN(ymax)))
            lines.add(new StraightLine2D(0, ymax, -1, 0));
        if (!(Double.isInfinite(xmin)||Double.isNaN(xmin)))
            lines.add(new StraightLine2D(xmin, 0, 0, -1));
        return lines;
    }

    public Collection<LinearShape2D> getEdges() {
        ArrayList<LinearShape2D> edges = new ArrayList<LinearShape2D>(4);

        if (isBounded()) {
            edges.add(new LineSegment2D(xmin, ymin, xmax, ymin));
            edges.add(new LineSegment2D(xmax, ymin, xmax, ymax));
            edges.add(new LineSegment2D(xmax, ymax, xmin, ymax));
            edges.add(new LineSegment2D(xmin, ymax, xmin, ymin));
            return edges;
        }

        if (!Double.isInfinite(ymin)) {
            if (Double.isInfinite(xmin)&&Double.isInfinite(xmax))
                edges.add(new StraightLine2D(0, ymin, 1, 0));
            else if (!Double.isInfinite(xmin)&&!Double.isInfinite(xmax))
                edges.add(new LineSegment2D(xmin, ymin, xmax, ymin));
            else
                edges.add(new LineArc2D(0, ymin, 1, 0, xmin, xmax));
        }

        if (!Double.isInfinite(xmax)) {
            if (Double.isInfinite(ymin)&&Double.isInfinite(ymax))
                edges.add(new StraightLine2D(xmax, 0, 0, 1));
            else if (!Double.isInfinite(ymin)&&!Double.isInfinite(ymax))
                edges.add(new LineSegment2D(xmax, ymin, xmax, ymax));
            else
                edges.add(new LineArc2D(xmax, 0, 0, 1, ymin, ymax));
        }

        if (!Double.isInfinite(ymax)) {
            if (Double.isInfinite(xmin)&&Double.isInfinite(xmax))
                edges.add(new StraightLine2D(0, ymax, 1, 0));
            else if (!Double.isInfinite(xmin)&&!Double.isInfinite(xmax))
                edges.add(new LineSegment2D(xmax, ymax, xmin, ymax));
            else
                edges.add(new LineArc2D(0, ymin, 1, 0, xmin, xmax)
                        .getReverseCurve());
        }

        if (!Double.isInfinite(xmin)) {
            if (Double.isInfinite(ymin)&&Double.isInfinite(ymax))
                edges.add(new StraightLine2D(xmin, 0, 0, -1));
            else if (!Double.isInfinite(ymin)&&!Double.isInfinite(ymax))
                edges.add(new LineSegment2D(xmin, ymax, xmin, ymin));
            else
                edges.add(new LineArc2D(xmin, 0, 0, 1, ymin, ymax)
                        .getReverseCurve());
        }

        return edges;
    }

    public Boundary2D getBoundary() {

        // First case of totally bounded box
        if (isBounded()) {
            Point2D pts[] = new Point2D[4];
            pts[0] = new Point2D(xmin, ymin);
            pts[1] = new Point2D(xmax, ymin);
            pts[2] = new Point2D(xmax, ymax);
            pts[3] = new Point2D(xmin, ymax);
            return new Ring2D(pts);
        }

        // extract boolean info on "boundedness" in each direction
        boolean bx0 = !(Double.isInfinite(xmin));
        boolean bx1 = !(Double.isInfinite(xmax));
        boolean by0 = !(Double.isInfinite(ymin));
        boolean by1 = !(Double.isInfinite(ymax));

        // case of boxes unbounded in both x directions
        if (!bx0&&!bx1) {
            if (!by0&&!by1)
                return new BoundarySet2D<StraightLine2D>();
            if (by0)
                return new StraightLine2D(0, ymin, 1, 0);
            if (by1)
                return new StraightLine2D(0, ymax, -1, 0);
            return new BoundarySet2D<StraightLine2D>(new StraightLine2D[] {
                    new StraightLine2D(0, ymin, 1, 0),
                    new StraightLine2D(0, ymax, -1, 0) });
        }

        // case of boxes unbounded in both y directions
        if (!by0&&!by1) {
            if (!bx0&&!bx1)
                return new BoundarySet2D<StraightLine2D>();
            if (bx0)
                return new StraightLine2D(xmin, 0, 0, -1);
            if (bx1)
                return new StraightLine2D(xmax, 0, 0, 1);
            return new BoundarySet2D<StraightLine2D>(new StraightLine2D[] {
                    new StraightLine2D(xmin, 0, 0, -1),
                    new StraightLine2D(xmax, 0, 0, 1) });
        }

        // "corner boxes"

        if (bx0&&by0) // lower left corner
            return new BoundaryPolyCurve2D<LineArc2D>(
                    new LineArc2D[] {
                            new LineArc2D(xmin, ymin, 0, -1,
                                    Double.NEGATIVE_INFINITY, 0),
                            new LineArc2D(xmin, ymin, 1, 0, 0,
                                    Double.POSITIVE_INFINITY) });

        if (bx1&&by0) // lower right corner
            return new BoundaryPolyCurve2D<LineArc2D>(
                    new LineArc2D[] {
                            new LineArc2D(xmax, ymin, 1, 0,
                                    Double.NEGATIVE_INFINITY, 0),
                            new LineArc2D(xmax, ymin, 0, 1, 0,
                                    Double.POSITIVE_INFINITY) });

        if (bx1&&by1) // upper right corner
            return new BoundaryPolyCurve2D<LineArc2D>(
                    new LineArc2D[] {
                            new LineArc2D(xmax, ymax, 0, 1,
                                    Double.NEGATIVE_INFINITY, 0),
                            new LineArc2D(xmax, ymax, -1, 0, 0,
                                    Double.POSITIVE_INFINITY) });

        if (bx0&&by1) // upper left corner
            return new BoundaryPolyCurve2D<LineArc2D>(new LineArc2D[] {
                    new LineArc2D(xmin, ymax, -1, 0, Double.NEGATIVE_INFINITY,
                            0),
                    new LineArc2D(xmin, ymax, 0, -1, 0,
                            Double.POSITIVE_INFINITY) });

        // Remains only 4 cases: boxes unbounded in only one direction

        if (bx0)
            return new BoundaryPolyCurve2D<AbstractLine2D>(
                    new AbstractLine2D[] {
                            new LineArc2D(xmin, ymax, -1, 0,
                                    Double.NEGATIVE_INFINITY, 0),
                            new LineSegment2D(xmin, ymax, xmin, ymin),
                            new LineArc2D(xmin, ymin, 1, 0, 0,
                                    Double.POSITIVE_INFINITY) });

        if (bx1)
            return new BoundaryPolyCurve2D<AbstractLine2D>(
                    new AbstractLine2D[] {
                            new LineArc2D(xmax, ymin, 1, 0,
                                    Double.NEGATIVE_INFINITY, 0),
                            new LineSegment2D(xmax, ymin, xmax, ymax),
                            new LineArc2D(xmax, ymax, -1, 0, 0,
                                    Double.POSITIVE_INFINITY) });

        if (by0)
            return new BoundaryPolyCurve2D<AbstractLine2D>(
                    new AbstractLine2D[] {
                            new LineArc2D(xmin, ymin, 0, -1,
                                    Double.NEGATIVE_INFINITY, 0),
                            new LineSegment2D(xmin, ymin, xmax, ymin),
                            new LineArc2D(xmax, ymin, 0, 1, 0,
                                    Double.POSITIVE_INFINITY) });

        if (by1)
            return new BoundaryPolyCurve2D<AbstractLine2D>(
                    new AbstractLine2D[] {
                            new LineArc2D(xmax, ymax, 0, 1,
                                    Double.NEGATIVE_INFINITY, 0),
                            new LineSegment2D(xmax, ymax, xmin, ymax),
                            new LineArc2D(xmin, ymax, 0, -1, 0,
                                    Double.POSITIVE_INFINITY) });

        return null;
    }

    public Collection<Point2D> getVertices() {
        ArrayList<Point2D> points = new ArrayList<Point2D>(4);
        boolean bx0 = !(Double.isInfinite(xmin)||Double.isNaN(xmin));
        boolean bx1 = !(Double.isInfinite(xmax)||Double.isNaN(xmax));
        boolean by0 = !(Double.isInfinite(ymin)||Double.isNaN(ymin));
        boolean by1 = !(Double.isInfinite(ymax)||Double.isNaN(ymax));
        if (bx0&&by0)
            points.add(new Point2D(xmin, ymin));
        if (bx1&&by0)
            points.add(new Point2D(xmax, ymin));
        if (bx0&&by1)
            points.add(new Point2D(xmin, ymax));
        if (bx1&&by1)
            points.add(new Point2D(xmax, ymax));
        return points;
    }

    /** Returns the number of vertices of the box. */
    public int getVertexNumber() {
        return this.getVertices().size();
    }

    // ===================================================================
    // combination of box with other boxes

    /**
     * Returns the Box2D which contains both this box and the specified box.
     * 
     * @param box the bounding box to include
     * @return a new Box2D
     */
    public Box2D union(Box2D box) {
        double xmin = Math.min(this.xmin, box.xmin);
        double xmax = Math.max(this.xmax, box.xmax);
        double ymin = Math.min(this.ymin, box.ymin);
        double ymax = Math.max(this.ymax, box.ymax);
        return new Box2D(xmin, xmax, ymin, ymax);
    }

    /**
     * Returns the Box2D which is contained both by this box and by the
     * specified box.
     * 
     * @param box the bounding box to include
     * @return a new Box2D
     */
    public Box2D intersection(Box2D box) {
        double xmin = Math.max(this.xmin, box.xmin);
        double xmax = Math.min(this.xmax, box.xmax);
        double ymin = Math.max(this.ymin, box.ymin);
        double ymax = Math.min(this.ymax, box.ymax);
        return new Box2D(xmin, xmax, ymin, ymax);
    }

    /**
     * Changes the bounds of this box to also include bounds of the argument.
     * 
     * @param box the bounding box to include
     * @return this
     */
    public Box2D merge(Box2D box) {
        this.xmin = Math.min(this.xmin, box.xmin);
        this.xmax = Math.max(this.xmax, box.xmax);
        this.ymin = Math.min(this.ymin, box.ymin);
        this.ymax = Math.max(this.ymax, box.ymax);
        return this;
    }

    /**
     * Clip this bounding box such that after clipping, it is totally contained
     * in the given box.
     * 
     * @return this
     */
    public Box2D clip(Box2D box) {
        this.xmin = Math.max(this.xmin, box.xmin);
        this.xmax = Math.min(this.xmax, box.xmax);
        this.ymin = Math.max(this.ymin, box.ymin);
        this.ymax = Math.min(this.ymax, box.ymax);
        return this;
    }

    /**
     * Return the new domain created by an affine transform of this box.
     */
    public Box2D transform(AffineTransform2D trans) {
        if (this.isBounded()) {
            // Extract the 4 vertices, transform them, and compute
            // the new bounding box.
            Collection<Point2D> points = this.getVertices();
            double xmin = Double.POSITIVE_INFINITY;
            double xmax = Double.NEGATIVE_INFINITY;
            double ymin = Double.POSITIVE_INFINITY;
            double ymax = Double.NEGATIVE_INFINITY;
            for (Point2D point : points) {
                point = point.transform(trans);
                xmin = Math.min(xmin, point.getX());
                ymin = Math.min(ymin, point.getY());
                xmax = Math.max(xmax, point.getX());
                ymax = Math.max(ymax, point.getY());
            }
            return new Box2D(xmin, xmax, ymin, ymax);
        }

        // TODO: implement a more precise method
        double xmin = Double.NEGATIVE_INFINITY;
        double xmax = Double.POSITIVE_INFINITY;
        double ymin = Double.NEGATIVE_INFINITY;
        double ymax = Double.POSITIVE_INFINITY;

        return new Box2D(xmin, xmax, ymin, ymax);
    }

    // ===================================================================
    // conversion methods

    /**
     * convert to AWT rectangle.
     * 
     * @return an instance of java.awt.geom.Rectangle2D
     */
    public java.awt.Rectangle getAsAWTRectangle() {
        int xr = (int) Math.floor(this.xmin);
        int yr = (int) Math.floor(this.ymin);
        int wr = (int) Math.ceil(this.xmax-xr);
        int hr = (int) Math.ceil(this.ymax-yr);
        return new java.awt.Rectangle(xr, yr, wr, hr);
    }

    /**
     * convert to AWT Rectangle2D. Result is an instance of HRectangle, which
     * extends java.awt.geom.Rectangle2D.Double.
     * 
     * @return an instance of java.awt.geom.Rectangle2D
     */
    public java.awt.geom.Rectangle2D getAsAWTRectangle2D() {
        return new HRectangle2D(xmin, ymin, xmax-xmin, ymax-ymin);
    }

    /**
     * Converts to a rectangle. Result is an instance of HRectangle, which
     * extends java.awt.geom.Rectangle2D.Double.
     * 
     * @return an instance of HRectangle2D
     */
    public HRectangle2D getAsRectangle() {
        return new HRectangle2D(xmin, ymin, xmax-xmin, ymax-ymin);
    }

    public void draw(Graphics2D g2) {
        if (!isBounded())
            throw new UnboundedShapeException();
        this.getBoundary().draw(g2);
    }

    public void fill(Graphics2D g2) {
        if (!isBounded())
            throw new UnboundedShapeException();
        this.getBoundary().fill(g2);
    }

    public Box2D getBoundingBox() {
        return new Box2D(this.getMinX(), this.getMaxX(), this.getMinY(), this
                .getMaxY());
    }

    // ===================================================================
    // methods from Object interface

    /**
     * Test if boxes are the same. two boxes are the same if the have the same
     * bounds.
     */
    @Override
    public boolean equals(Object obj) {
        // check class, and cast type
        if (!(obj instanceof Box2D))
            return false;
        Box2D box = (Box2D) obj;

        if (Math.abs(box.xmin-this.xmin)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(box.ymin-this.ymin)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(box.xmax-this.xmax)>Shape2D.ACCURACY)
            return false;
        if (Math.abs(box.ymax-this.ymax)>Shape2D.ACCURACY)
            return false;

        return true;
    }
    
    @Override
    public Box2D clone() {
        return new Box2D(xmin, xmax, ymin, ymax);
    }
}