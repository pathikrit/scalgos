package com.github.pathikrit.scalgos

import scala.Ordering.Implicits._

/**
 * Collection of algorithms pertaining to Binary Trees
 */
object BinaryTree {

  /**
   * A Binary Search Tree
   */
  sealed trait BST[A] {
    val value: A
  }

  /**
   * A way to represent a full BST i.e. no nodes with 1 child
   */
  object BST {

    /**
     * An internal node of the full BST
     */
    case class Node[A: Ordering](left: BST[A], override val value: A, right: BST[A]) extends BST[A] {
      require(left.value <= value && value <= right.value)
    }

    /**
     * A leaf node of the full BST
     */
    case class Leaf[A: Ordering](override val value: A) extends BST[A]
  }

  /**
   * A BinaryTree
   */
  type Tree[A] = Option[Node[A]]

  /**
   * A binary tree node
   * @param left left sub-tree
   * @param entry the value stored at the node
   * @param right right sub-tree
   */
  case class Node[A](left: Tree[A], entry: A, right: Tree[A])

  /**
   * Reconstruct a BST from its pre-order traversal in O(n * depth)
   * Assume elements in BST are unique
   * O(n)
   *
   * @param preOrder pre-order traversal of BST
   * @return reconstructed BST
   */
  def reconstructBST[A: Ordering](preOrder: Seq[A]): Tree[A] = preOrder match {
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
  def preOrderTraversal[A](root: Tree[A]): List[A] = root match {
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
  def reconstruct[A](inOrder: Seq[A], preOrder: Seq[A]): Tree[A] = preOrder match {
    case Nil => None
    case root :: children =>
      val (leftIn, _ :: rightIn) = inOrder splitAt (inOrder indexOf root)
      val (leftPre, rightPre) = children splitAt leftIn.length
      Some(Node(reconstruct(leftIn, leftPre), root, reconstruct(rightIn, rightPre)))
  }
}
