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
}
