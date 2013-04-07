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

  /**
   * Edge between points
   * @param points (from,to)
   * @return edge value (else 0 if from==to or +infinity if from and to has no edge)
   */
  def apply(points: EndPoints) = {
    val (u, v) = points
    adjacencyList(u) getOrElse (v, if (u == v) 0 else Double.PositiveInfinity)
  }

  /**
   * Check if edge exists
   * @param points (from,to)
   * @return true iff from->to edge exists
   */
  def has(points: EndPoints) = adjacencyList(points._1) contains (points._2)

  /**
   * All neighbors of a vertex
   *
   * @param u starting vertex
   * @return neighbors of u
   */
  def neighbours(u: Int) = adjacencyList(u).keySet

  /**
   * Update edges
   * To remove use -=
   *
   * @param points (from, to)
   * @param weight (from,to) weight=
   */
  def update(points: EndPoints, weight: Double) {
    val (u, v) = points
    adjacencyList(u)(v) = weight
    if (!isDirected) {
      adjacencyList(v)(u) = weight
    }
  }

  /**
   * Delete an edge between (from,to)
   * @param points (from,to)
   */
  def -=(points: EndPoints) {
    adjacencyList(points._1) -= points._2
  }

  /**
   * Iterate over vertices
   * @return Iterator over vertices in graph
   */
  def vertices = adjacencyList.indices

  /**
   * @return the adjacency matrix of this graph
   */
  def adjacencyMatrix = Array.tabulate[Double](numberOfVertices, numberOfVertices)((i, j) => this(i->j))
}

/**
 * Collection of graph algorithms
 */
object Graph {

  /**
   * Run Dijkstra's shortest path algorithm
   * Basically runs A* with heuristic=0
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
   *
   * @param g input graph
   * @return the minimum distance matrix of g i.e. f(x)(y) = minimum distance between x and y
   *         if x and y on negative weight cycle f(x)(y) = -infinity
   *         if x and y disconnected then f(x)(y) = +infinity
   */
  def floydWarshall(g: Graph) = {
    val f = g.adjacencyMatrix

    for (k <- g.vertices; i <- g.vertices; j <- g.vertices) {
      f(i)(j) = f(i)(j) min (f(i)(k) + f(k)(j))
    }

    // TODO: handle negative weight cycle f(i)(i) < 0

    f
  }
}

