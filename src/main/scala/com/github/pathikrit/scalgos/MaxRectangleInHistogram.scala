package com.github.pathikrit.scalgos

/**
 * Find area of largest rectangle in histogram
 * Recursive rule-based 3-window linear rewrite algorithm
 */
object MaxRectangleInHistogram {

  private[this] val (left, right) = (true, false)

  /**
   * A block has dimension (height, width) and prev and next pointers
   * It can be merged left or right with prev or next respectively
   * The invariant is prev is always a an increasingly taller blocks upto current
   * next is the unprocessed blocks
   */
  implicit class Block(dimension: (Int, Int)) {
    var (prev, (height, width), next) = (Option.empty[Block], dimension, Option.empty[Block])

    def area = height * width

    /**
     * Append a new block-chain to end of this block
     */
    def +(that: Block) = {
      this.next = Some(that)
      that.prev = Some(this)
      that
    }

    /**
     * Merge this block either with prev (if left is true) or next (if left is false)
     * @return max(this block's area, collapsing after merging)
     */
    private[this] def merge(left: Boolean) = {
      val that = if (left) prev.get else next.get
      val currentArea = area
      height = this.height min that.height
      width = this.width + that.width
      // fix the pointers - basically a remove in doubly linked list
      if (left) {
        prev = that.prev
        if (prev.isDefined) {
          prev.get.next = Some(this)
        }
      } else {
        next = that.next
        if (next.isDefined) {
          next.get.prev = Some(this)
        }
      }
      currentArea max collapse
    }

    /**
     * Assuming the ends are (0,0) blocks, it collapses according to rules by looking at prev,this,next
     * @return area of largest rectangle in histogram
     */
    def collapse: Int = (prev, next) match {
      case (None, None) => area

      case (Some(a), None) => merge(left)    // nothing to process - pop things of stack

      case (None, Some(b)) => b.collapse

      // our goal here is to make sure x <= y <= z
      case (Some(a), Some(b)) => (a.height, height, b.height) match {
        case (x, y, _) if x > y => merge(left)    // x and y itself in wrong order
        case (x, _, z) if z < x => merge(left)    // z is smaller than even x
        case (_, y, z) if z < y => merge(right)   // z is between (x,y)
        case _ => b.collapse                    // everything in right order - proceed right
      }
    }
  }

  /**
   * Finds largest rectangle (parallel to axes) under histogram with given heights and width
   * O(n) - Each step in collapse decreased either sorted.length or unprocessed.length and each step is O(1)
   *
   * @param dimensions (height, width)s of histogram
   * @return area of largest rectangle under histogram
   */
  def apply(dimensions: Seq[(Int, Int)]) = {
    val start = Block(0,0)    // pad start & end with dummy height=0 block
    (dimensions :+ (0,0)).foldLeft(start)(_ + _)   //link up the blocks
    start.collapse
  }
}
