/*
 * @(#)GeneralPath.java	1.54 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package math.geom2d.curve;

import math.geom2d.Point2D;

import java.awt.geom.*;
import java.awt.Shape;

/**
 * The <code>GeneralPath</code> class represents a geometric path constructed
 * from straight lines, and quadratic and cubic (Bezier) curves. It can contain
 * multiple subpaths.
 * <p>
 * The winding rule specifies how the interior of a path is determined. There
 * are two types of winding rules: EVEN_ODD and NON_ZERO.
 * <p>
 * An EVEN_ODD winding rule means that enclosed regions of the path alternate
 * between interior and exterior areas as traversed from the outside of the path
 * towards a point inside the region.
 * <p>
 * A NON_ZERO winding rule means that if a ray is drawn in any direction from a
 * given point to infinity and the places where the path intersects the ray are
 * examined, the point is inside of the path if and only if the number of times
 * that the path crosses the ray from left to right does not equal the number of
 * times that the path crosses the ray from right to left.
 * 
 * @version 1.54, 02/02/00
 * @author Jim Graham
 */
public final class GeneralPath2D implements Shape, Cloneable {

    GeneralPath             path;

    /**
     * An even-odd winding rule for determining the interior of a path.
     */
    public static final int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;

    /**
     * A non-zero winding rule for determining the interior of a path.
     */
    public static final int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;

    /**
     * Constructs a new <code>GeneralPath</code> object. If an operation
     * performed on this path requires the interior of the path to be defined
     * then the default NON_ZERO winding rule is used.
     * 
     * @see #WIND_NON_ZERO
     */
    public GeneralPath2D() {
        path = new GeneralPath();
    }

    /**
     * Constructs a new <code>GeneralPath</code> object with the specified
     * winding rule to control operations that require the interior of the path
     * to be defined.
     * 
     * @param rule the winding rule
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     */
    public GeneralPath2D(int rule) {
        path = new GeneralPath(rule);
    }

    /**
     * Constructs a new <code>GeneralPath</code> object with the specified
     * winding rule and the specified initial capacity to store path
     * coordinates. This number is an initial guess as to how many path segments
     * are in the path, but the storage is expanded as needed to store whatever
     * path segments are added to this path.
     * 
     * @param rule the winding rule
     * @param initialCapacity the estimate for the number of path segments in
     *            the path
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     */
    public GeneralPath2D(int rule, int initialCapacity) {
        path = new GeneralPath(rule, initialCapacity);
    }

    /**
     * Constructs a new <code>GeneralPath</code> object from an arbitrary
     * {@link Shape} object. All of the initial geometry and the winding rule
     * for this path are taken from the specified <code>Shape</code> object.
     * 
     * @param s the specified <code>Shape</code> object
     */
    public GeneralPath2D(Shape s) {
        path = new GeneralPath(s);
    }

    /**
     * Adds a point to the path by moving to the specified coordinates.
     * 
     * @param x the x-coordinate of the destination
     * @param y the y-coordinate of the destination
     */
    public synchronized void moveTo(double x, double y) {
        path.moveTo((float) x, (float) y);
    }

    /**
     * Adds a point to the path by moving to the specified coordinates.
     * 
     * @param p the specified point
     */
    public synchronized void moveTo(java.awt.geom.Point2D p) {
        path.moveTo((float) p.getX(), (float) p.getY());
    }

    /**
     * Adds a point to the path by drawing a straight line from the current
     * coordinates to the new specified coordinates.
     * 
     * @param x the x-coordinate of the destination
     * @param y the y-coordinate of the destination
     */
    public synchronized void lineTo(double x, double y) {
        path.lineTo((float) x, (float) y);
    }

    /**
     * Adds a point to the path by drawing a straight line from the current
     * coordinates to the new specified coordinates.
     * 
     * @param p the coordinate of the destionation point
     */
    public synchronized void lineTo(java.awt.geom.Point2D p) {
        path.lineTo((float) p.getX(), (float) p.getY());
    }

