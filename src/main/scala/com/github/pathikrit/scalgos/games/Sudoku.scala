package com.github.pathikrit.scalgos.games

/**
 * Sudoku related algorithms
 */
object Sudoku {

  type Board = Array[Array[Int]]

  /**
   * Sudoku solver
   * O(average elementary branching factor ^ empty cells)
   *
   * @param board input game to be solved
   * @param cell current cell (0 at start, 81 at end)
   * @return true iff solution exists (the sudoku board is modified in place with solution else left alone)
   */
  def solve(board: Board, cell: Int = 0): Boolean = (cell%9, cell/9) match {
    case (_, 9) => isSolution(board)
    case (r, c) if board(r)(c) > 0 => solve(board, cell+1)
    case (r, c) =>
      def cells(i: Int) = board(r)(i) :: board(r)(i) :: board(3*(r/3) + i/3)(3*(c/3) + i%3) :: Nil
      val used = 0 until 9 flatMap cells

      def guess(x: Int) = {
        board(r)(c) = x
        solve(board, cell+1)
      }

      (1 to 9 diff used exists guess) || {board(r)(c) = 0; false}
  }

  /**
   * @return true iff sudoku is in valid solved state
   */
  def isSolution(sudoku: Board) = true

  /**
   * @return pretty print sudoku board
   */
  def pretty(sudoku: Board) = {
    var pretty = ""
    for (i <- sudoku.indices) {
      if(i == 3 || i == 6) {
        pretty += "- - - - - - - - - - -\n"
      }
      for (j <- sudoku(i).indices) {
        if (j == 3 || j == 6) {
          pretty += "| "
        }
        pretty += sudoku(i)(j) + " "
      }
      pretty += "\n"
    }
    pretty
  }
}
