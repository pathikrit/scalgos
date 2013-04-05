package scalgos

import scalgos.Graph._

class GraphSpec extends ScalgosSpec {

  "dijkstra" should {
    TODO("work for trivial/empty graphs")
    TODO("work for trivial graphs")
    TODO("work for graphs with no edges")
    TODO("not work for negative edges")

    "match floyd-warshall" in {
      val g = randomGraph()
      val f = floydWarshall(g)
      for {
        i <- g.vertices
        j <- g.vertices
      } if (g(i, j).isDefined) {
        val Some((distance, path)) = dijkstra(g, i, j)
        distance must be equalTo f(i, j).get
      }
    }.pendingUntilFixed
  }

}
