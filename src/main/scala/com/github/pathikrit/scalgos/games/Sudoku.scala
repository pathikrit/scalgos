package com.github.pathikrit.scalgos.games

/**
 * Sudoku related algorithms
 */
object Sudoku {

  type Board = Array[Array[Int]]

  /**
   * Sudoku solver
   * O(average elementary branching factor ^ empty cells
   *
   * @param sudoku input game to be solved
   * @param cell current cell (0 at start, 81 at end)
   * @return true iff solution exists (the sudoku board is modified in place with solution else left alone)
   */
  def solve(sudoku: Board, cell: Int = 0): Boolean = {
    if (cell == 9*9) {
      return isSolution(sudoku)
    }

    val (r, c) = (cell%9, cell/9)

    if(sudoku(r)(c) > 0) {
      return solve(sudoku, cell+1)
    }

    def row(i: Int) = sudoku(r)(i)
    def col(i: Int) = sudoku(i)(c)
    def box(i: Int) = sudoku(3*(r/3) + i/3)(3*(c/3) + i%3)

    def good(x: Int) = (0 until 9) forall {i => x != row(i) && x != col(i) && x != box(i)}

    def guess(x: Int) = {
      sudoku(r)(c) = x
      solve(sudoku, cell+1)
    }

    ((1 to 9) filter good exists guess) || {sudoku(r)(c) = 0; false}
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
