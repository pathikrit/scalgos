package com.github.pathikrit.scalgos.games

import org.specs2.mutable.Specification

import Sudoku._

class SudokuSpec extends Specification {

  "solve" should {
    "fail on boards that are not 9x9" in todo
    "fail on boards that contain anything other than 0-9" in todo
    "false on invalid solved boards" in todo
    "false and input not modified on insolvable boards" in todo
    "solve a normal solution" in todo
    "find multiple solutions" in todo

    "work on the fully empty board" in {
      solve(Array.fill(9, 9)(0)) must beTrue
    }
  }
}
