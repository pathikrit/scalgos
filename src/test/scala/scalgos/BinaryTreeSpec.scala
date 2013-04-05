package scalgos

import scalgos.BinaryTree._

class BinaryTreeSpec extends ScalgosSpec {

  "reconstructBST" should {
    "do a round-trip" in {
      val preOrder = Seq(5, 3, 1, 2, 6)
      preOrderTraversal(reconstructBST(preOrder)) must be equalTo(preOrder)
    }
  }

  "reconstruct" should {
    "do a round-trip" in {
      val (inOrder, preOrder) = (Seq(1, 2, 3, 4, 5), Seq(5, 4, 3, 2, 1))
      preOrderTraversal(reconstruct(inOrder, preOrder)) must be equalTo(preOrder)
    }
  }
}
