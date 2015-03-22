package com.github.pathikrit.scalgos

import scala.math.Ordering.Implicits._

import Implicits._
import Memo._

/**
 * collection of algorithms related to combinatorics
 */
object Combinatorics {

  // TODO: make these implicit methods .combinations, .combinations(n) etc

  /**
   * Iterate over all 2^n combinations - Different from the Scala Collection's combinations...
   *
   * @param s sequence to do combination over
   * @return all 2^n ways of choosing elements from s
   */
  def combinations[A](s: Seq[A]) = for {i <- 0 to s.length; j <- s combinations i} yield j

  /**
   * Combinations with repeats e.g. (2, Set(A,B,C)) -> AA, AB, AC, BA, BB, BC, CA, CB, CC
   *
   * @return all s.length^n combinations
   */
  def repeatedCombinations[A](s: Set[A], n: Int) : Traversable[List[A]] = n match {
    case 0 => List(Nil)
    case _ => for {(x, xs) <- s X repeatedCombinations(s, n-1)} yield x :: xs
  }

  /**
   * Generates all n^s.length combinations
   * O(n)
   * Think of as instead of boolean (2 states), we have n states for each cell in s
   * Also, equivalent to adding 1 in base n
   * Call with initially all zeroes in s
   *
   * @return next combination of n
   */
  def nextCombination(s: Seq[Int], n: Int): List[Int] = s match {
    case Nil => Nil
    case x :: xs if x < 0 || x >= n => throw new IllegalArgumentException
    case x :: xs if x == n-1 => 0 :: nextCombination(xs, n)
    case x :: xs => x+1 :: xs
  }

  /**
   * Find next permutation of s - do not use this, simply use the one from Scala collections library instead
   * Call with Seq(0,0,0,0,1,1) for example to 6C2
   * O(n)
   *
   * @return Some(p) if next permutation exists or None if s is already in decreasing order
   */
  def nextPermutation[A: Ordering](s: List[A]) = indexToOpt(s zip s.tail lastIndexWhere {e => e._1 < e._2}) map {
    case p =>
      val e = s(p)
      val n = s lastIndexWhere {e < _}
      val (a, b) = s.swap(p, n) splitAt (p + 1)
      a ::: b.reverse
  }

  /**
   * Find nth Permutation
   * O(l*l)
   *
   * @return nth permutation of {0, 1, ... , l-1}
   */
  def nthPermutation(l: Int)(n: Int) = {
    implicit def toInt(b: BigInt): Int = b.toInt
    require(l >=0 && n >= 0 && n < (l!))
    def select(s: List[Int], i: Int): List[Int] = s.length match {
      case 0 => Nil
      case k =>
        val d = (k - 1)!
        val e = s(i/d)
        e :: select(s - e, i%d)
    }
    select(List(0 until l: _*), n)
  }

  /**
   * @return memoized function to calculate C(n,r)
   */
  val c: (Int, Int) ==> BigInt = Memo {
    case (_, 0) => 1
    case (n, r) if r > n/2 => c(n, n - r)
    case (n, r) => c(n - 1, r - 1) + c(n - 1, r)
  }

  /**
   * @return a stream of longs such that k bits of it are set and max total bits = n
   */
  def choose(n: Int, k: Int) = ((1L<<k) - 1) `...` { c =>
    val u = -c&c
    val v = c + u
    v + (v^c)/u/4
  } takeWhile {i => (i>>n) == 0}

  /**
   * Number of ways to permute n objects which has r.length kinds of items
   * O(n*r)
   *
   * @param r r(i) is number of types of item i
   *
   * @return n!/(r0! * r1! * ....) * (n - r.sum)!
   */
  def choose(n: Int, r: Seq[Int]): BigInt = r match {
    case Nil => 1
    case x :: Nil => c(n, x)
    case x :: y :: z => c(x + y, y) * choose(n, (x + y) :: z)
  }

  val naturals = 1 `...`

  val wholes = 0 `...`

  /**
   * Stream of factorials
   */
  val factorial: Stream[BigInt] = 1 #:: (naturals map {i => i*factorial(i - 1)})

  /**
   * Stream of fibonacci numbers
   */
  val fibonacci: Stream[BigInt] = 0 #:: fibonacci.scanLeft(BigInt(1)){_ + _}

  /**
   * Stream of catalan numbers
   */
  val catalan: Stream[BigInt] = 1 #:: (naturals map {i => (4*i - 2)*catalan(i - 1)/(i + 1)})

  /**
   * Number of ways of selecting (1 to n) items such that none of the items are in its own position
   * TODO: Proof
   * TODO: nextPartialDerangement
   * O(n)
   *
   * @return memoized function to count derangements
   */
  val derangement: Int ==> BigInt = Memo {
    case n if n%2 == 0 => n*derangement(n - 1) + 1
    case n if n%2 == 1 => n*derangement(n - 1) - 1
    case _ => 0     // negative n
  }

  /**
   * @return Number of ways to arrange [1 to n] such that exactly k of them are in own position
   */
  def partialDerangement(n: Int, k: Int) = c(n, k) * derangement(n - k)

  private[this] implicit def toBigInt(i: Int): BigInt = BigInt(i)
}
