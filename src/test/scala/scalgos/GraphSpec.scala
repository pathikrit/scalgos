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

    "may not work for negative edges" in {
      val g = new Graph(numberOfVertices = 4)
      g(0->1) = 5
      g(1->2) = 5
      g(0->2) = 1
      g(0->3) = 100
      g(3->1) = -200
      val Some(Result(distance, path)) = dijkstra(g, 0, 2)
      distance must be equalTo 1    // Dijkstra says shortest path is 1
      val (cost, paths) = bellmanFord(g, 0)
      cost(2) must be equalTo -95   // But, bellmanFord says its -95
    }

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
    "work for arbitrary input" in todo
    "work for no path from start to end" in todo
    "work when start is goal" in todo
    "handle negative weight cycles" in todo
    "match bellman-ford algorithm" in todo
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

  "bellman-ford" should {
    "work for empty graphs" in todo
    "work for graphs with 1 or 2 vertices" in todo
    "work for graphs with no edges" in todo
    "work for arbitrary input" in todo
    "work for no path from start to end" in todo
    "work when start is goal" in todo
    "handle negative weight cycles" in todo

    "match dijkstra for positive graphs" in {
      def trace(parents: Seq[Int], v: Int): Seq[Int] = if(parents(v) < 0) Seq(v) else (trace(parents, parents(v)) :+ v)

      val g = RandomData.graph()
      for {
        u <- g.vertices
        (distances, parents) = bellmanFord(g, u)
        v <- g.vertices
      } dijkstra(g, u, v) match {
        case None =>
          distances(v).isPosInfinity must beTrue
          // TODO: no path to parent or loop here

        case Some(Result(distance, path)) =>
          distances(v) must be ~(distance +/- 1e-9)
          trace(parents, v) must be equalTo path  // might be different paths with same cost - random chance of failure!
      }
    }
  }
}
