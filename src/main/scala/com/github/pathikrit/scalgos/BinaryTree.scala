package com.github.pathikrit.scalgos

import Ordering.Implicits._

/**
 * Collection of algorithms pertaining to Binary Trees
 */
object BinaryTree {

  /**
   * A Binary Search Tree
   */
  sealed abstract class BST[A: Ordering](val value: A)

  /**
   * A way to represent a full BST i.e. no nodes with 1 child
   */
  object BST {

    /**
     * An internal node of the BST
     */
    case class Node[A: Ordering](left: BST[A], override val value: A, right: BST[A]) extends BST {
      require(left.value <= value && value <= right.value)
    }
    case class Leaf[A: Ordering](override val value: A) extends BST
  }

  /**
   * A BinaryTree
   */
  type Tree[T] = Option[Node[T]]

  /**
   * A binary tree node
   * @param left left sub-tree
   * @param entry the value stored at the node
   * @param right right sub-tree
   */
  case class Node[T](left: Tree[T], entry: T, right: Tree[T])

  /**
   * Reconstruct a BST from its pre-order traversal in O(n * depth)
   * Assume elements in BST are unique
   * O(n)
   *
   * @param preOrder pre-order traversal of BST
   * @return reconstructed BST
   */
  def reconstructBST[T: Ordering](preOrder: Seq[T]): Tree[T] = preOrder match {
    case Nil => None
    case root :: children =>
      val (left, right) = children partition {_ < root}
      Some(Node(reconstructBST(left), root, reconstructBST(right)))
  }

  /**
   * Pre-order traverse a tree
   * O(n)
   *
   * @param root the root of the tree
   * @return the pre-order traversal
   */
  def preOrderTraversal[T](root: Tree[T]): List[T] = root match {
    case None => Nil
    case Some(Node(left, entry, right)) => entry :: preOrderTraversal(left) ::: preOrderTraversal(right)
  }

  /**
   * Reconstruct a binary tree from its in-order and pre-order traversals in O(n * depth)
   * Assume elements in tree are unique
   * require (inOrder.length == preOrder.length)
   *
   * @param inOrder in-order traversal of binary tree
   * @param preOrder pre-order traversal of binary tree
   * @return reconstructed tree
   */
  def reconstruct[T](inOrder: Seq[T], preOrder: Seq[T]): Tree[T] = preOrder match {
    case Nil => None
    case root :: children =>
      val (leftIn, head :: rightIn) = inOrder splitAt (inOrder indexOf root)
      val (leftPre, rightPre) = children splitAt leftIn.length
      Some(Node(reconstruct(leftIn, leftPre), root, reconstruct(rightIn, rightPre)))
  }
}
