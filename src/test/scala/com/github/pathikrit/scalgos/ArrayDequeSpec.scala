package com.github.pathikrit.scalgos

import com.github.pathikrit.scalgos.Implicits._

import org.specs2.mutable.Specification

import scala.collection.mutable

class ArrayDequeSpec extends Specification {
  "circular-buffer" should {
    "match the library" in {
      val buffer = ArrayDeque.empty[Int]
      val buffer2 = mutable.ArrayBuffer.empty[Int]

      def apply[U](f: mutable.Buffer[Int] => U) = {
        //println(s"Before: [buffer1=${buffer}; buffer2=${buffer2}]")
        f(buffer) shouldEqual f(buffer2)
        buffer shouldEqual buffer2
      }

      apply(_.append(1, 2, 3, 4, 5))
      apply(_.prepend(6, 7, 8))
      apply(_.trimStart(2))
      apply(_.trimEnd(3))
      apply(_.insertAll(0, Seq(9, 10, 11)))
      apply(_.insertAll(1, Seq(12, 13)))
      apply(_.remove(2))
      apply(_.prependAll(Seq(14, 15, 16, 17)))
      apply(_.remove(1, 5))
      apply(_.prependAll(Seq.tabulate(100)(identity)))
      buffer.trimToSize()
      apply(_.appendAll(Seq.tabulate(100)(identity)))

      examplesBlock {
        (-100 to 100) foreach {i =>
          buffer.splitAt(i) shouldEqual buffer2.splitAt(i)
        }
      }

      examplesBlock {
        (-100 to 100) X (-100 to 100) foreach {case (i, j) =>
          buffer.slice(i, j) shouldEqual buffer2.slice(i, j)
          if (i >= 1 && j >= 1 && j >= i) buffer.sliding(i, j).toList shouldEqual buffer2.sliding(i, j).toList
        }
      }
    }
  }
}
