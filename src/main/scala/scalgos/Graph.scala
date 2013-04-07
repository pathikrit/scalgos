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
  def adjacencyMatrix = Array.tabulate(numberOfVertices, numberOfVertices)((u, v) => this(u->v))
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
   * O(V*V*V)
   * TODO: handle negative weight cycle f(i)(i) < 0
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

    f
  }

  /**
   * Run Tarjan's strongly connected component algorithm in G
   * O(E + V) - each edge is examined once
   *          - each vertex is pushed/popped once
   * Trivially finds all cycles too
   * TODO: Return a DisjointSet?

   * @param g input graph
   * @return the set of strongly connected components
   *         either a set is of size 1 or for every pair of vertex u,v in each set v is reachable from u
   */
  def stronglyConnectedComponents(g: Graph) = {
    var count = 0
    val (index, lowLink) = (mutable.Map.empty[Int, Int], mutable.Map.empty[Int, Int])
    val stack = mutable.Stack[Int]()
    val inProcess = mutable.LinkedHashSet.empty[Int]

    def dfs(u: Int) {
      index(u) = count
      lowLink(u) = count
      stack push u
      inProcess += u
      count += 1

      g neighbours u foreach {v =>
        if(!(index contains v)) {
          dfs(v)
          lowLink(u) = lowLink(u) min lowLink(v)
        } else if (inProcess contains v) {
          // TODO: What happens when we always do this (i.e. if v has been processed?)
          // add test case to test importance of this block
          lowLink(u) = lowLink(u) min index(v)
        }
      }

      if (index(u) == lowLink(u)) {
       var v = -1
       do {
         v = stack.pop()
         inProcess -= v
       } while(u != v)
      }
    }

    for {
      u <- g.vertices
      if (!(index contains u))
    } dfs(u)

    lowLink groupBy {_._2} mapValues {_.keySet} values
  }
}

