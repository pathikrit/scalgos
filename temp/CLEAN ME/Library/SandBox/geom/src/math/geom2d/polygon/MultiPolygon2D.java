
package math.geom2d.polygon;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.domain.Boundary2D;
import math.geom2d.domain.Boundary2DUtils;
import math.geom2d.domain.BoundarySet2D;
import math.geom2d.domain.ContinuousBoundary2D;
import math.geom2d.domain.Domain2D;
import math.geom2d.line.LineSegment2D;

/**
 * A polygonal domain whose boundary is composed of several disjoint continuous
 * Ring2D.
 * 
 * @author dlegland
 */
public class MultiPolygon2D implements Domain2D, Polygon2D {

    ArrayList<Ring2D> rings = new ArrayList<Ring2D>();

    // ===================================================================
    // Constructors

    public MultiPolygon2D() {
    }

    public MultiPolygon2D(Ring2D polyline) {
        rings.add(polyline);
    }

    public MultiPolygon2D(Ring2D[] polylines) {
        for (Ring2D polyline : polylines)
            this.rings.add(polyline);
    }

    public MultiPolygon2D(SimplePolygon2D polygon) {
        rings.addAll(polygon.getBoundary().getCurves());
    }

    public MultiPolygon2D(Collection<Ring2D> lines) {
        rings.addAll(lines);
    }

    // ===================================================================
    // methods specific to MultiPolygon2D

    public void addPolygon(SimplePolygon2D polygon) {
        rings.addAll(polygon.getBoundary().getCurves());
    }

    /**
     * Return the set of (oriented) polygons forming this MultiPolygon2D.
     * 
     * @return a set of Polygon2D.
     */
    public Collection<SimplePolygon2D> getPolygons() {
        // allocate memory for polygon array
        ArrayList<SimplePolygon2D> polygons = new ArrayList<SimplePolygon2D>();
        
        // create a new SimplePolygon with each ring
        for (Ring2D polyline : rings)
            polygons.add(new SimplePolygon2D(polyline.getVertices()));
        return polygons;
    }

    public void addPolyline(Ring2D polyline) {
        rings.add(polyline);
    }

    // ===================================================================
    // methods implementing the Polygon2D interface

  
    /* (non-Javadoc)
     * @see math.geom2d.polygon.Polygon2D#getRings()
     */
    public Collection<Ring2D> getRings() {
        return Collections.unmodifiableList(rings);
    }

    // ===================================================================
    // methods inherited from interface AbstractDomain2D

    public BoundarySet2D<Ring2D> getBoundary() {
        return new BoundarySet2D<Ring2D>(rings);
    }

    public Polygon2D complement() {
        // allocate memory for array of reversed rings
        ArrayList<Ring2D> reverseLines = new ArrayList<Ring2D>(rings.size());
        
        // reverse each ring
        for (Ring2D ring : rings)
            reverseLines.add(ring.getReverseCurve());
        
        // create the new MultiMpolygon2D with set of reversed rings
        return new MultiPolygon2D(reverseLines);
    }

    // ===================================================================
    // methods implementing the interface Polygon2D

    public Collection<LineSegment2D> getEdges() {
        ArrayList<LineSegment2D> edges = new ArrayList<LineSegment2D>();
        for (Ring2D ring : rings)
            edges.addAll(ring.getEdges());
        return edges;
    }

    public int getEdgeNumber() {
        int count = 0;
        for (Ring2D ring : rings)
            count += ring.getVertexNumber();
        return count;
    }

    public Collection<math.geom2d.Point2D> getVertices() {
        ArrayList<math.geom2d.Point2D> points = new ArrayList<math.geom2d.Point2D>();
        for (Ring2D ring : rings)
            points.addAll(ring.getVertices());
        return points;
    }

    /**
     * Returns the i-th vertex of the polygon.
     * 
     * @param i index of the vertex, between 0 and the number of vertices
     */
    public Point2D getVertex(int i) {
        int count = 0;
        Ring2D boundary = null;

        for (Ring2D ring : rings) {
            int nv = ring.getVertexNumber();
            if (count+nv>i) {
                boundary = ring;
                break;
            }
            count += nv;
        }

        if (boundary==null)
            throw new IndexOutOfBoundsException();

        return boundary.getVertex(i-count);
    }

