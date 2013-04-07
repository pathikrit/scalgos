package scalgos

import org.specs2.mutable._

import util.Random._

import scalgos.Geometry.Point

class ScalgosSpec extends Specification {

  def TODO(msg: String) = msg in { failure }.pendingUntilFixed

  val EPSILON = 1e-9

  def randomInteger(start: Int = 0, end: Int = 100) = {
    assume(end > start)
    start + nextInt(end - start + 1)
  }

  def randomNumber(start: Double = 0, end: Double = 1) = {
    assume(end > start)
    start + (end - start)*nextDouble()
  }

  def randomSeq(number: Int = 100, min: Int = -10, max: Int = 10) = Seq.fill(number)(randomInteger(min, max))

  def randomPositiveSeq(number: Int = 100, max: Int = 10) = randomSeq(number, 0, max)

  def randomPoints(minX: Int = -10, minY: Int = -10, maxX: Int = 10, maxY: Int = 10, howMany: Int = 100) =
    (for (i <- 1 to howMany) yield Point(randomInteger(minX, maxX), randomInteger(minY, maxY))).toSet

  def randomGraph(numVertices: Int = 10, edgeDensity: Double = 0.25,
                  isPositiveEdges: Boolean = true, isDirected: Boolean = true) = {
    assume(numVertices >= 0)
    assume(edgeDensity >= 0 && edgeDensity <= 1)

    val g = new Graph(numVertices, isDirected)

    for {
      i <- g.vertices
      j <- g.vertices
      if i != j
      if (randomNumber() < edgeDensity)
    } g(i->j) = if (i == j) 0 else randomNumber(if (isPositiveEdges) 0 else -10, 10)

    g
  }

}
