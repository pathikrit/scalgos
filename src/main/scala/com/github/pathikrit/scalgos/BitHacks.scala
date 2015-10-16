package com.github.pathikrit.scalgos

/**
 * Collection of bit hacks - Hacker's Delight style
 */
object BitHacks {

  /**
   * Temporary solution to increase precision for +,-,* (not /) to 30 decimal digits (100 bits)
   * by doing calculations in both double & longs
   * double (first 13 digits correct) and longs (last 18 digits correct because calculation is mod 2^64)
   * When the digits on the border of long and double are ..99999... or ...00000..., it may fail (e.g. 10^30 fails)
   */
  implicit class ExtendedArithmetic(a: Long) {

    def +~(b: Long) = combine(a.toDouble + b, a+b)

    def -~(b: Long) = combine(a.toDouble - b, a-b)

    def *~(b: Long) = combine(a.toDouble * b, a*b)

    private[this] def combine(x: Double, y: Long) = {
      val l = 18
      val sx = "%.0f" format x
      if (sx.length <= l) {
        y.toString
      } else {
        var sl = sx.substring(0, sx.length()-l).scanLeft(0l)((i, c) => 10*i + c - '0').last
        for (i <- 1 to l) sl *= 10
        sx + (s"%0${l}d" format y - sl)
      }
    }
  }

  /**
   * @return toggle case of c
   */
  def toggleCase(c: Char) = (c^32).toChar

  /**
   * @return the tuple swapped
   */
  def xorSwap(a: (Int, Int)) = {
    var (x, y) = a
    x ^= y
    y ^= x
    x ^= y
    (x, y)
  }

  /**
   * @return the tuple swapped
   */
  def noTempSwap(a: (Int, Int)) = {
    var (x, y) = a
    x = x - y
    y = x + y
    x = y - x
    (x, y)
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
    case _ if a == b => a
    case _ => (a&1, b&1) match {
      case (0, 0) => gcd(a>>1, b>>1)<<1
      case (0, 1) => gcd(a>>1, b)
      case (1, 0) => gcd(a, b>>1)
      case (1, 1) => if (a>b) gcd(b, a-b) else gcd(a, b-a)
    }
  }

  /**
   * Binary search by bit toggling from MSB to LSB
   * O(64) bit-wise operations for Longs (O(32) for Ints)
   *
   * @return Some(x) such that x is the largest number for which f is true
   *         If no such x is found, None
   */
  def bitBinSearch(f: Long => Boolean): Option[Long] = {
    var p = 0L
    var n = Long.MinValue
    var t = n >>> 1
    while (t > 0) {
      if (f(p|t)) p |= t
      if (f(n|t)) n |= t
      t >>= 1
    }
    Seq(p, n) find f
  }
}
