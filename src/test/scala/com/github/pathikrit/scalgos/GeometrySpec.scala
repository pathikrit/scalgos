package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import java.lang.Math._

import Geometry._

class GeometrySpec extends Specification {

  "intersects" should {
    "false when parallel" in todo
    "true when same" in todo
    "true when only endpoints touch" in todo
    "false when barely apart" in todo
    "false when on same line but different segment" in todo
    "work for arbitrary case" in todo
  }

  "crossProduct" should {
    "be zero when collinear" in todo
    "match determinant value" in todo
  }

  "areaOfTriangle" should {

    "be zero when collinear" in todo

    "match heron's formula" in {
      def heronsFormula(a: Point, b: Point, c: Point) = {
        val (ab, bc, ca) = ((a->b).length, (b->c).length, (c->a).length)
        val s = (ab + bc + ca)/2
        sqrt(s*(s-ab)*(s-bc)*(s-ca))
      }

      def points = RandomData.points(howMany = 1).head
      val (a,b,c) = (points, points, points)
      areaOfTriangle(a, b, c) must be ~(heronsFormula(a, b, c) +/- 1e-9)
    }
  }

  "grahamScan" should {

    "fail for <3 points" in todo
    "work for triangles" in todo

    "include extremities" in {
      val points = RandomData.points()
      val hull = grahamScan(points)
      val extremities: Array[Point => Double] = Array(_.x, _.y, _.manhattan /*,p => (p.x - p.y).abs*/)
      val extremes = (extremities map {points minBy _}) ++ (extremities map {points maxBy _})
      extremes.size must be greaterThanOrEqualTo 3
      //todo: hull must contain(allOf(extremes))
      todo
    }

    "work for circles" in todo
    "match jarvis march" in todo
  }
}
