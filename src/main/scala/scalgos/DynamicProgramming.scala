package scalgos

import collection.mutable

object DynamicProgramming {

  /**
   * Finds largest rectangle (parallel to axes) under histogram with given heights and unit width
   * O(n) since each block is pushed/popped exactly once in O(1) time
   *
   * @param heights heights of histogram
   * @return area of largest rectangle under histogram
   */
  def maxRectangleInHistogram(heights: Seq[Int]) = {
    case class Block(position: Int, height: Int, leftBarrier: Int) {
      def areaUpto(to: Int) = height * (leftBarrier + to - position)
    }
    val blocks = mutable.Stack[Block]()
    var (maxArea, position) = (0, 0)
    for (height <- heights :+ 0) {    // insert 0 at end to flush stack
      var popped = 0
      while(!blocks.isEmpty && blocks.top.height > height) {
        val taller = blocks.pop()
        maxArea = maxArea max taller.areaUpto(position)
        popped += 1
      }
      blocks push Block(position, height, popped)
      position += 1
    }
    maxArea
  }
}
