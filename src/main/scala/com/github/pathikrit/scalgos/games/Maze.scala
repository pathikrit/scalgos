package com.github.pathikrit.scalgos.games

import scala.util.Try

/**
 * Generic idea to solve maze style problems
 */
object Maze {

  implicit class Grid[A](a: Array[Array[A]]) {
    def apply(x: Int, y: Int) = Try(a(x)(y)).toOption
  }

  def explore[A](g: Array[Array[A]]) = for {
    x <- g.indices
    y <- g(x).indices
    dx <- -1 to 1 by 2
    dy <- -1 to 1 by 2
    i <- 1 to 1     // increase i for rook style
    v <- g.apply(x + i*dx, y + i*dy)
  } yield v
}
