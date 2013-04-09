package scalgos

import util.Random._

import scalgos.Geometry.Point

/**
 * Has utility methods for random data generation
 */
object RandomData {

  /**
   * @return Random Integer in [start, end]
   */
  def integer(start: Int = 0, end: Int = 100) = {
    assume(end > start)
    start + nextInt(end - start + 1)
  }

  /**
   * @return Random Double in [start, end]
   */
  def number(start: Double = 0, end: Double = 1) = {
    assume(end > start)
    start + (end - start)*nextDouble()
  }

  /**
   * @return Random sequence of numbers in [min, max] of size = length
   */
  def seq(length: Int = 100, min: Int = -10, max: Int = 10) = Seq.fill(length)(integer(min, max))

  /**
   * @return Random sequence of numbers in [0, max] of size = length
   */
  def positiveSeq(length: Int = 100, max: Int = 10) = seq(length, 0, max)

  /**
   * @return Atmost howMany unique points in in rectangle (minX, minY) - (maxX, maxY)
   */
  def points(minX: Int = -10, minY: Int = -10, maxX: Int = 10, maxY: Int = 10, howMany: Int = 100) =
    (for (i <- 1 to howMany) yield Point(integer(minX, maxX), integer(minY, maxY))).toSet

  /**
   * Generate random graph
   *
   * @param numberOfVertices number of vertices in graph
   * @param edgeDensity number of edges = edge_density * v*v/2
   * @param isPositiveEdges if edges must be positive
   * @param isDirected true iff graph must be directed
   * @return a random graph
   */
  def graph(numberOfVertices: Int = 100, edgeDensity: Double = 0.25,
                  isPositiveEdges: Boolean = true, isDirected: Boolean = true) = {
    assume(numberOfVertices >= 0)
    assume(edgeDensity >= 0 && edgeDensity <= 1)

    val g = new Graph(numberOfVertices, isDirected)

    for {
      i <- g.vertices
      j <- g.vertices
      if i != j
      if (number() < edgeDensity)
    } g(i->j) = number(if (isPositiveEdges) 0 else -10, 10)

    g
  }

  /**
   * Some special trivial graphs
   */
  object Graphs {
    def empty = graph(numberOfVertices = 0)
    def point = graph(numberOfVertices = 1)
    def line = graph(numberOfVertices = 2, edgeDensity = 1)
    def noEdges = graph(edgeDensity = 0)
    def clique = graph(edgeDensity = 1)
  }
}
