package com.github.pathikrit.scalgos.games

/**
 * Sudoku related algorithms
 * @param board underlying board (0 for empty cells)
 */
case class Sudoku(board: IndexedSeq[IndexedSeq[Int]]) {
  /**
   * Solve this board
   * O(average elementary branching factor ^ empty cells)
   *
   * @param cell current cell (0 at start, 81 at end)
   * @return Some(solved board) if solution exists; else None
   */
  def solve(cell: Int = 0): Option[Sudoku] = (cell%9, cell/9) match {
    case (r, 9) => Some(this)
    case (r, c) if board(r)(c) > 0 => solve(cell + 1)
    case (r, c) =>
      def cells(i: Int) = board(r)(i) :: board(i)(c) :: board(3*(r/3) + i/3)(3*(c/3) + i%3) :: Nil
      val used = 0 until 9 flatMap cells
      1 to 9 diff used collectFirst Function.unlift(update(r, c, _).solve(cell+1))
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
  override def toString = board grouped 3 map {_ map {_ grouped 3 map {_ mkString " "} mkString " | "} mkString "\n"} mkString s"\n${"-" * 11 mkString " "}\n"
}
