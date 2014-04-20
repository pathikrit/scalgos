package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import DynamicProgramming._

class DynamicProgrammingSpec extends Specification {

  "subsetSum" should {

    "work for empty set" in todo

    "work for only positive numbers" in todo

    "work for only negative numbers" in todo

    "always work for sum == 0" in todo

    "match brute force check" in {
      def bruteForceCheck(s: Seq[Int], t: Int) = Combinatorics.combinations(s) filter {_.sum == t}
      def normalized(sums: Seq[Seq[Int]]) = sums map {_.sorted} toSet       //todo: why toSet?

      examplesBlock {
        for (i <- -50 to 50) {
          val nums = RandomData.seq(length = 10)
          normalized(subsetSum(nums, i)) must be equalTo normalized(bruteForceCheck(nums, i))
        }
      }
    }
  }

  "isSubsetSumAchievable" should {

    "work for empty set" in todo

    "work for only positive numbers" in todo

    "work for only negative numbers" in todo

    "always true for sum == 0" in todo

    "match brute force check" in {
      def bruteForceCheck(s: Seq[Int], t: Int) = Combinatorics.combinations(s) exists {_.sum == t}
      examplesBlock {
        for (i <- -50 to 50) {
          val nums = RandomData.seq(length = 10)
          isSubsetSumAchievable(nums, i) must be equalTo bruteForceCheck(nums, i)
        }
      }
    }
  }

  "closestPartition" should {
    "work for trivial cases" in todo

    "match brute force algorithm" in {
      def closestDiff(s: Seq[Int]) = {
        val total = s.sum
        val sums = s.foldLeft(Set(0)){(p, i) => (p map {_ + i}) ++ p}
        sums minBy {i => (total - 2*i).abs}
      }

      examplesBlock {
        for(i <- 1 to 10000) {
          val n = RandomData.integer(end = 20)
          val nums = RandomData.seq(n)
          val a = closestPartition(nums)
          val b = nums diff a
          val c = closestDiff(nums)
          val d = nums.sum - c
          (a.sum - b.sum).abs must be equalTo (c - d).abs
        }
      }
    }
  }

  "minimumChange" should {
    "fail for negative numbers" in todo
    "work for empty coin list" in todo
    "match brute force search" in todo
  }

  "editDistance" should {
    "work when either or both sequence is empty" in todo
    "work for completely mismatched sequences" in todo
    "work for arbitrary input" in todo
  }

  "validBrackets" should {
    "be list containing empty string for 0" in {
      validBrackets(0) must be equalTo IndexedSeq("")
    }

    "work for arbitrary input" in {
      validBrackets(1) must be equalTo IndexedSeq("()")
      validBrackets(2) must contain(exactly("()()", "(())"))
      validBrackets(3) must contain(exactly("()()()", "()(())", "(())()", "((()))", "(()())"))
    }

    "match catalan numbers" in {
      examplesBlock {
        for (i <- 0 to 10) {
          validBrackets(i).length must be equalTo Combinatorics.catalan(i).intValue
        }
      }
    }
  }

  "longestCommonSubsequence" should {
    implicit def toList(s: String) = s.toList

    "be empty if one of the input is empty" in {
      longestCommonSubsequence("hello", "") must beEmpty
      longestCommonSubsequence("", "nastenka") must beEmpty
      longestCommonSubsequence("", "") must beEmpty
    }

    "be empty if nothing in common" in {
      longestCommonSubsequence("abcdef", "ghijklmonopqr") must be empty
    }

    "work for arbitrary input" in {
      longestCommonSubsequence("patrick", "pathikrit") must be equalTo "patik"  // TODO: patri?
    }
  }

  "longestIncreasingSubsequence" should {
    "be empty for empty sequence" in {
      longestIncreasingSubsequence(Seq[Int]()) must be empty
    }

    "have 1 item for decreasing sequence" in {
      longestIncreasingSubsequence(Seq(5, 4, 3, 2, 1)) must have length 1
    }

    "work for arbitrary input" in {
      val input = Seq(0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15)
      longestIncreasingSubsequence(input) must be equalTo Seq(0, 2, 6, 9, 11, 15)
    }

    "be same same as longestCommonSubsequence with sorted input" in {
      val s = RandomData.list().distinct // TODO: What happens when duplicates?
      val lis = longestIncreasingSubsequence(s)
      val expected = longestCommonSubsequence(s.sorted.reverse, s.reverse).reverse // bias towards "earlier" sequence
      lis must be equalTo expected
      lis.length must be equalTo longestCommonSubsequence(s, s.sorted).length
    }
  }

  "maxSubArraySum" should {
    "work on empty sequences" in {
      maxSubArraySum(Nil) must be equalTo 0
    }

    "work on small sequences" in {
      maxSubArraySum(Seq(1)) must be equalTo 1
      maxSubArraySum(Seq(-1)) must be equalTo 0
      maxSubArraySum(Seq(-1, -2)) must be equalTo 0
      maxSubArraySum(Seq(-1, 0, -2)) must be equalTo 0
      maxSubArraySum(Seq(-1, 0, 2)) must be equalTo 2
      maxSubArraySum(Seq(-1, 0)) must be equalTo 0
      maxSubArraySum(Seq(0)) must be equalTo 0
      maxSubArraySum(Seq(1)) must be equalTo 1
    }

    "match brute force algorithm" in {
      val s = RandomData.list()
      val sums = for (start <- s.indices; end <- start to s.length) yield s.slice(start, end).sum
      maxSubArraySum(s) must be equalTo sums.max
    }
  }
}
