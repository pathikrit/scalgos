package com.github.pathikrit.scalgos

import collection.mutable
import java.lang.Math._

/**
 * Collection of geometrical algorithms
 */
object Geometry {

  /**
   * Represents a point (x,y)
   */
  implicit class Point(tuple: (Double, Double)) {
    val (x,y) = tuple
    def manhattan = x+y
  }

  /**
   * Represents a vector between a and b
   */
  implicit class Vector(ends: (Point, Point)) {
    val (a,b) = ends
    def X(c: Point) = crossProduct(a, b, c)
    def length = sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y))
  }

  /**
   * Represents a 2D shape or polygon
   * A simple wrapper around Java's GeneralPath class
   *
   * @param points points on the shape
   */
  case class Shape(points: Set[Point]) {
    private val polygon = new java.awt.geom.GeneralPath()

    polygon moveTo (points.head.x, points.head.y)
    points.tail foreach {p => polygon lineTo (p.x, p.y)}
    polygon.closePath()

    def contains(p: Point) = polygon contains (p.x, p.y)
  }

  /**
   * Cross product of Segment(a, b) and Segment(a, c)
   * Twice the signed area of triangle formed by (a,b,c) i.e. if 0 then collinear
   * if <0 counter-clockwise turn from (a,b) to (a,c) and vice-versa
   * determinant | a.x  a.y  a.z |
   *             | b.x  b.y  b.z |
   *             | c.x  c.y  c.z |
   * @return cross product of segment(a,b) and segment(a,c)
   */
  def crossProduct(a: Point, b: Point, c: Point) = (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y)

  /**
   * @return Area of triangle a,b,c
   */
  def areaOfTriangle(a: Point, b: Point, c: Point) = crossProduct(a,b,c).abs/2

  /**
   * Check if 2 segments intersect
   * TODO: proof?
   * @return true iff i and j intersects
   */
  def intersects(i: Vector, j: Vector) = (i X j.a) * (i X j.b) <= 0 && (j X i.a) * (j X i.b) <= 0

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
     * Akl-Touissant Heuristic: Discard points in interior of the quadrilateral formed by top, left, bottom, right
     * @param points set of input points
     * @return sorted remaining points
     */
    def discardInteriorPoints(points: Set[Point]) = {
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
    val sorted = discardInteriorPoints(points)
    (halfHull(sorted) ++ halfHull(sorted.reverse)).toSet
  }
}
