package scalgos

import scalgos.Geometry._

class GeometrySpec extends ScalgosSpec {

  "grahamScan" should {

    "fail for <3 points" in todo

    "work for triangles" in todo

    "include extremities" in {
      val points = randomPoints()
      val hull = grahamScan(points)
      val extremities: Array[Point => Double] = Array(_.x, _.y, _.manhattan /*,p => (p.x - p.y).abs*/)
      val extremes = (extremities map {points minBy _}) ++ (extremities map {points maxBy _})
      extremes.size must be greaterThanOrEqualTo 3
      hull must containAllOf(extremes)
    }

    "work for circles" in todo

    "match jarvis march" in todo
  }
}
