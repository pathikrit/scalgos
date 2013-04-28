package com.github.pathikrit.scalgos

import Implicits._

/**
 * Collection of number theory algorithms
 */
object NumberTheory {

  /**
   * Run's sieve of Eratosthenes
   * O(n log n)
   *
   * @param n number upto (inclusive) to run the sieve
   * @return bitset p such that p(x) is true iff x is prime
   */
  def sieveOfEratosthenes(n: Int) = {
    val numbers = 2 to n
    val sieve = collection.mutable.BitSet(numbers: _*)
    for (p <- numbers takeWhile (i => i*i <= n) if sieve(p)) {
      sieve --= p * p to n by p
    }
    sieve.toImmutable
  }

  /**
   * O(c = 100) primality check
   *
   * @return true iff n is prime (with 2^(-100) probability of failure)
   */
  def isPrime(n: Int) = BigInt(n) isProbablePrime 100

  /**
   * Euclid's algorithm to calculate gcd
   * O(log(max(a,b))
   *
   * @return largest number g such that a%g == 0 and b%g == 0
   */
  def gcd(a: Int, b: Int): Int =
    if (a < 0) gcd(-a, b)
    else if(b < 0) gcd(a, -b)
    else if (b == 0) {assume(a!=0); a}
    else gcd(b, a%b)

  /**
   * Binary gcd algorithm
   * O(log(max(a,b))
   *
   * @return largest number g such that a%g == 0 and b%g == 0
   */
  def binaryGcd(a: Int, b: Int): Int = (a, b) match {
    case _ if a < 0 => gcd(-a, b)
    case _ if b < 0 => gcd(a, -b)
    case (0, 0) => throw new IllegalArgumentException
    case (0, _) => b
    case (_, 0) => a
    case _ if a == b => a
    case _ => (a%2, b%2) match {
      case (0, 0) => 2*binaryGcd(a/2, b/2)
      case (0, 1) => binaryGcd(a/2, b)
      case (1, 0) => binaryGcd(a, b/2)
      case (1, 1) => if (a>b) binaryGcd(b, a-b) else binaryGcd(a, b-a)
    }
  }

  /**
   * Uses Euclid's GCD algorithm
   * O(log(max(a,b))
   *
   * @return least common (non-negative) multiple of a,b
   */
  def lcm(a: Int, b: Int) = a/gcd(a,b) * b
}
