package scalgos

import scalgos.Graph._

class GraphSpec extends ScalgosSpec {

  "dijkstra" should {
    TODO("work for empty graphs")
    TODO("work for graphs with <4 vertices")
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
    TODO("work for empty graphs")
    TODO("work for graphs with <4 vertices")
    TODO("work for graphs with no edges")
    TODO("not work for negative edges")
    TODO("work for no path from start to end")
    TODO("work when start is goal")
    TODO("handle negative weight cycles")
  }

  "stronglyConnectedComponents" should {
    TODO("work for empty graphs")
    TODO("work for graphs with <4 vertices")
    TODO("work for graphs with no edges")
    TODO("work on a clique")

    "must match floyd-warshall" in {
      val g = randomGraph()
      val f = floydWarshall(g)

      def inCycle(u: Int, v: Int) = !f(u)(v).isPosInfinity && !f(v)(u).isPosInfinity

      val sccs = stronglyConnectedComponents(g)

      sccs foreach (scc => {
        for {
          u <- scc
          v <- g.vertices
        } if(scc contains v) {
          s"$u to $v must be in cycle" ! inCycle(u,v)
        } else {
          s"$u to $v must not be in cycle" ! !inCycle(u,v)
        }
      })

      (sccs.flatten.toSet.size) must be equalTo g.numberOfVertices
    }
  }
}
