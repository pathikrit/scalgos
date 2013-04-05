package scalgos

import org.specs2.mutable.Specification

import util.Random

import scalgos.Graph._

class GraphSpec extends Specification {

  def randomGraph(numVertices: Int, edgeDensity: Double, isPositiveEdges: Boolean = true, isDirected: Boolean = true) = {
    assume(numVertices >= 0)
    assume(edgeDensity >= 0 && edgeDensity <= 1)

    val g = new Graph[Double](numVertices, isDirected)

    val lowestEdge = if (isPositiveEdges) -100 else 0

    for {
      i <- g.vertices
      j <- g.vertices
      if (Random.nextDouble < edgeDensity)
    } g(i, j) = Randomized.randomNumber(lowestEdge, 100)

    g
  }

  "dijkstra" should {
    "work for trivial/empty graphs" in { failure }.pendingUntilFixed("TODO")
    "work for trivial graphs" in { failure }.pendingUntilFixed("TODO")
    "work for graphs with no edges" in { failure }.pendingUntilFixed("TODO")
    "not work for negative edges" in { failure }.pendingUntilFixed("TODO")

    "match floyd-warshall" in { failure }.pendingUntilFixed("TODO")
  }

}
