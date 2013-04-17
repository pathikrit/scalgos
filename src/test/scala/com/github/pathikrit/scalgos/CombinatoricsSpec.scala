package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Combinatorics._

class CombinatoricsSpec extends Specification {

  "combinations" should {
    "match the bitmask version" in todo

    "work for 0 or 1 length" in todo

    "work for arbitrary input" in {
      val s = Seq(1, 2, 3)
      val result = combinations(s, (i: Seq[Int]) => i mkString ("[", ",", "]"))
      result.length must be equalTo 1<<s.length
      result mkString " " must be equalTo "[] [1] [2] [3] [1,2] [1,3] [2,3] [1,2,3]"
    }
  }

}
