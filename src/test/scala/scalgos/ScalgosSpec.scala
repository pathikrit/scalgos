package scalgos

import org.specs2.mutable._
import util.Random._
import scalgos.Geometry.Point
import util.Random

class ScalgosSpec extends Specification {

  def todo(msg: String) = msg in { failure }.pendingUntilFixed

  def randomInteger(start: Int = 0, end: Int) = {
    assume(end > start)
    start + nextInt(end - start + 1)
  }

  def randomNumber(start: Double = 0, end: Double) = {
    assume(end > start)
    start + (end - start)*nextDouble()
  }

  def randomSeq(number: Int, start: Int, end: Int) = Seq.fill(number)(randomInteger(start, end))

  def randomPoints(minX: Int, minY: Int, maxX: Int, maxY: Int, howMany: Int = 1) =
    (for (i <- 1 to howMany) yield Point(randomInteger(minX, maxX), randomInteger(minY, maxY))).toSet

  def randomGraph(numVertices: Int, edgeDensity: Double, isPositiveEdges: Boolean = true, isDirected: Boolean = true) = {
    assume(numVertices >= 0)
    assume(edgeDensity >= 0 && edgeDensity <= 1)

    val g = new Graph[Double](numVertices, isDirected)

    val lowestEdge = if (isPositiveEdges) -100 else 0

    for {
      i <- g.vertices
      j <- g.vertices
      if (Random.nextDouble < edgeDensity)
    } g(i, j) = randomNumber(lowestEdge, 100)

    g
  }

}
