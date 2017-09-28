package com.github.pathikrit.scalgos

import scala.util.Random._

import Implicits.Crossable
import Geometry.Point
import LinearAlgebra.Matrix2D

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
   * @return Random list of numbers in [min, max] of size = length
   */
  def list(length: Int = 100, min: Int = -10, max: Int = 10) = List.fill(length)(integer(min, max))

  /**
   * @return Random IndexedSeq of numbers in [min, max] of size = length
   */
  def seq(length: Int = 100, min: Int = -10, max: Int = 10) = IndexedSeq.fill(length)(integer(min, max))

  /**
   * @return Random list of numbers in [0, max] of size = length
   */
  def positiveList(length: Int = 100, max: Int = 10) = list(length, 0, max)

  /**
   * @return Atmost howMany unique points in in rectangle (minX, minY) - (maxX, maxY)
   */
  def points(minX: Int = -10, minY: Int = -10, maxX: Int = 10, maxY: Int = 10, howMany: Int = 100) =
    {for (i <- 1 to howMany) yield new Point((number(minX, maxX), number(minY, maxY)))}.toSet

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
      (i, j) <- g.vertices X g.vertices if i != j && number() < edgeDensity
    } g(i->j) = number(if (isPositiveEdges) 0 else -10, 10)

    g
  }

  /**
   * Some special trivial graphs
   */
  object Graphs {
    def zero = graph(numberOfVertices = 0)
    def one = graph(numberOfVertices = 1)
    def two = graph(numberOfVertices = 2, edgeDensity = 1)
    def noEdges = graph(edgeDensity = 0)
    def clique = graph(edgeDensity = 1)
  }

  /**
   * Generate random matrix
   *
   * @param r rows
   * @param c columns
   * @return a random matrix with r rows and c columns
   */
  def matrix(r: Int, c: Int): Matrix2D = Array.tabulate(r, c){(i, j) => number(-100, 100)}

  object Matrices {
    def square = matrix(10, 10)
    def rectangle = matrix(15, 20)
  }
}
