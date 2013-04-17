package com.github.pathikrit.scalgos

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

}
