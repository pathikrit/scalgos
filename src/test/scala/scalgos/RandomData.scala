package scalgos

import org.specs2.mutable._

import util.Random._

import scalgos.Geometry.Point

/**
 * All Specs must extend this
 * Has utility methods for random data generation
 * TODO: Create random data generation class?
 */
class RandomData extends Specification {

  /**
   * Tolerance for floating point matching
   * sample usage: `value must be ~(expected +/- EPSILON)`
   */
  val EPSILON = 1e-9

  /**
   * @return Random Integer in [start, end]
   */
  def randomInteger(start: Int = 0, end: Int = 100) = {
    assume(end > start)
    start + nextInt(end - start + 1)
  }

  /**
   * @return Random Double in [start, end]
   */
  def randomNumber(start: Double = 0, end: Double = 1) = {
    assume(end > start)
    start + (end - start)*nextDouble()
  }

  /**
   * @return Random sequence of numbers in [min, max] of size = length
   */
  def randomSeq(length: Int = 100, min: Int = -10, max: Int = 10) = Seq.fill(length)(randomInteger(min, max))

  /**
   * @return Random sequence of numbers in [0, max] of size = length
   */
  def randomPositiveSeq(length: Int = 100, max: Int = 10) = randomSeq(length, 0, max)

  /**
   * @return Atmost howMany unique points in in rectangle (minX, minY) - (maxX, maxY)
   */
  def randomPoints(minX: Int = -10, minY: Int = -10, maxX: Int = 10, maxY: Int = 10, howMany: Int = 100) =
    (for (i <- 1 to howMany) yield Point(randomInteger(minX, maxX), randomInteger(minY, maxY))).toSet

  /**
   * Generate random graph
   *
   * @param numberOfVertices number of vertices in graph
   * @param edgeDensity number of edges = edge_density * v*v/2
   * @param isPositiveEdges if edges must be positive
   * @param isDirected true iff graph must be directed
   * @return a random graph
   */
  def randomGraph(numberOfVertices: Int = 100, edgeDensity: Double = 0.25,
                  isPositiveEdges: Boolean = true, isDirected: Boolean = true) = {
    assume(numberOfVertices >= 0)
    assume(edgeDensity >= 0 && edgeDensity <= 1)

    val g = new Graph(numberOfVertices, isDirected)

    for {
      i <- g.vertices
      j <- g.vertices
      if i != j
      if (randomNumber() < edgeDensity)
    } g(i->j) = randomNumber(if (isPositiveEdges) 0 else -10, 10)

    g
  }

}
