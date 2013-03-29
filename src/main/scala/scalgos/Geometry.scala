package scalgos

object Geometry {

  case class Point(x: Double, y: Double)
  implicit def toPoint(tuple: (Double, Double)) = Point(tuple._1, tuple._2)

  type Segment = (Point, Point)

  def vectorProduct(p0: Point, p1: Point, p2: Point) = (p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y)

  def intersects(a: Segment, b: Segment) =
    vectorProduct(a._1, a._2, b._1) * vectorProduct(a._1, a._2, b._2) <= 0 &&
    vectorProduct(b._1, b._2, a._1) * vectorProduct(b._1, b._2, a._2) <= 0

}
