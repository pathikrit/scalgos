package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Combinatorics._

class CombinatoricsSpec extends Specification {

  "combinations" should {
    "work for 0 or 1 length" in todo

    "match the bitmask version" in todo

    "work for arbitrary input" in {
      val s = Seq(1, 2, 3)
      val result = combinations(s, (i: Seq[Int]) => i mkString ("[", ",", "]"))
      result.length must be equalTo 1<<s.length
      result mkString " " must be equalTo "[] [1] [2] [3] [1,2] [1,3] [2,3] [1,2,3]"
    }
  }

  "nextPermutation" should {

    "work for 0 or 1 length" in todo

    "match the standard library version" in todo

    "work with duplicate items" in todo

    "operate in lexicographic order" in {
      var s: Option[Seq[Int]] = Some(Seq(1,2,3,4))
      do {
        val permutation = s.get
        //println(permutation mkString " ")
        s = nextPermutation(permutation)
      } while(s.isDefined)
    }
  }
}
