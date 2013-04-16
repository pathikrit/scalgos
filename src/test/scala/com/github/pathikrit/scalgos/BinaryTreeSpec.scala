package com.github.pathikrit.scalgos

import org.specs2.mutable._

import BinaryTree._
import util.Random

class BinaryTreeSpec extends Specification {

  "reconstructBST" should {
    "do a round-trip" in {
      val preOrder = RandomData.seq()
      preOrderTraversal(reconstructBST(preOrder)) must be equalTo preOrder
    }.pendingUntilFixed
  }

  "reconstruct" should {
    "do a round-trip" in {
      val inOrder = RandomData.seq().distinct
      val preOrder = Random.shuffle(inOrder)
      preOrderTraversal(reconstruct(inOrder, preOrder)) must be equalTo preOrder
    }
  }
}
