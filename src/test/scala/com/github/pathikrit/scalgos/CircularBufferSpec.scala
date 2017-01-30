package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import scala.collection.mutable

class CircularBufferSpec extends Specification {
  "circular-buffer" should {
    "match the library" in {
      val buffer = new CircularBuffer[Int]()
      val buffer2 = mutable.ArrayBuffer.empty[Int]

      def apply[U](f: mutable.Buffer[Int] => U) = {
        //println(s"Before: [buffer1=${buffer}; buffer2=${buffer2}]")
        f(buffer)
        f(buffer2)
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
    }
  }
}
