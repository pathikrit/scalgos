package com.github.pathikrit.scalgos.games

object Sudoku {

  type Board = Array[Array[Int]]

  def solve(sudoku: Board, cell: Int = 0): Option[Board] = {
    if (cell == 9*9) {
      return if (isValid(sudoku)) Some(sudoku) else None
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

    val solutions = (1 to 9) filter good flatMap guess

    if (solutions isEmpty) {
      sudoku(r)(c) = 0
      None
    } else {
      Some(solutions.head)
    }
  }

  def isValid(sudoku: Board) = true

}
