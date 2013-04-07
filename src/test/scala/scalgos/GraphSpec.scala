package scalgos

import scalgos.Graph._

class GraphSpec extends ScalgosSpec {

  "dijkstra" should {
    TODO("work for trivial/empty graphs")
    TODO("work for trivial graphs")
    TODO("work for graphs with no edges")
    TODO("not work for negative edges")
    TODO("work for no path from start to end")
    TODO("work when start is goal")

    "match floyd-warshall" in {
      val g = randomGraph()
      val f = floydWarshall(g)
      for {
        i <- g.vertices
        j <- g.vertices
        d = dijkstra(g, i, j)
      } if (f(i->j) < Double.PositiveInfinity) {
        val Some(Result(goal, distance, path)) = d
        path.head must be equalTo i
        path.last must be equalTo j
        goal must be equalTo j
        distance must be ~(f(i->j) +/- EPSILON)
      } else {
        d must beNone
      }
    }
  }

}
