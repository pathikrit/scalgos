package scalgos

import collection.mutable
import java.awt.geom.GeneralPath

/**
 * Collection of geometrical algorithms
 */
object Geometry {

  /**
   * Represents a point
   * @param x x-coordinate
   * @param y y-coordinate
   */
  case class Point(x: Double, y: Double) {
    def manhattan = x+y
  }

  /**
   * Represents a vector between start and end
   * @param A start
   * @param B end
   */
  case class Vector(A: Point, B: Point) {
    def X(C: Point) = crossProduct(A, B, C)
  }

  /**
   * Represents a 2D shape or polygon
   * A simple wrapper around Java's GeneralPath class
   *
   * @param points points on the shape
   */
  case class Shape(points: Set[Point]) {
    private val polygon = new GeneralPath()

    polygon moveTo (points.head.x, points.head.y)
    points.tail foreach {p => polygon lineTo (p.x, p.y)}
    polygon.closePath()

    def contains(p: Point) = polygon contains (p.x, p.y)
  }

  implicit def toPoint(tuple: (Double, Double)) = Point(tuple._1, tuple._2)

  /**
   * Cross product of Segment(a, b) and Segment(a, c)
   * Signed area of triangle formed by (a,b,c) i.e. if 0 then collinear
   * if <0 counter-clockwise turn from (a,b) to (a,c) and vice-versa
   * determinant | a.x  a.y  a.z |
   *             | b.x  b.y  b.z |
   *             | c.x  c.y  c.z |
   *
   * @param a first point
   * @param b second point
   * @param c third point
   * @return cross product of segment(a,b) and segment(a,c)
   */
  def crossProduct(a: Point, b: Point, c: Point) = (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y)

  /**
   * Check if 2 segments intersect
   * TODO: proof?
   *
   * @param i first segment
   * @param j second segment
   * @return true iff first and second intersects
   */
  def intersects(i: Vector, j: Vector) = (i X j.A) * (i X j.B) <= 0 && (j X i.A) * (j X i.B) <= 0

  /**
   * Finds convex hull using Graham Scan
   * O(n log n) because of the sortBy
   * Else each point is pushed/popped atmost twice (one each for reverse) in constant time in halfHull
   *
   * @param points input points
   * @return points on the convex hull of the given points (including collinear points)
   */
  def grahamScan(points: Set[Point]) = {
    assume(points.size >= 3)
    type Hull = mutable.ArrayStack[Point]

    /**
     * Discard points in interior of the quadrilateral formed by top, left, bottom, right
     * @param points set of input points
     * @return sorted remaining points
     */
    def aklToussaintHeuristic(points: Set[Point]) = {
      def extremities: Array[Point => Double] = Array(_.x, -_.x, _.y, -_.y)
      val extremes = extremities map {points minBy _}
      val quad = Shape(extremes.toSet)
      ((points filterNot quad.contains) ++ extremes).toSeq.sortBy(p => (p.x, p.y))
    }

    def turnLeft(h: Hull, p: Point) = {
      while(h.size > 1 && crossProduct(h(1), h(0), p) < 0) { // if crossProduct = 0, collinear, if > 0 "right turn"
        h pop
      }
      h push p
      h
    }

    def halfHull(points: Seq[Point]) = points.foldLeft(new Hull)(turnLeft)
    val sorted = aklToussaintHeuristic(points)
    (halfHull(sorted) ++ halfHull(sorted.reverse)).toSet
  }
}
