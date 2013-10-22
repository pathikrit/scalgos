package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Implicits._

class ImplicitsSpec extends Specification {
  "FuzzyDouble" should {
    "work" in todo
  }

  "IntExtenshions" should {
    "have correct mod" in {
      examplesBlock {
        for {
          (x,y) <- (-100 to 100) X (-100 to 100)
          if y != 0
          m = x mod y
        } {
          ((x/y)*y + m) must be equalTo x
          (x-m)%y must be equalTo 0
        }
      }
    }
  }

  "ForwardPipe" should {
    "work" in todo
  }

  "RemovableList" should {
    "work" in todo
  }
}
