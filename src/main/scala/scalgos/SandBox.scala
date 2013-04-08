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


//  def bellmanFord(g: Graph, start: Int, goal: Int) {
//    val distance = Array.tabulate(g.numberOfVertices)(i => g(start->i))
//    val parent = mutable.Map.empty[Int, Int]
//
//    def relax = for {
//      (u,v) <- g.edges
//      if distance(v) > distance(u) + g(u->v)
//    } {
//      distance(v) = distance(u) + g(u->v)
//      parent(v) = u
//    }
//  }

}
