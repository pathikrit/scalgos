package com.github.pathikrit.scalgos

import scala.util.Random
import org.specs2.mutable.Specification

import BinaryTree._

class BinaryTreeSpec extends Specification {

  "reconstructBST" should {
    "do a round-trip" in {
      val preOrder = RandomData.list()
      preOrderTraversal(reconstructBST(preOrder)) must be equalTo preOrder
    }.pendingUntilFixed
  }

  "reconstruct" should {
    "do a round-trip" in {
      val inOrder = RandomData.list().distinct
      val preOrder = Random.shuffle(inOrder)
      preOrderTraversal(reconstruct(inOrder, preOrder)) must be equalTo preOrder
    }
  }
}
