package scalgos

import collection.mutable
import util.Random

object NumberTheory {

  def sieveOfEratosthenes(n: Int) = {
    val isPrime = new mutable.BitSet(n)
  }

  def rand(start: Int = 0, end: Int) = {
    assume(end >= start)
    start + Random.nextInt(end - start)
  }
}
