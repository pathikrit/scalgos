package scalgos

import collection.mutable

/**
 * A semi mutable graph representation
 * Can add/remove/update edges but cannot add/remove vertices
 *
 * @param numberOfVertices Number of vertices in graph
 * @param isDirected true iff a directed graph
 * @tparam E represents edge info e.g. for weighted graphs E is Double
 */
class Graph[E](val numberOfVertices: Int, val isDirected: Boolean = true) {

  private val adjacencyList = Array.fill(numberOfVertices)(mutable.Map.empty[Int, E])

  def apply(from: Int, to: Int) = adjacencyList(from) get to

  def neighbours(from: Int) = adjacencyList(from).keySet

  def update(from: Int, to: Int, edge: E) {
    adjacencyList(from)(to) = edge
    if (!isDirected) {
      adjacencyList(to)(from) = edge
    }
  }

  def vertices = adjacencyList.indices
}

/**
 * Collection of graph algorithms
 */
object Graph {

  type WeightedGraph = Graph[Double]
  type UnweightedGraph = Graph[Boolean]

  def dijkstra(g: WeightedGraph, start: Int, end: Int) =
    AStar.run[Int](start, _ == end, g.neighbours, (i, j) => g(i, j).get)

  /**
   * Run Floyd-Warshall all pair shortest path algorithm on g
   * O(V&#94;3)
   * To detect negative cycles, call once more and see if anything changes
   *
   *@param g input graph
   * @return the minimum distance graph of g i.e. f(x,y) = minimum distance between x and y
   */
  def floydWarshall(g: WeightedGraph) = {
    val f = new WeightedGraph(g.vertices.size)
    for {
      k <- g.vertices
      i <- g.vertices
      if g(i, k).isDefined
      j <- g.vertices
      if g(k, j).isDefined
    } f(i, j) = f(i, j) getOrElse Double.PositiveInfinity min (g(i, k).get + g(k, i).get)
    f
  }


}

