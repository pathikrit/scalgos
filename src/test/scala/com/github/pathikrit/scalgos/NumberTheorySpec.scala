package com.github.pathikrit.scalgos

import org.specs2.mutable._

import Implicits.Crossable
import NumberTheory._

class NumberTheorySpec extends Specification {

  "sieveOfEratostheses" should {
    "not work for negative numbers" in todo

    "work for n = 0,1,2,3" in todo

    "match actual isPrime check" in {
      val n = 100000
      val primeSet = sieveOfEratosthenes(n)
      for (i <- 1 to n) {
        primeSet(i) must be equalTo isPrime(i)
      }
    }
  }

  "gcd" should {
    "match brute force" in {
      def commonMultiples(a: Int, b: Int) = {(1 to (a.abs max b.abs)) filter (g => a%g == 0 && b%g == 0)}
      for ((x,y) <- (-100 to 100) X (-100 to 100) if x!=0 || y!=0) {
        val g = gcd(x,y)
        x%g must be equalTo 0
        y%g must be equalTo 0
        g must be equalTo commonMultiples(x,y).max
      }
    }

    "fail for (0,0)" in {
      gcd(0,0) must throwA[AssertionError]
    }

    "work for Int.MaxValue and Int.MinValue" in todo
  }

  "extendedEuclidean algorithm" should {
    "match Bézout's identity" in {
      for ((a,b) <- (-100 to 100) X (-100 to 100) if a!=0 || b!=0) {
        val (x,y) = extendedEuclidean(a,b)
        // todo: do we really need abs here?
        a.abs*x + b.abs*y must be equalTo gcd(a,b)
      }
    }

    "fail for (0,0)" in {
      extendedEuclidean(0,0) must throwA[AssertionError]
    }

    "work for Int.MaxValue and Int.MinValue" in todo
  }

  "lcm" should {
    "match brute force" in todo
    "work for (0,0)" in todo
    "work for Int.MaxValue & Int.MinValue" in todo
  }

  "numberOfMultiples" should {
    "fail for invalid inputs (c = 0 or a>b)" in todo

    "match brute force" in {
      val n = 100
      for {
        c <- -n to n
        a <- -n to n
        b <- -n to n
      } if (a > b || c == 0) {
        numberOfMultiples(a,b,c) must throwA[IllegalArgumentException]
      } else {
        val (actual, expected) = (numberOfMultiples(a,b,c), (a to b) count {_ % c == 0})
        actual must be equalTo expected
      }
    }

    "works for -2^31 to 2^31-1" in todo

  }
}
