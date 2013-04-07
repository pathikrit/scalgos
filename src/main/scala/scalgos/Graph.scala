package scalgos

import collection.mutable

/**
 * A semi mutable weighted graph representation using adjacency list
 * Can add/remove/update edges but cannot add/remove vertices
 *
 * @param numberOfVertices Number of vertices in graph
 * @param isDirected true iff a directed graph
 */
class Graph(val numberOfVertices: Int, val isDirected: Boolean = true) {

  private val adjacencyList = Array.fill(numberOfVertices)(mutable.Map.empty[Int, Double])

  private type EndPoints = Pair[Int, Int]

  def apply(points: EndPoints) = {
    val (u, v) = points
    adjacencyList(u) getOrElse (v, if (u == v) 0 else Double.PositiveInfinity)
  }

  def has(points: EndPoints) = adjacencyList(points._1) contains (points._2)

  def neighbours(u: Int) = adjacencyList(u).keySet

  def update(points: EndPoints, weight: Double) {
    val (u, v) = points
    adjacencyList(u)(v) = weight
    if (!isDirected) {
      adjacencyList(v)(u) = weight
    }
  }

  def vertices = adjacencyList.indices
}

/**
 * Collection of graph algorithms
 */
object Graph {

  /**
   * Run Dijkstra's shortest path algorithm
   * Basically runs A* with heuristic=0
   * O(TODO)
   *
   * @param g input graph
   * @param start starting vertex
   * @param goal end vertex
   * @return result of A* search
   */
  def dijkstra(g: Graph, start: Int, goal: Int) = new AStar[Int] {
    def neighbors(n: Int) = g neighbours n
    override def distance(from: Int, to: Int) = g(from -> to)
  } run (start, _ == goal)

  /**
   * Run Floyd-Warshall all pair shortest path algorithm on g
   * O(V&#94;3)
   * To detect negative cycles, call once more and see if anything changes
   *
   * @param g input graph
   * @return the minimum distance graph of g i.e. f(x,y) = minimum distance between x and y
   */
  def floydWarshall(g: Graph) = {
    val f = new Graph(g.vertices.size)

    for {
      i <- g.vertices
      j <- g.vertices
    } f(i->j) = g(i->j)

    for {
      k <- g.vertices
      i <- g.vertices
      j <- g.vertices
    } f(i->j) = f(i->j) min (f(i->k) + f(k->j))

    f
  }
}

