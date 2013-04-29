package com.github.pathikrit.scalgos

import org.specs2.mutable._

import DynamicProgramming._

class DynamicProgrammingSpec extends Specification {

  "subsetSum" should {

    "work for empty set" in todo

    "work for only postive numbers" in todo

    "work for only negative numbers" in todo

    "always work for sum == 0" in todo

    "match brute force check" in {
      def bruteForceCheck(s: Seq[Int], t: Int) = Combinatorics.combinations(s) filter {_.sum == t}
      def normalized(sums: Seq[Seq[Int]]) = sums map {_.sorted} toSet       //todo: why toSet?

      for (i <- (-50 to 50)) {
        val nums = RandomData.seq(length = 10)
        normalized(subsetSum(nums, i)) must be equalTo normalized(bruteForceCheck(nums, i))
      }
    }
  }

  "isSubsetSumAchievable" should {

    "work for empty set" in todo

    "work for only postive numbers" in todo

    "work for only negative numbers" in todo

    "always true for sum == 0" in todo

    "match brute force check" in {
      def bruteForceCheck(s: Seq[Int], t: Int) = Combinatorics.combinations(s) exists {_.sum == t}
      for (i <- (-50 to 50)) {
        val nums = RandomData.seq(length = 10)
        isSubsetSumAchievable(nums, i) must be equalTo bruteForceCheck(nums, i)
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
    "work for completely mismtached sequences" in todo
    "work for arbitrary input" in todo
  }

  "validBrackets" should {
    "be list containing empty string for 0" in {
      validBrackets(0) must be equalTo Seq("")
    }

    "work for arbitrary input" in {
      validBrackets(1) must be equalTo Seq("()")
      validBrackets(2) must contain("()()", "(())").only
      validBrackets(3) must contain("()()()", "()(())", "(())()", "((()))", "(()())").only
    }

    "match catalan numbers" in {
      for (i <- 0 to 10) {
        validBrackets(i).length must be equalTo Combinatorics.catalan(i).intValue
      }
    }
  }

  "longestCommonSubsequence" should {
    "be empty if one of the input is empty" in {
      longestCommonSubsequence("hello", "") must beEmpty
      longestCommonSubsequence("", "nastenka") must beEmpty
      longestCommonSubsequence("", "") must beEmpty
    }

    "be empty if nothing in common" in {
      longestCommonSubsequence("abcdef", "ghijklmonopqr") must be empty
    }

    "work for arbitrary input" in {
      longestCommonSubsequence("patrick", "pathikrit") must be equalTo "patri"
    }
  }

  "longestIncreasingSubsequence" should {
    "be empty for empty sequence" in {
      longestIncreasingSubsequence(Seq[Int]()) must be empty
    }

    "have 1 item for decreasing sequence" in {
      longestIncreasingSubsequence(Seq(5, 4, 3, 2, 1)) must have length(1)
    }

    "work for arbitrary input" in {
      val input = Seq(0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15)
      longestIncreasingSubsequence(input) must contain(0, 2, 6, 9, 11, 15).only.inOrder
    }

    "be same same as longestCommonSubsequence with sorted input" in {
      val s = RandomData.seq().distinct // TODO: What happens when duplicates?
      val lis = longestIncreasingSubsequence(s)
      lis must be equalTo longestCommonSubsequence(s.sorted, s)              // bias towards "earlier" sequence
      lis.length must be equalTo longestCommonSubsequence(s, s.sorted).length
    }
  }

  "maxSubArraySum" should {
    "work on empty sequences" in {
      maxSubArraySum(Nil) must be equalTo(0)
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
      val s = RandomData.seq()
      val sums = for (start <- s.indices; end <- start to s.length) yield s.slice(start, end).sum
      maxSubArraySum(s) must be equalTo sums.max
    }
  }
}
