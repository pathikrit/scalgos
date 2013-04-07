package scalgos

import scalgos.Graph._

class GraphSpec extends ScalgosSpec {

  "dijkstra" should {
    "work for empty graphs" in todo
    "work for graphs with <4 vertices" in todo
    "work for graphs with no edges" in todo
    "not work for negative edges" in todo
    "work for no path from start to end" in todo
    "work when start is goal" in todo

    "match floyd-warshall" in {
      val g = randomGraph()
      val f = floydWarshall(g)
      for {
        i <- g.vertices
        j <- g.vertices
        d = dijkstra(g, i, j)
      } if (f(i)(j) isPosInfinity) {
        d must beNone
      } else {
        val Some(Result(distance, path)) = d
        path.head must be equalTo i
        path.last must be equalTo j
        distance must be ~ (f(i)(j) +/- EPSILON)
      }
    }
  }

  "floyd-warshall" should {
    "work for empty graphs" in todo
    "work for graphs with <4 vertices" in todo
    "work for graphs with no edges" in todo
    "not work for negative edges" in todo
    "work for no path from start to end" in todo
    "work when start is goal" in todo
    "handle negative weight cycles" in todo
  }

  "stronglyConnectedComponents" should {
    "work for empty graphs" in todo
    "work for graphs with <4 vertices" in todo
    "work for graphs with no edges" in todo
    "work on a clique" in todo

    "match floyd-warshall" in {
      val g = randomGraph()
      val f = floydWarshall(g)

      def inCycle(u: Int, v: Int) = !f(u)(v).isPosInfinity && !f(v)(u).isPosInfinity

      val sccs = stronglyConnectedComponents(g)

      sccs foreach (scc => for {u <- scc; v <- g.vertices}
        inCycle(u,v) must be equalTo (scc contains v)
       )

      (sccs.flatten.toSet.size) must be equalTo g.numberOfVertices
    }
  }
}
