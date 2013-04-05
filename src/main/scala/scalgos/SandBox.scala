package scalgos

import collection.{SortedMap, mutable}

object SandBox {

  def subsetSums(s: Seq[Int]) = s.foldLeft(Set[Int](0))((possibleSums, i) => possibleSums map {_ + i})

  // use double[][] because of Double.POSITIVE_INFINITY

  def combineIntervals(intervals: Pair[Int, Int]*) = {
 //   val combined = mutable.

  }


  def max2DSubArraySum(s: Seq[Seq[Int]]) = {
 //   val rectSum =
  }

}
