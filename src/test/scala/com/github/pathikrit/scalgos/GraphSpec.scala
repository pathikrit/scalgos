package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import RandomData.Graphs
import Graph._
import Implicits._

class GraphSpec extends Specification {

  "graph" should {
    "work" in todo
  }

  "dijkstra" should {
    "work for empty graphs" in {
      val g = Graphs.zero
      dijkstra(g, 0, 1) must throwA[AssertionError]
      dijkstra(g, 0, 0) must throwA[AssertionError]
    }

    "work for graphs with 1 or 2 vertices" in todo

    "work for graphs with no edges" in {
      val g = Graphs.noEdges
      examplesBlock {
        for {
          (u, v) <- g.vertices X g.vertices
        } dijkstra(g, u, v) match {
          case Some(Result(cost, path)) =>
            cost must be equalTo 0
            u must be equalTo v

          case None =>
            u mustNotEqual v
        }
      }
    }

    "may not work for negative edges" in {
      val g = new Graph[Double](numberOfVertices = 4)
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
      examplesBlock {
        for {
          (i, j) <- g.vertices X g.vertices
          d = dijkstra(g, i, j)
        } if (f(i)(j) isPosInfinity) {
          d must beNone
        } else {
          val Some(Result(distance, path)) = d
          (path.head -> path.last) must be equalTo (i -> j)
          distance must be ~(f(i)(j) +/- 1e-9)
        }
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

  "minimum-spanning-tree" should {

    "kruskalsMst" should {
      "work for graphs with 0, 1 or 2 vertices" in todo
      "work for graphs with no edges" in todo
      "fail for disjoint graphs" in todo
      "fail for directed graphs" in todo
    }

    "primsMst" should {
      "work for graphs with 0, 1 or 2 vertices" in todo
      "work for graphs with no edges" in todo
      "fail for disjoint graphs" in todo
      "fail for directed graphs" in todo
    }

    "prims match kruskals" in {
      val g = RandomData.graph(numberOfVertices = 100, edgeDensity = 1, isPositiveEdges = true, isDirected = false)
      def cost(edges: Set[(Int, Int)]) = (edges map g.apply).sum
      val (p, k) = (primsMst(g), kruskalsMst(g))
      cost(p) mustEqual cost(k)
      // todo: check for cycle, coverage, vertex incidence etc
    }
  }

  "stronglyConnectedComponents" should {
    /**
      * Given a graph and its strongly connected components check if all vertices are covered and mutual exclusion
      */
    def checkCoverage(g: Graph[Double], sccs: Seq[Set[Int]]) = {
      for {
        (s1, s2) <- sccs X sccs if s1 != s2
      } s1.intersect(s2) must be empty

      sccs.flatten must containTheSameElementsAs(g.vertices)
    }

    "work for empty graphs" in {
      val g = Graphs.zero
      val components = stronglyConnectedComponents(g)
      checkCoverage(g, components)
      components must beEmpty
    }

    "work for graphs with 1 or 2 vertices" in todo

    "work for graphs with no edges" in {
      val g = Graphs.noEdges
      val components = stronglyConnectedComponents(g)
      checkCoverage(g, components)
      components must be length g.numberOfVertices
    }

    "work on a clique" in {
      val g = Graphs.clique
      val components = stronglyConnectedComponents(g)
      checkCoverage(g, components)
      components must be length 1
    }

    "match floyd-warshall" in {
      val g = RandomData.graph(numberOfVertices = 20, edgeDensity = 0.1)
      val f = floydWarshall(g)

      def inCycle(u: Int, v: Int) = !f(u)(v).isPosInfinity && !f(v)(u).isPosInfinity

      def checkCycle(scc: Set[Int]) = for ((u, v) <- scc X g.vertices) (scc contains v) must be equalTo inCycle(u,v)

      val components = stronglyConnectedComponents(g)
      checkCoverage(g, components)
      examplesBlock {components foreach checkCycle}
    }
  }

  "bellman-ford" should {
    "work for empty graphs" in todo
    "work for graphs with 1 or 2 vertices" in todo
    "work for graphs with no edges" in todo
    "work for arbitrary input" in todo

    "handle negative weight cycles" in todo

    "match dijkstra for positive graphs" in {
      def trace(parents: Seq[Int], v: Int): Seq[Int] = if(parents(v) < 0) Seq(v) else trace(parents, parents(v)) :+ v

      val g = RandomData.graph()
      examplesBlock {
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

  "max-flow" should {
    "work for empty graphs" in {
      val g = new Graph[Int](numberOfVertices = 0)
      maxFlow(g, 0, 0) should throwA[AssertionError]
    }

    "work for graphs with no edges" in {
      val g = new Graph[Int](numberOfVertices = 2)
      maxFlow(g, 0, 1)._1 must be equalTo 0
    }

    "calculate maximum flow correctly" in {
      val g = new Graph[Int](numberOfVertices = 7)
      g(0->1) = 3
      g(0->2) = 1
      g(2->3) = 5
      g(1->3) = 3
      g(2->4) = 4
      g(4->5) = 2
      g(5->6) = 3
      g(3->6) = 2
      val (maximumFlow, _) = maxFlow(g, 0, 6)
      maximumFlow must be equalTo 3
    }

    "calculate maximum flow correctly" in {
      val g = new Graph[Int](numberOfVertices = 6)
      g(0->1) = 4
      g(1->2) = 3
      g(2->3) = 2
      g(3->4) = 4
      g(0->5) = 2
      g(5->4) = 4
      g(4->0) = 3
      g(4->1) = 1
      g(2->4) = 1
      val (maximumFlow, _) = maxFlow(g, 0, 3)
      maximumFlow must be equalTo 2
    }
  }
}