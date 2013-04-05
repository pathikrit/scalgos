package scalgos

import scalgos.Geometry._

class GeometrySpec extends ScalgosSpec {

  "grahamScan" should {

    todo("fail for <3 points")

    todo("work for triangles")

    "include extremities" in {
      val points = randomPoints(0, 0, 100, 100, 500)
      val hull = grahamScan(points)
      val extremities: Array[Point => Double] = Array(_.x, _.y, _.manhattan /*,p => (p.x - p.y).abs*/)
      val extremes = (extremities map {points minBy _}) ++ (extremities map {points maxBy _})
      "must have atleast 3 extremes" ! (extremes.size >= 3)
      "must include all extremes" ! (extremes forall hull.contains)
    }

    todo("work for circles")

    todo("match jarvis march")
  }
}
