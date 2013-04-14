package scalgos

import org.specs2.mutable.Specification

import scalgos.MaxRectangleInHistogram.apply

class MaxAreaInHistogramSpec extends Specification {

  "maxRectangleUnderHistogram" should {
    "work for small inputs" in {
      apply(Seq((1, 1))) must be equalTo 1
      apply(Seq((1, 2))) must be equalTo 2
    }

    "work for zeroes" in {
      apply(Nil) must be equalTo 0
      apply(Seq((0, 0))) must be equalTo 0
      apply(Seq((5, 0))) must be equalTo 0
      apply(Seq((0, 0), (5, 0))) must be equalTo 0
    }

    "work for arbitrary input" in {
      val dims = Seq((1, 6), (5, 3), (1, 8), (9, 4), (3, 5), (2, 8), (18, 1), (2, 2), (1, 19), (10, 2))
      apply(dims) must be equalTo 58
    }

    "fail for negative inputs" in todo

    "match the divide and conquer algorithm" in {
      val blocks = RandomData.positiveSeq() zip RandomData.positiveSeq()
      val bars = for {b <- blocks; i <- 1 to b._2} yield b._1  //break block of width w into w blocks of width 1
      val expectedArea = DivideAndConquer.maxRectangleInHistogram(bars)
      apply(blocks) must be equalTo expectedArea
      apply(bars map {(_, 1)}) must be equalTo expectedArea
    }.pendingUntilFixed("TODO: This has a bug - run it 100s of time till failure on small inputs")
  }
}
