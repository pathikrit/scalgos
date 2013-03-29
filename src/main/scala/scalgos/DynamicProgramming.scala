package scalgos

import collection.mutable

object DynamicProgramming {

  /**
   * Generate all possible valid brackets
   * O(C(n)) = O(4^n / n^1.5)
   * Number of brackets = C(n) i.e. the n-th Catalan number
   * because C(n) = sigma(i = 0 to n-1 C(i)*C(n-i))
   *
   * @param n number of pairs
   * @return generate all possible valid n-pair bracket strings
   */
  def validBrackets(n: Int) = {
    val cache = mutable.Map.empty[Int, Seq[String]]

    def _validBrackets(n: Int): Seq[String] = cache getOrElseUpdate (n, if (n == 0) Seq("") else for {
      i <- 0 until n
      a <- _validBrackets(i)
      b <- _validBrackets(n-i-1)
    } yield '(' + a  + ')' + b)

    _validBrackets(n)
  }

  /**
   * Finds largest rectangle (parallel to axes) under histogram with given heights and width
   * O(n) since its basically recursive fibonacci algorithm
   *
   * @param dimensions (width, height)s of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(dimensions: Seq[(Int, Int)]) = {
    case class Block(width: Int, height: Int) {
      val area = width * height
      def join(other: Block) = Block(width + other.width, height min other.height)
    }
    implicit def toBlock(dimension: (Int, Int)) = Block(dimension._1, dimension._2)

    val cache = mutable.Map.empty[Seq[Block], Int]

    def area(blocks: Seq[Block]): Int = cache getOrElseUpdate (blocks, blocks match {
      case Nil => 0
      case first :: Nil => first.area
      case first :: second :: rest => Seq(first.area, area(second :: rest), area((first join second) :: rest)).max
    })

    area(dimensions map toBlock)
  }
}
