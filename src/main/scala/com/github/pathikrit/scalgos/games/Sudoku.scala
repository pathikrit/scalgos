package com.github.pathikrit.scalgos.games

import com.github.pathikrit.scalgos.Implicits._

/**
 * Sudoku related algorithms
 * @param board underlying board (0 for empty cells)
 */
case class Sudoku(board: IndexedSeq[IndexedSeq[Int]]) {
  val n = board.length
  val s = Math.sqrt(n).toInt.ensuring(i => i*i == n, "Size must be a perfect square")
  require(board.forall(_.length == n), s"Board must be ${n}x$n")

  /**
   * Solve this board
   * O(average elementary branching factor ^ empty cells)
   *
   * @param cell current cell (0 at start, 81 at end)
   * @return Some(solved board) if solution exists; else None
   */
  def solve(cell: Int = 0): Option[Sudoku] = (cell%n, cell/n) match {
    case (r, `n`) => when(isSolution)(this)
    case (r, c) if board(r)(c) > 0 => solve(cell + 1)
    case (r, c) =>
      def cells(i: Int) = board(r)(i) :: board(i)(c) :: board(s*(r/s) + i/s)(s*(c/s) + i%s) :: Nil
      val used = 0 until n flatMap cells
      def guess(x: Int) = (this(r, c) = x).solve(c + 1)
      1 to n diff used firstDefined guess
  }

  /**
   * @return New board with (r,c) set to i
   */
  def update(r: Int, c: Int, i: Int) = Sudoku(board.updated(r, board(r).updated(c, i)))

  /**
   * @return true iff board is in valid solved state
   */
  def isSolution = true

  /**
   * @return pretty print sudoku board
   */
  override def toString = board grouped s map {_ map {_ grouped s map {_ mkString " "} mkString " | "} mkString "\n"} mkString s"\n${"-" * 11 mkString " "}\n"
}