    /**
     * Adds a curved segment, defined by two new points, to the path by drawing
     * a Quadratic curve that intersects both the current coordinates and the
     * coordinates (x2,&nbsp;y2), using the specified point (x1,&nbsp;y1) as a
     * quadratic parametric control point.
     * 
     * @param x1 the x-coordinate of the control point
     * @param y1 the y-coordinate of the control point
     * @param x2 the x-coordinate of the end point
     * @param y2 the y-coordinate of the end point
     */
    public synchronized void quadTo(double x1, double y1, double x2, double y2) {
        path.quadTo((float) x1, (float) y1, (float) x2, (float) y2);
    }

    /**
     * Adds a curved segment, defined by two new points, to the path by drawing
     * a Quadratic curve that intersects both the current coordinates and the
     * coordinates (x2,&nbsp;y2), using the specified point (x1,&nbsp;y1) as a
     * quadratic parametric control point.
     * 
     * @param p1 the control point
     * @param p2 the end point
     */
    public synchronized void quadTo(java.awt.geom.Point2D p1,
            java.awt.geom.Point2D p2) {
        path.quadTo((float) p1.getX(), (float) p1.getY(), (float) p2.getX(),
                (float) p2.getY());
    }

    /**
     * Adds a curved segment, defined by three new points, to the path by
     * drawing a Bezier curve that intersects both the current coordinates and
     * the coordinates (x3,&nbsp;y3), using the specified points (x1,&nbsp;y1)
     * and (x2,&nbsp;y2) as Bezier control points.
     * 
     * @param x1 the x-coordinate of the first control point
     * @param y1 the y-coordinate of the first control point
     * @param x2 the x-coordinate of the second control point
     * @param y2 the y-coordinate of the second control point
     * @param x3 the x-coordinate of the end point
     * @param y3 the y-coordinate of the end point
     */
    public synchronized void curveTo(double x1, double y1, double x2,
            double y2, double x3, double y3) {
        path.curveTo((float) x1, (float) y1, (float) x2, (float) y2,
                (float) x3, (float) y3);
    }

    /**
     * Adds a curved segment, defined by three new points, to the path by
     * drawing a Bezier curve that intersects both the current coordinates and
     * the coordinates (x3,&nbsp;y3), using the specified points (x1,&nbsp;y1)
     * and (x2,&nbsp;y2) as Bezier control points.
     * 
     * @param p1 the coordinates of the first control point
     * @param p2 the coordinates of the second control point
     * @param p3 the coordinates of the final endpoint
     */
    public synchronized void curveTo(java.awt.geom.Point2D p1,
            java.awt.geom.Point2D p2, java.awt.geom.Point2D p3) {
        path.curveTo((float) p1.getX(), (float) p1.getY(), (float) p2.getX(),
                (float) p2.getY(), (float) p3.getX(), (float) p3.getY());
    }

    /**
     * Closes the current subpath by drawing a straight line back to the
     * coordinates of the last <code>moveTo</code>. If the path is already
     * closed then this method has no effect.
     */
    public synchronized void closePath() {
        path.closePath();
    }

    /**
     * Appends the geometry of the specified <code>Shape</code> object to the
     * path, possibly connecting the new geometry to the existing path segments
     * with a line segment. If the <code>connect</code> parameter is
     * <code>true</code> and the path is not empty then any initial
     * <code>moveTo</code> in the geometry of the appended <code>Shape</code>
     * is turned into a <code>lineTo</code> segment. If the destination
     * coordinates of such a connecting <code>lineTo</code> segment match the
     * ending coordinates of a currently open subpath then the segment is
     * omitted as superfluous. The winding rule of the specified
     * <code>Shape</code> is ignored and the appended geometry is governed by
     * the winding rule specified for this path.
     * 
     * @param s the <code>Shape</code> whose geometry is appended to this path
     * @param connect a boolean to control whether or not to turn an initial
     *            <code>moveTo</code> segment into a <code>lineTo</code>
     *            segment to connect the new geometry to the existing path
     */
    public void append(Shape s, boolean connect) {
        path.append(s, connect);
    }

