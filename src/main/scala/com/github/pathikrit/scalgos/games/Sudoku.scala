package com.github.pathikrit.scalgos.games

import com.github.pathikrit.scalgos.Implicits.{BooleanExtensions, TraversableExtension}

/**
 * Sudoku related algorithms
 * @param board underlying board (0 for empty cells)
 */
case class Sudoku(board: IndexedSeq[IndexedSeq[Int]]) {
  private[this] val n = board.length
  private[this] val s = Math.sqrt(n).toInt.ensuring(i => i*i == n, "Size must be a perfect square")
  require(board.forall(_.length == n), s"Board must be ${n}x$n")

  def solve: Option[Sudoku] = solve(0)

  /**
   * Solve this board
   * O(average elementary branching factor ^ empty cells)
   *
   * @param cell current cell (0 at start, 81 at end)
   * @return Some(solved board) if solution exists; else None
   */
  private[Sudoku] def solve(cell: Int): Option[Sudoku] = (cell%n, cell/n) match {
    case (_, `n`) => isSolution then this
    case (r, c) if board(r)(c) > 0 => solve(cell + 1)
    case (r, c) =>
      def used(i: Int) = Seq(board(r)(i), board(i)(c), board(s*(r/s) + i/s)(s*(c/s) + i%s))
      def guess(x: Int) = (this(r, c) = x).solve(c + 1)
      1 to n diff (board.indices flatMap used) firstDefined guess
  }

  /**
   * @return New board with (r,c) set to i
   */
  def update(r: Int, c: Int, i: Int) = Sudoku(board.updated(r, board(r).updated(c, i)))

  /**
   * @return true iff board is in valid solved state
   */
  def isSolution = {
    val expected = (1 to n).toSet
    board.indices forall {i =>
      (board.indices map {c => board(i)(c)}).toSet == expected &&
      (board.indices map {r => board(r)(i)}).toSet == expected &&
      (board.indices map {j => board(s*(i/s) + j%s)(s*(i%s) + j/s)}).toSet == expected
    }
  }

  /**
   * @return pretty print sudoku board
   */
  override def toString = board grouped s map {_ map {_ grouped s map {_ mkString " "} mkString " | "} mkString "\n"} mkString s"\n${"-" * 11 mkString " "}\n"
}
