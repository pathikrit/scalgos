package scalgos.sandbox


object BinaryTree {

  type Tree[T] = Option[Node[T]]

  case class Node[T](left: Tree[T], value: T, right: Tree[T])

  def inOrder[T](tree: Tree[T]): Seq[T] = tree match {
    case None => Nil
    case Some(Node(left, value, right)) => inOrder(left) ::: Seq(value) ::: inOrder(right)
  }

  def reconstruct[T](inOrder: Seq[T], preOrder: Seq[T]): Tree[T] = (inOrder, preOrder) match {
    case (Nil, Nil) => None

    case (leftIn :: x :: rightIn, root :: pres) if root == x =>
      val (leftPre, rightPre) = pres splitAt leftIn.length
      Some(Node(reconstruct(leftIn, leftPre), root, reconstruct(rightIn, rightPre)))
  }





}
