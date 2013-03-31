package scalgos

import collection.mutable

/**
 * Collection of number theory algorithms
 */
object NumberTheory {

  def sieveOfEratosthenes(n: Int) = {
    val isPrime = new mutable.BitSet(n)
  }


  /**
   * Calculate catalan number
   * O(n*n) - each recursive step takes O(n) time
   *
   * @param n
   * @return n=th catalan number
   */
  def catalan(n: Int) = {
    val cache = mutable.Map.empty[Int, BigInt]
    def _catalan(n: Int): BigInt = cache getOrElseUpdate(n, if (n == 0) 1 else {
      (0 until n) map {i => _catalan(i) * _catalan(n-i-1)} sum
    })
    _catalan(n)
  }

}
