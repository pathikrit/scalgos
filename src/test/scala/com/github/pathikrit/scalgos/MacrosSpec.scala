package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Macros._

class MacrosSpec extends Specification {

  "profile" should {
    "work" in {
      val (result, time, memory) = profile {
        val x = 20
        def fib(n: Int): Int = if (n <= 1) n else fib(n-1) + fib(n-2)
        fib(x)
      }
      result must be equalTo 6765
      time must be greaterThan 0
      memory must be greaterThan 32
    }
  }

  "download" should {
    "work" in todo
  }

  "lruCache" should {
    "work" in todo
  }
}
