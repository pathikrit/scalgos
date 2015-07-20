package com.github.pathikrit.scalgos

import org.specs2.mutable._

import scala.collection.mutable
import scala.collection.immutable.BitSet

import Implicits.Crossable
import NumberTheory._

class NumberTheorySpec extends Specification {

  "sieveOfEratosthnes" should {
    "not work for negative numbers" in todo

    "work for n = 0,1,2,3" in todo

    "include n when n is a prime" in todo

    "match actual isPrime check" in {
      val n = 100000
      val primeSet = sieveOfEratosthenes(n)
      examplesBlock {
        for (i <- 1 to n) {
          primeSet(i) must be equalTo isPrime(i)
        }
      }
    }
  }

  "sieveOfSundaram" should {
    "not work for negative numbers" in todo

    "match the sieveOfEratosthenes" in todo
//    {
//      for (i <- 3 to 1000) {
//        BitSet(sieveOfSundaram(i): _*) must be equalTo (sieveOfEratosthenes(i) - 2)
//      }
//    }
  }

  "phi" should {
    "fail for negative/zero inputs" in todo

    "match brute force" in {
      val n = 100
      val phi = phis(n)
      def phi2(n: Int) = sieveOfEratosthenes(n).foldLeft(n){(phi, p) => if (phi%p == 0) phi - phi/p else phi}
      examplesBlock {
        for (i <- 1 to n) {
          phi(i) must be equalTo ((1 to i) count {gcd(i, _) == 1})
          phi(i) must be equalTo phi2(i)
        }
      }
    }
  }

  "gcd" should {
    "match brute force" in {
      def commonMultiples(a: Int, b: Int) = {(1 to (a.abs max b.abs)) filter (g => a%g == 0 && b%g == 0)}
      examplesBlock {
        for ((x,y) <- (-100 to 100) X (-100 to 100) if x!=0 || y!=0) {
          val g = gcd(x,y)
          x%g must be equalTo 0
          y%g must be equalTo 0
          g must be equalTo commonMultiples(x,y).max
        }
      }
    }

    "fail for (0,0)" in {
      gcd(0,0) must throwA[AssertionError]
    }

    "work for Int.MaxValue and Int.MinValue" in todo
  }

  "extendedEuclidean algorithm" should {
    "match Bézout's identity" in {
      examplesBlock {
        for ((a,b) <- (-100 to 100) X (-100 to 100) if a!=0 || b!=0) {
          val (x,y) = extendedEuclidean(a,b)
          // todo: do we really need abs here?
          a.abs*x + b.abs*y must be equalTo gcd(a,b)
        }
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
      examplesBlock {
        for {
          c <- -n to n
          a <- -n to n
          b <- -n to n
        } if (a > b || c == 0) {
          numberOfMultiples(a,b,c) must throwA[IllegalArgumentException]
        } else {
          numberOfMultiples(a,b,c) must be equalTo ((a to b) count {_ % c == 0})
        }
      }
    }

    "works for -2^31 to 2^31-1" in todo
  }

  "countFactors" should {
    "fail for invalid inputs e.g. negative n" in todo

    "match brute force in" in {
      def slowCount(k: Int) = (1 to k/2) count {i => k%i == 0}
      val n = 1000
      val f = countFactors(n)
      (0 to n) forall {i => f(i) == slowCount(i)} must beTrue
    }.pendingUntilFixed
  }
}
