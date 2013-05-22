package com.github.pathikrit.scalgos

import collection.mutable

/**
 * A semi mutable weighted graph representation using adjacency list
 * Can add/remove/update edges but cannot add/remove vertices
 *
 * @param numberOfVertices Number of vertices in graph
 * @param isDirected true iff a directed graph
 */
class Graph(val numberOfVertices: Int, val isDirected: Boolean = true) {

  private val adjacencyList = Array.fill(numberOfVertices)(mutable.Map.empty[Int, Double] withDefaultValue Double.PositiveInfinity)

  private type EndPoints = Pair[Int, Int]

  private implicit class Edge(points: EndPoints) {
    val (u, v) = points
    assume(hasVertices(u, v))
  }

  /**
   * Edge between points
   * This is more readable alternative to traditional g(u,v) i.e. g(u->v)
   *
   * @param points (from,to)
   * @return edge value (else 0 if from==to or +infinity if from and to has no edge)
   */
  def apply(points: EndPoints) = if (points.u == points.v) 0.0 else adjacencyList(points.u)(points.v)

  /**
   * Check if edge exists
   * @param points (from,to)
   * @return true iff from->to edge exists
   */
  def has(points: EndPoints) = adjacencyList(points.u) contains (points.v)

  /**
   * @return true iff all vertices in graph
   */
  def hasVertices(vs: Int*) = vs forall vertices.contains

  /**
   * @return neighbors of u
   */
  def neighbours(u: Int) = adjacencyList(u).keySet

  /**
   * Update edges
   * To remove use -=
   *
   * @param points (from, to)
   * @param weight (from,to) = weight
   */
  def update(points: EndPoints, weight: Double) {
    adjacencyList(points.u)(points.v) = weight
    if (!isDirected) {
      adjacencyList(points.v)(points.u) = weight
    }
  }

  /**
   * Delete an edge between (from,to)
   * @param points (from,to)
   */
  def -=(points: EndPoints) {
    adjacencyList(points.u) -= points.v
    if (!isDirected) {
      adjacencyList(points.v) -= (points.u)
    }
  }

  /**
   * @return vertices in graph
   */
  def vertices = adjacencyList.indices

  /**
   * @return edges in graph
   */
  def edges = for (u <- vertices; v <- neighbours(u)) yield u->v

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
  def dijkstra(g: Graph, start: Int, goal: Int) = {
    assume(g hasVertices (start, goal))
    new AStar[Int] {
      def neighbors(n: Int) = g neighbours n
      override def distance(from: Int, to: Int) = g(from -> to)
    } run (start, _ == goal)
  }

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
   * TODO: http://apps.topcoder.com/forums/?module=Thread&threadID=785825&mc=1#1714843
   *
   * @param g input graph
   * @return the set of strongly connected components
   *         either a set is of size 1 or for every pair of vertex u,v in each set v is reachable from u
   */
  def stronglyConnectedComponents(g: Graph) = {
    var count = 0
    val (index, lowLink) = (mutable.Map.empty[Int, Int], mutable.Map.empty[Int, Int])
    val stack = mutable.Stack[Int]()         //TODO: try empty here
    val inProcess = mutable.LinkedHashSet.empty[Int]
    val sccs = mutable.Queue.empty[Set[Int]]

    def dfs(u: Int) {
      index(u) = count        // set u.index to lowest unused count
      lowLink(u) = count
      stack push u
      inProcess += u
      count += 1

      g neighbours u foreach {v =>
        if(!(index contains v)) {
          dfs(v)
          lowLink(u) = lowLink(u) min lowLink(v)
        } else if (inProcess contains v) {
          lowLink(u) = lowLink(u) min index(v)
        }
      }

      if (index(u) == lowLink(u)) {
       var v = -1
       val scc = mutable.Set.empty[Int]
       do {
         v = stack.pop()
         inProcess -= v
         scc += v
       } while(u != v)
       sccs += scc.toSet
      }
    }

    //todo: g.vertices filterNot index.contains foreach dfs
    for {
      u <- g.vertices
      if (!(index contains u))
    } dfs(u)
    sccs.toSeq
  }

  /**
   * Run Bellman-Ford algorithm for finding
   * Handles negative weights
   * TODO: Negative weight cycle?
   * TODO: Improvements
   * TODO: Proof of correctness
   * TODO: Why does parent work for all vertices? can there be cycles in parent?
   * O(VE)
   *
   * @param g input graph
   * @param source starting vertex
   * @return (d, p) where d(i) is shortest distance from source to i
   *                if negative cycle then d(i) is negative infinity
   *         and p(j) = parent of vertex j - follow back to source for path
   *                    should either end in -1 or a loop if d(i) is positive infinity
   */
  def bellmanFord(g: Graph, source: Int) = {
    val distance = Array.tabulate(g.numberOfVertices)(i => g(source->i))
    val parent = Array.fill(g.numberOfVertices)(-1)

    for {
      i <- 1 until g.numberOfVertices
      (u,v) <- g.edges
      if distance(v) >= distance(u) + g(u->v)
    } {
      distance(v) = distance(u) + g(u->v)
      parent(v) = u
    }

    (distance.toSeq, parent.toSeq)
  }


  /**
   * Breadth first search from source in g
   * If we replace queue with stack, we get DFS
   * O(V + E) - each vertex/edge is examined atmost once
   *
   * @param f Apply f to each vertex in bfs order from source
   * @return If f is true at a vertex v, return Some(v) else None
   */
  def bfs(g: Graph, source: Int, f: Int => Boolean): Option[Int] = {
    val (seen, queue) = (mutable.Set.empty[Int], mutable.Queue.empty[Int])

    def visit(i: Int) = {
      seen += i
      queue += i
    }

    visit(source)

    while (!queue.isEmpty) {
      val u = queue.dequeue()
      if (f(u)) {
        return Some(u)
      }
      g neighbours u filterNot seen foreach visit
    }

    None
  }

  /**
   * Recursive depth first search from u in g
   * TODO: Change BFS, DFS to cost too
   * O(V + E) - each vertex/edge is examined atmost once
   *
   * @param f Apply f to each vertex in dfs order from source
   * @return If f is true at a vertex v, return Some(v) else None
   */
  def dfs(g: Graph, u: Int, f: Int => Boolean, seen: Set[Int] = Set.empty[Int]): Option[Int] = {
    if(f(u)) {
      Some(u)
    } else {
      g neighbours u filterNot seen find {v => dfs(g, u, f, seen + u).isDefined}
    }
  }
}

