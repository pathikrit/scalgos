package com.github.pathikrit.scalgos

import org.specs2.mutable._

import NumberTheory._

class NumberTheorySpec extends Specification {

  "sieveOfEratostheses" should {
    "not work for negative numbers" in todo

    "work for n = 0,1,2,3" in todo

    "match the lazy haskell algorithm" in {
      val n = 100000
      val primeSet = sieveOfEratosthenes(n)
      for (i <- 1 to n) {
        primeSet(i) must be equalTo isPrime(i)
      }
    }
  }

}