    /**
     * Appends the geometry of the specified {@link PathIterator} object to the
     * path, possibly connecting the new geometry to the existing path segments
     * with a line segment. If the <code>connect</code> parameter is
     * <code>true</code> and the path is not empty then any initial
     * <code>moveTo</code> in the geometry of the appended <code>Shape</code>
     * is turned into a <code>lineTo</code> segment. If the destination
     * coordinates of such a connecting <code>lineTo</code> segment match the
     * ending coordinates of a currently open subpath then the segment is
     * omitted as superfluous. The winding rule of the specified
     * <code>Shape</code> is ignored and the appended geometry is governed by
     * the winding rule specified for this path.
     * 
     * @param pi the <code>PathIterator</code> whose geometry is appended to
     *            this path
     * @param connect a boolean to control whether or not to turn an initial
     *            <code>moveTo</code> segment into a <code>lineTo</code>
     *            segment to connect the new geometry to the existing path
     */
    public void append(PathIterator pi, boolean connect) {
        path.append(pi, connect);
    }

    /**
     * Returns the fill style winding rule.
     * 
     * @return an integer representing the current winding rule.
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     */
    public synchronized int getWindingRule() {
        return path.getWindingRule();
    }

    /**
     * Sets the winding rule for this path to the specified value.
     * 
     * @param rule an integer representing the specified winding rule
     * @exception <code>IllegalArgumentException</code> if <code>rule</code>
     *                is not either <code>WIND_EVEN_ODD</code> or
     *                <code>WIND_NON_ZERO</code>
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     */
    public void setWindingRule(int rule) {
        path.setWindingRule(rule);
    }

    /**
     * Returns the coordinates most recently added to the end of the path as a
     * {@link Point2D} object.
     * 
     * @return a <code>Point2D</code> object containing the ending coordinates
     *         of the path or <code>null</code> if there are no points in the
     *         path.
     */
    public synchronized Point2D getCurrentPoint() {
        return new Point2D(path.getCurrentPoint());
    }

    /**
     * Resets the path to empty. The append position is set back to the
     * beginning of the path and all coordinates and point types are forgotten.
     */
    public synchronized void reset() {
        path.reset();
    }

    /**
     * Transforms the geometry of this path using the specified
     * {@link AffineTransform}. The geometry is transformed in place, which
     * permanently changes the boundary defined by this object.
     * 
     * @param at the <code>AffineTransform</code> used to transform the area
     */
    public void transform(AffineTransform at) {
        path.transform(at);
    }

    /**
     * Returns a new transformed <code>Shape</code>.
     * 
     * @param at the <code>AffineTransform</code> used to transform a new
     *            <code>Shape</code>.
     * @return a new <code>Shape</code>, transformed with the specified
     *         <code>AffineTransform</code>.
     */
    public synchronized Shape createTransformedShape(AffineTransform at) {
        return path.createTransformedShape(at);
    }

    /**
     * Return the bounding box of the path.
     * 
     * @return a {@link java.awt.Rectangle} object that bounds the current path.
     */
    public java.awt.Rectangle getBounds() {
        return path.getBounds();
    }

    /**
     * Returns the bounding box of the path.
     * 
     * @return a {@link Rectangle2D} object that bounds the current path.
     */
    public synchronized java.awt.geom.Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    /**
     * Tests if the specified coordinates are inside the boundary of this
     * <code>Shape</code>.
     * 
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return <code>true</code> if the specified coordinates are inside this
     *         <code>Shape</code>; <code>false</code> otherwise
     */
    public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    /**
     * Tests if the specified <code>Point2D</code> is inside the boundary of
     * this <code>Shape</code>.
     * 
     * @param p the specified <code>Point2D</code>
     * @return <code>true</code> if this <code>Shape</code> contains the
     *         specified <code>Point2D</code>, <code>false</code>
     *         otherwise.
     */
    public boolean contains(java.awt.geom.Point2D p) {
        return contains(p);
    }

