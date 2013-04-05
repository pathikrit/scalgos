package scalgos

import collection.mutable

object AStar {

  /**
   * Run A* algorithm
   *
   * @param start starting node
   * @param isGoal true iff we are at goal
   * @param neighbors returns neighbors of given node
   * @param distance  distance between two neighbour nodes (guaranteed to be only called for @param neighbors)
   * @param heuristic admissible heuristic distance from goal to node n
   *                  must never over-estimate i.e. heuristic(x) <= dist(x,y) + heuristic(y) for all x,y
   *                  If not known, simply use 0
   * @tparam Node  encapsulates each position/state in search space
   * @return If found Some(goal, path) (where path.head is start and path.last is goal) else None
   */
  def run[Node](start: Node,
                isGoal: Node => Boolean,
                neighbors: Node => Iterable[Node],
                distance: (Node, Node) => Double = (i: Node, j: Node) => 1,
                heuristic: Node => Double = (i: Node) => 0d): Option[(Node, Seq[Node])] =
  {
    val score = mutable.Map(start -> 0d).withDefaultValue(Double.PositiveInfinity)
    val priority = Ordering by {n: Node => score(n) + heuristic(n)} reverse
    val queue = mutable.TreeSet(start)(priority)
    val parent = mutable.Map.empty[Node, Node]
    val visited = mutable.Set.empty[Node]

    def removeFirst = {
      val head = queue.head
      queue -= head
      head
    }

    while(!queue.isEmpty) {
      val c = removeFirst
      if (isGoal(c)) {
        val trace = mutable.ArrayBuffer.empty[Node]
        var current = c
        while (parent contains current) {
          current +=: trace
          current = parent(current)
        }
        assert(trace.head == start && trace.tail == c)
        return Some(c, trace.toSeq)
      }
      neighbors(c) filterNot visited.contains foreach {n =>
        if(score(n) >= score(c) + distance(c, n)) {
          queue -= n
          score(n) = score(c) + distance(c, n)
          queue += n
          parent(n) = c
        }
      }
      visited += c
    }
    None
  }
}
