package com.github.pathikrit.scalgos.games

import org.specs2.mutable.Specification

class SudokuSpec extends Specification {

  "solve" should {
    "fail on boards that are not 9x9" in todo
    "fail on boards that contain anything other than 0-9" in todo
    "false on invalid solved boards" in todo
    "false and input not modified on insolvable boards" in todo

    "solve a normal solution" in {
      import scala.collection.{IndexedSeq => $}
      Sudoku(
        $(
          $(1, 0, 0, 0, 0, 7, 0, 9, 0),
          $(0, 3, 0, 0, 2, 0, 0, 0, 8),
          $(0, 0, 9, 6, 0, 0, 5, 0, 0),
          $(0, 0, 5, 3, 0, 0, 9, 0, 0),
          $(0, 1, 0, 0, 8, 0, 0, 0, 2),
          $(6, 0, 0, 0, 0, 4, 0, 0, 0),
          $(3, 0, 0, 0, 0, 0, 0, 1, 0),
          $(0, 4, 0, 0, 0, 0, 0, 0, 7),
          $(0, 0, 7, 0, 0, 0, 3, 0, 0)
        )
      ).solve.map(_.toString) must beSome("""
        |1 6 2 | 8 5 7 | 4 9 3
        |5 3 4 | 1 2 9 | 6 7 8
        |7 8 9 | 6 4 3 | 5 2 1
        |- - - - - - - - - - -
        |4 7 5 | 3 1 2 | 9 8 6
        |9 1 3 | 5 8 6 | 7 4 2
        |6 2 8 | 7 9 4 | 1 3 5
        |- - - - - - - - - - -
        |3 5 6 | 4 7 8 | 2 1 9
        |2 4 1 | 9 3 5 | 8 6 7
        |8 9 7 | 2 6 1 | 3 5 4 """.stripMargin.trim)
    }

    "find multiple solutions" in todo
    "work on the fully empty board" in todo
  }
}
