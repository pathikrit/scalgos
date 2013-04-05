package scalgos

import scalgos.Geometry._

class GeometrySpec extends ScalgosSpec {

  "grahamScan" should {

    TODO("fail for <3 points")

    TODO("work for triangles")

    "include extremities" in {
      val points = randomPoints()
      val hull = grahamScan(points)
      val extremities: Array[Point => Double] = Array(_.x, _.y, _.manhattan /*,p => (p.x - p.y).abs*/)
      val extremes = (extremities map {points minBy _}) ++ (extremities map {points maxBy _})
      "must have atleast 3 extremes" ! (extremes.size >= 3)
      "must include all extremes" ! (extremes forall hull.contains)
    }

    TODO("work for circles")

    TODO("match jarvis march")
  }
}