    /**
     * Tests if the specified rectangular area is inside the boundary of this
     * <code>Shape</code>.
     * 
     * @param x the x coordinate of the rectangle
     * @param y the y coordinate of the rectangle
     * @param w the width of the specified rectangular area
     * @param h the height of the specified rectangular area
     * @return <code>true</code> if this <code>Shape</code> contains the
     *         specified rectangluar area; <code>false</code> otherwise.
     */
    public boolean contains(double x, double y, double w, double h) {
        return contains(x, y, w, h);
    }

    /**
     * Tests if the specified <code>Rectangle2D</code> is inside the boundary
     * of this <code>Shape</code>.
     * 
     * @param r a specified <code>Rectangle2D</code>
     * @return <code>true</code> if this <code>Shape</code> bounds the
     *         specified <code>Rectangle2D</code>; <code>false</code>
     *         otherwise.
     */
    public boolean contains(java.awt.geom.Rectangle2D r) {
        return path.contains(r);
    }

    /**
     * Tests if the interior of this <code>Shape</code> intersects the
     * interior of a specified set of rectangular coordinates.
     * 
     * @param x the position of the left corner
     * @param y the position of the bottom corner
     * @param w the width of the specified rectangular coordinates
     * @param h the height of the specified rectangular coordinates
     * @return <code>true</code> if this <code>Shape</code> and the interior
     *         of the specified set of rectangular coordinates intersect each
     *         other; <code>false</code> otherwise.
     */
    public boolean intersects(double x, double y, double w, double h) {
        return intersects(x, y, w, h);
    }

    /**
     * Tests if the interior of this <code>Shape</code> intersects the
     * interior of a specified <code>Rectangle2D</code>.
     * 
     * @param r the specified <code>Rectangle2D</code>
     * @return <code>true</code> if this <code>Shape</code> and the interior
     *         of the specified <code>Rectangle2D</code> intersect each other;
     *         <code>false</code> otherwise.
     */
    public boolean intersects(java.awt.geom.Rectangle2D r) {
        return intersects(r);
    }

    /**
     * Returns a <code>PathIterator</code> object that iterates along the
     * boundary of this <code>Shape</code> and provides access to the geometry
     * of the outline of this <code>Shape</code>. The iterator for this class
     * is not multi-threaded safe, which means that this
     * <code>GeneralPath</code> class does not guarantee that modifications to
     * the geometry of this <code>GeneralPath</code> object do not affect any
     * iterations of that geometry that are already in process.
     * 
     * @param at an <code>AffineTransform</code>
     * @return a new <code>PathIterator</code> that iterates along the
     *         boundary of this <code>Shape</code> and provides access to the
     *         geometry of this <code>Shape</code>'s outline
     */
    public PathIterator getPathIterator(AffineTransform at) {
        return path.getPathIterator(at);
    }

    /**
     * Returns a <code>PathIterator</code> object that iterates along the
     * boundary of the flattened <code>Shape</code> and provides access to the
     * geometry of the outline of the <code>Shape</code>. The iterator for
     * this class is not multi-threaded safe, which means that this
     * <code>GeneralPath</code> class does not guarantee that modifications to
     * the geometry of this <code>GeneralPath</code> object do not affect any
     * iterations of that geometry that are already in process.
     * 
     * @param at an <code>AffineTransform</code>
     * @param flatness the maximum distance that the line segments used to
     *            approximate the curved segments are allowed to deviate from
     *            any point on the original curve
     * @return a new <code>PathIterator</code> that iterates along the
     *         flattened <code>Shape</code> boundary.
     */
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return path.getPathIterator(at, flatness);
    }

    /**
     * Creates a new object of the same class as this object.
     * 
     * @return a clone of this instance.
     * @exception OutOfMemoryError if there is not enough memory.
     * @see java.lang.Cloneable
     * @since 1.2
     */
    @Override
    public Object clone() {
        return path.clone();
    }
}
