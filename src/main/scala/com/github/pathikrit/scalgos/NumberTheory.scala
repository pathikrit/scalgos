package com.github.pathikrit.scalgos

import scala.collection.mutable

import Implicits._

/**
 * Collection of number theory algorithms
 */
object NumberTheory {

  /**
   * Runs sieve of Eratosthenes
   * O(n log n) - TODO: why?
   *
   * @param n number upto (inclusive) to run the sieve
   * @return bitset p s.t. p(x) <=> x is prime
   */
  def sieveOfEratosthenes(n: Int) = {
    val numbers = 2 to n
    val isPrime = mutable.BitSet(numbers: _*)
    for (p <- numbers takeWhile {i => i*i <= n} if isPrime(p)) {
      isPrime --= p*p to n by p
    }
    isPrime.toImmutable
  }

  /**
   * Runs sieve of Sundaram
   * O(n log n) - TODO: why?
   *
   * @return all *odd* primes in [1,n]
   */
  def sieveOfSundaram(n: Int) = {
    val s = mutable.BitSet(1 to n/2 : _*)
    for {
      i <- 1 to n/6
      j <- 1 to (n-2*i)/(4*i+1)   // j s.t. i + j + 2ij <= n/2  todo: we go from j <- i to (2i+1)j?
    } s -= i + j + 2*i*j          // all odd composites are of form (2i+1)(2j+1) = 2(i + j + 2ij) + 1
    s.toSeq.map(2*_ + 1)
  }

  /**
   * Euler's Totient Function or phi function
   * O(n log n)
   *
   * @return phi s.t. phi(n) = number of positive integers <= n that are co-prime to n
   */
  def phis(n: Int) = {
    val phi = Array.tabulate(n + 1)(identity)
    for {
      i <- 2 to n if phi(i) == i
      j <- i to n by i
    } phi(j) = (phi(j)/i)*(i-1)
    phi
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
  def gcd(a: Int, b: Int): Int = (a, b) match {
    case _ if a < 0 => gcd(-a, b)
    case _ if b < 0 => gcd(a, -b)
    case (_, 0) => assume(a!=0); a
    case _ => gcd(b, a%b)
  }

  /**
   * Uses Euclid's GCD algorithm
   * O(log(max(a,b))
   *
   * @return least common (non-negative) multiple of a,b
   */
  def lcm(a: Int, b: Int) = a/gcd(a,b) * b

  /**
   * Extended Euclidean algorithm to calculate Bézout's identity
   * @return (x,y) such that ax + by = gcd(a,b)
   */
  def extendedEuclidean(a: Int, b: Int): (Int, Int) = (a, b) match {
    case _ if a < 0 => extendedEuclidean(-a, b)
    case _ if b < 0 => extendedEuclidean(a, -b)
    case (_, 0) => assume(a != 0); (1, 0)
    case _ =>
      val (x, y) = extendedEuclidean(b, a%b)
      (y, x - (a/b)*y)
  }

  /**
   * Count multiples - surprisingly non trivial
   * O(n)
   * TODO: Proof
   *
   * @return number of multiples of c in [a,b]
   */
  def numberOfMultiples(a: Int, b: Int, c: Int): Int = c.signum match {
    case -1 => numberOfMultiples(a, b, -c)
    case 1 if b >= a => (b + (b < 0))/c - (a - (a > 0))/c + (a <= 0 && b >= 0)
    case _ => throw new IllegalArgumentException
  }

  /**
   * Count factors of all numbers upto n
   * @return f s.t. f(i) = number of factors of i (including 1 and i)
   */
  def countFactors(n: Int) = {
    val f = Array[Int](n + 1)
    for {
      i <- 1 to n
      j <- i to n by i
    } f(j) = f(j) + 1
    f
  }
}
