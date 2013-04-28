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

  "binaryGcd" should {
    "match Euclid's gcd" in {
      for ((x,y) <- (-100 to 100) X (-100 to 100) if x!=0 || y!=0) {
        val g = binaryGcd(x,y)
        x%g must be equalTo 0
        y%g must be equalTo 0
        g must be equalTo gcd(x,y)
      }
    }

    "fail for (0,0)" in {
      binaryGcd(0,0) must throwA[IllegalArgumentException]
    }

    "work for Int.MaxValue and Int.MinValue" in todo
  }

  "lcm" should {
    "match brute force" in todo
    "work for (0,0)" in todo
    "work for Int.MaxValue & Int.MinValue" in todo
  }
}