    public int getVertexNumber() {
        int count = 0;
        for (Ring2D ring : rings)
            count += ring.getVertexNumber();
        return count;
    }

    // ===================================================================
    // methods inherited from interface Shape2D

    public Box2D getBoundingBox() {
        // start with empty bounding box
        Box2D box = new Box2D(Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY);
        
        // compute union of all bounding boxes
        for (Ring2D ring : this.rings)
            box = box.union(ring.getBoundingBox());
        
        // return result
        return box;
    }

    /**
     * Returns a new instance of MultiPolygon2D.
     */
    public MultiPolygon2D clip(Box2D box) {
        // call generic method for computing clipped boundary
        BoundarySet2D<?> boundary = 
            Boundary2DUtils.clipBoundary(this.getBoundary(), box);
        
        // convert boundary to list of rings
        ArrayList<Ring2D> boundaries = new ArrayList<Ring2D>(
                boundary.getCurveNumber());
        for (ContinuousBoundary2D curve : boundary.getBoundaryCurves())
            boundaries.add((Ring2D) curve);
        
        // create new MultiPolygon with the set of rings
        return new MultiPolygon2D(boundaries);
    }

    public double getDistance(java.awt.geom.Point2D p) {
        return Math.max(this.getBoundary().getSignedDistance(p), 0);
    }

    public double getDistance(double x, double y) {
        return Math.max(this.getBoundary().getSignedDistance(x, y), 0);
    }

    public boolean isBounded() {
        // If boundary is not bounded, the polygon is not
        Boundary2D boundary = this.getBoundary();
        if (!boundary.isBounded())
            return false;

        // Computes the signed area
        double area = 0;
        for (Ring2D ring : rings)
            area += ring.getSignedArea();

        // bounded if positive area
        return area>0;
    }

    /**
     * The MultiPolygon2D is empty either if it contains no ring, or if all
     * rings are empty.
     */
    public boolean isEmpty() {
        // return true if at least one ring is not empty
        for (Ring2D ring : rings)
            if (!ring.isEmpty())
                return false;
        return true;
    }

    public MultiPolygon2D transform(AffineTransform2D trans) {
        // allocate memory for transformed rings
        ArrayList<Ring2D> transformed = 
            new ArrayList<Ring2D>(rings.size());
        
        // trasnform each ring
        for (Ring2D ring : rings)
            transformed.add(ring.transform(trans));
        
        // creates a new MultiPolygon2D with the set of trasnformed rings
        return new MultiPolygon2D(transformed);
    }

    public boolean contains(java.awt.geom.Point2D point) {
        double angle = 0;
        for (Ring2D ring : this.rings)
            angle += ring.getWindingAngle(point);
        return angle>Math.PI;
    }

    public boolean contains(double x, double y) {
        return this.contains(new math.geom2d.Point2D(x, y));
    }

    public void draw(Graphics2D g2) {
        g2.draw(this.getBoundary().getGeneralPath());
    }

    public void fill(Graphics2D g) {
        g.fill(this.getBoundary().getGeneralPath());
    }
    
    public boolean equals(Object obj) {
        if(!(obj instanceof MultiPolygon2D))
            return false;
        
        // check if the two objects have same number of rings
        MultiPolygon2D polygon = (MultiPolygon2D) obj;
        if(polygon.rings.size()!=this.rings.size()) 
            return false;
        
        // check each couple of ring
        for(int i=0; i<rings.size(); i++)
            if(!this.rings.get(i).equals(polygon.rings.get(i)))
                return false;
        
        return true;
    }
   
    public MultiPolygon2D clone() {
        // allocate memory for new ring array
        ArrayList<Ring2D> array = new ArrayList<Ring2D>(rings.size());
        
        // clone each ring
        for(Ring2D ring : rings)
            array.add(ring.clone());
        
        // create a new polygon with cloned rings
        return new MultiPolygon2D(array);
    }
}
