package scalgos.sandbox

import collection.mutable

class DisjointSet[T] {

  class Node(entry: T) {
    var parent = this
    def representative: Node = if (parent == this) this else parent.representative
  }

  private val index = mutable.Map[T, Node]()

  def create(entry: T) {
    if(has(entry)) {
      sys.error(s"DisjoinSet already contains $entry")
    }
    index(entry) = new Node(entry)
  }

  def find(entry: T) = if (has(entry)) Some(index(entry).representative) else None

  def union(e1: T, e2: T) {
    (find(e1), find(e2)) match {
      case (Some(n1), Some(n2)) => n2.parent = n1
      case _ => sys.error(s"Could not find $e1 or $e2")
    }
  }


  def has(entry: T) = index contains entry
}

