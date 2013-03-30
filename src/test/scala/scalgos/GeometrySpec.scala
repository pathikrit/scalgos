package scalgos

import org.specs2.mutable._

import scalgos.Geometry._

class GeometrySpec extends Specification {

  "graham scan" should {

    "fail for <3 points" in { failure }.pendingUntilFixed("TODO")

    "work for triangles" in { failure }.pendingUntilFixed("TODO")

    def randomGrahamScan = {
      import Randomizations.randomInteger
      def randomPoint = Point(randomInteger(1, 100), randomInteger(1, 100))
      val points = (for(i <- 1 to 100) yield randomPoint).toSet
      (points, grahamScan(points))
    }

    "include extremities" in {
      val (points, hull) = randomGrahamScan
      val extremities: Array[Point => Double] = Array(_.x, _.y, _.manhattan /*,p => (p.x - p.y).abs*/)
      val extremes = (extremities map {points minBy _}) ++ (extremities map {points maxBy _})
      "must have atleast 3 extremes" ! (extremes.size >= 3)
      "must include all extremes" ! (extremes forall hull.contains)
    }

    "work for circles" in { failure }.pendingUntilFixed("TODO")

    "must match jarvis march" in { failure }.pendingUntilFixed("TODO")
  }
}
