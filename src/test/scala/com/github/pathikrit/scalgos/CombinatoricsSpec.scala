package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Implicits._
import Combinatorics._

class CombinatoricsSpec extends Specification {

  "combinations" should {
    "work for 0 or 1 length" in todo

    "match the bitmask version" in todo

    "work for arbitrary input" in {
      val s = Seq(1, 2, 3)
      val result = combinations(s) map {_ mkString ("[", ",", "]")}
      result.length must be equalTo 1<<s.length
      result mkString " " must be equalTo "[] [1] [2] [3] [1,2] [1,3] [2,3] [1,2,3]"
    }
  }

  "repeatedCombinations" should {
    "work with n = 0, 1 etc" in todo

    "fail for n<0" in todo

    "work for empty lists" in todo

    "work for arbitrary case" in todo
//    {
//      repeatedCombinations("abc".toSet, 2) must be equalTo List("aa", "ab", "ac", "ba", "bb", "bc", "ca", "cc", "cc")
//    }
  }

  "nextCombination" should {
    "fail for n < 0" in todo
    "fail when s has elements not in [0,n)" in todo

    "work for s.length = 0, n = 0" in todo
    "work for s.length = 0, n = 1" in todo
    "work for s.length = 1, n = 0" in todo
    "work for s.length = 1, n = 1" in todo

    "n = 2 should match bit masking" in todo
//    {
//      var s = List.fill(5)(0)
//      do {
//        println(s)
//        s = nextCombination(s, 2)
//      } while (s exists {_ != 0})
//    }

    "work for arbitrary input" in todo
  }

  "nextPermutation" should {

    "work for 0 or 1 length" in todo

    "match the standard library version" in todo

    "work with duplicate items" in todo

    "operate in lexicographic order" in todo

    "match the library method" in {
      val input = List(1, 2, 3, 4)
      var s: Option[List[Int]] = Some(input)
      var c = 0
      do {
        val permutation = s.get
        //println(s mkString ", ")
        s = nextPermutation(permutation)
        c += 1
      } while(s.isDefined)

      c mustEqual input.permutations.length
    }

    "should match combinations(k) when called with 0,1 and k bits sets" in todo
  }

  "nthPermutation" should {

    "work for 0 or 1 length" in todo

    "fail for l < 0 or n not in [0, l!)" in todo

    "match permutations" in {
      val n = 5
      (0 until (n!).toInt) map nthPermutation(n) mustEqual (0 until n).permutations.toList
    }
  }

  "c" should {
    "fail if either n or r is negative" in todo
    "always 1 if r>n" in todo

    "match formula" in {
      examplesBlock {
        c.cache.size must be equalTo 0
        val t = 50
        for {n <- 0 to t; r <- 0 to n} {
          c(n, r) must be equalTo (n!)/(((n-r)!) * (r!))
        }
        c.cache.size must be equalTo (t/2 + 1)*(t + 1) // TODO: why
      }
    }
  }

  "choose-bithacks" should {
    "fail for negative n or negative k" in todo

    "fail when k > n" in todo

    "match C(n,r)" in {
      examplesBlock {
        for {
          // TODO:  start from 0?
          n <- 1 to 10
          r <- 1 to n
        } {
          val (a, e) = (choose(n, r), c(n, r))
          a.length must be equalTo e.toInt
          a forall {i => i.bitCount == r} must beTrue
        }
      }
    }
  }

  "choose" should {
    "fail when n or one of rs is negative" in todo
    "fail when r.sum > n" in todo
    "work for empty r" in todo
    "work when r = 0 or 1" in todo
    "r.length = 1 must be same as c(n,r)" in todo
    "match formula" in {
      // todo for n <- 0 to 100, partitions <- 1 to n, randomly generate p n/p numbers
      // todo - what if n != r.sum?
      val n = 10
      val r = Seq(1,2,3,4)
      choose(n, r) must be equalTo((n!)/(r map factorial).product)
    }
  }

  "factorial" should {
    "fail for negative numbers" in todo

    "match known sequence" in todo
  }

  "fibonacci" should {
    "fail for negative numbers" in todo

    "match known sequence" in {
      val expected: List[BigInt] = List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55)
      ((0 to 10) map fibonacci).toList must be equalTo expected
      fibonacci(99) must be equalTo BigInt("218922995834555169026")
    }
  }

  "catalan" should {
    "fail for negative numbers" in todo

    "match known sequence" in {
      val expected: List[BigInt] = List(1, 1, 2, 5, 14, 42, 132, 429, 1430, 4862, 16796)
      ((0 to 10) map catalan).toList must be equalTo expected
    }

//    "match recurrence relation" in {
//      examplesBlock {
//        def cat(n: Int): BigInt = ((0 until n) map {i => cat(i) * cat(n-i-1)}).sum
//        for (i <- 5 to 10) {
//          catalan(i) mustEqual cat(i)
//        }
//      }
//    }
  }

  "derangements" should {
    "fail for negative numbers" in todo
    "match the recurrence relation" in {

      lazy val d: Memo.F[Int, BigInt] = Memo {
        case n if n < 0 => throw new IllegalArgumentException
        case 0 => 1
        case 1 => 0
        case n => (n-1) * (d(n-1) + d(n-2))
      }

      examplesBlock {
        for (i <- 0 to 100) {
          derangement(i) must be equalTo d(i)
        }
      }
    }
  }

  "partialDerangement" should {
    "fail for negative numbers" in todo
    "match known sequence" in todo
    "be same as derangement for k = 0" in todo
  }
}
