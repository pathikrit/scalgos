package scalgos

import org.specs2.mutable._

import scalgos.RandomData.Graphs
import scalgos.Implicits.Crossable
import scalgos.Graph._

class GraphSpec extends Specification {

  "dijkstra" should {
    "work for empty graphs" in todo
    "work for graphs with 1 or 2 vertices" in todo
    "work for graphs with no edges" in todo
    "not work for negative edges" in todo
    "work for no path from start to end" in todo
    "work when start is goal" in todo

    "match floyd-warshall" in {
      val g = RandomData.graph()
      val f = floydWarshall(g)
      for {
        (i, j) <- (g.vertices X g.vertices)
        d = dijkstra(g, i, j)
      } if (f(i)(j) isPosInfinity) {
        d must beNone
      } else {
        val Some(Result(distance, path)) = d
        (path.head -> path.last) must be equalTo (i -> j)
        distance must be ~ (f(i)(j) +/- 1e-9)
      }
    }
  }

  "floyd-warshall" should {
    "work for empty graphs" in todo
    "work for graphs with 1 or 2 vertices" in todo
    "work for graphs with no edges" in todo
    "not work for negative edges" in todo
    "work for no path from start to end" in todo
    "work when start is goal" in todo
    "handle negative weight cycles" in todo
  }

  "stronglyConnectedComponents" should {
    /**
     * Given a graph and its strongly connected components check if all vertices are covered and mutual exclusion
     */
    def checkCoverage(g: Graph, sccs: Seq[Set[Int]]) {
      for {
        (s1, s2) <- (sccs X sccs)
        if (s1 != s2)
      } s1.intersect(s2) must be empty

      sccs.flatten must containTheSameElementsAs(g.vertices)
    }

    "work for empty graphs" in {
      val g = Graphs.empty
      val sccs = stronglyConnectedComponents(g)
      checkCoverage(g, sccs)
      sccs must be empty
    }

    "work for graphs with 1 or 2 vertices" in todo

    "work for graphs with no edges" in {
      val g = Graphs.noEdges
      val sccs = stronglyConnectedComponents(g)
      checkCoverage(g, sccs)
      sccs must be length(g.numberOfVertices)
    }

    "work on a clique" in {
      val g = Graphs.clique
      val sccs = stronglyConnectedComponents(g)
      checkCoverage(g, sccs)
      sccs must be length(1)
    }

    "match floyd-warshall" in {
      val g = RandomData.graph(numberOfVertices = 20, edgeDensity = 0.1)
      val f = floydWarshall(g)

      def inCycle(u: Int, v: Int) = !f(u)(v).isPosInfinity && !f(v)(u).isPosInfinity

      def checkCycle(scc: Set[Int]) = for ((u, v) <- scc X g.vertices) (scc contains v) must be equalTo inCycle(u,v)

      val sccs = stronglyConnectedComponents(g)
      checkCoverage(g, sccs)
      sccs foreach checkCycle
    }
  }
}
