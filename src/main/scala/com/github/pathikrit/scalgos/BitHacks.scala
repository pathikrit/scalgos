package com.github.pathikrit.scalgos

/**
 * Collection of bit hacks - Hacker's Delight style
 */
object BitHacks {

  /**
   * @return toggle case of c
   */
  def toggleCase(c: Char) = (c^32).toChar

  /**
   * @return the tuple swapped
   */
  def xorSwap(a: (Int, Int)) = {
    var (x,y) = a
    x ^= y
    y ^= x
    x ^= y
    (x,y)
  }

  /**
   * @return the tuple swapped
   */
  def noTempSwap(a: (Int, Int)) = {
    var (x,y) = a
    x = x - y
    y = x + y
    x = y - x
    (x,y)
  }

  /**
   * Binary gcd algorithm
   * O(log(max(a,b))
   *
   * @return largest number g such that a%g == 0 and b%g == 0
   */
  def gcd(a: Int, b: Int): Int = (a, b) match {
    case _ if a < 0 => gcd(-a, b)
    case _ if b < 0 => gcd(a, -b)
    case (0, 0) => throw new IllegalArgumentException
    case (0, _) => b
    case (_, 0) => a
    case _ if (a == b) => a
    case _ => (a&1, b&1) match {
      case (0, 0) => gcd(a>>1, b>>1)<<1
      case (0, 1) => gcd(a>>1, b)
      case (1, 0) => gcd(a, b>>1)
      case (1, 1) => if (a>b) gcd(b, a-b) else gcd(a, b-a)
    }
  }
}
