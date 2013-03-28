package scalgos

import collection.mutable

object DynamicProgramming {

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
