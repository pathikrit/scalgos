package scalgos

import util.Random

import scalgos.Graph._

class GraphSpec extends ScalgosSpec {

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
    todo("work for trivial/empty graphs")
    todo("work for trivial graphs")
    todo("work for graphs with no edges")
    todo("not work for negative edges")
    todo("match floyd-warshall")
  }

}
