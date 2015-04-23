package com.github.pathikrit.scalgos

import scala.collection.mutable

import Implicits.Updateable

/**
 * The result of an A* search
 *
 * @param cost the total cost to reach goal
 * @param path the path from start to goal (where path.head is start and path.last is goal)
 */
case class Result[Node](cost: Double, path: Seq[Node])

/**
 * Template to run A* algorithm
 * @tparam Node encapsulates each position/state in search space
 */
abstract class AStar[Node] {

  /**
   * Run A* from starting node
   * O(E + V log V) - each edge is examined atmost once and priority queue operations are log V
   *
   * @param start starting node
   * @param isGoal true iff we are at goal
   * @return Some(Result) if goal found else None
   */
  def run(start: Node, isGoal: Node => Boolean): Option[Result[Node]] = {
    val score = mutable.Map(start -> 0d) withDefaultValue Double.PositiveInfinity
    val priority = Ordering by {n: Node => score(n) + heuristic(n)}
    val queue = mutable.TreeSet(start)(priority)
    val parent = mutable.Map.empty[Node, Node]
    val visited = mutable.Set.empty[Node]

    def reScore(current: Node)(n: Node) = {
      score(n) = score(current) + distance(current, n)
    }

    while (queue.nonEmpty) {
      val current = queue.removeFirst
      if (isGoal(current)) {
        val trace = mutable.ArrayBuffer.empty[Node]
        var (v, cost) = (current, 0d)
        while (parent contains v) {
          cost += distance(parent(v), v)
          v +=: trace
          v = parent(v)
        }
        return Some(Result(cost, start +: trace.toSeq))
      }
      // TODO: if edge in visited, we have overestimation
      neighbors(current) filterNot visited.contains foreach {n =>
        if(score(n) >= score(current) + distance(current, n)) {
          queue updatePriority (n, reScore(current))
          parent(n) = current
        }
      }
      visited += current
    }
    None
  }

  /**
   * Find neighbors of given node
   * @param n given node
   * @return find all nodes that have edges from n
   */
  def neighbors(n: Node): Iterable[Node]

  /**
   * Calculate known distance between 2 nodes
   * Guaranteed to be called for only vertices that are @see neighbours
   *
   * @param from start node
   * @param to end node
   * @return distance between @param from and @param to
   */
  def distance(from: Node, to: Node) = 1d

  /**
   * Admissible heuristic distance from node n to goal
   * estimated cost from current node to goal
   * must never over-estimate i.e. heuristic(x) <= dist(x,y) + heuristic(y) for all x,y
   * If not known, simply use 0 (obviously fails if dist(x,y) can be negative)
   *
   * @param n input node
   * @return estimated heuristic distance from node to goal
   */
  def heuristic(n: Node) = 0d
}
