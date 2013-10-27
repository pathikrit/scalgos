package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

class CounterSpec extends Specification {
  "Counter" should {
    "count" in {
      val c = Counter.empty[String]
      c("world") must be equalTo 0
      c("hello") must be equalTo 0
      c("hello") = 2
      c("hello") must be equalTo 2
      c -= "hello"
      c("hello") must be equalTo 1
      c -= "hello"
      c("hello") must be equalTo 0
      c += ("hello", "world", "world")
      c("hello") must be equalTo 1
      c("world") must be equalTo 2
      c -= ("hello", "hello")
      c("hello") must be equalTo -1
    }

    "support map methods" in {
      val c = Counter("hello", "world", "hello", "world2")
      c count {_._2 > 1} must be equalTo 1
    }
  }
}
