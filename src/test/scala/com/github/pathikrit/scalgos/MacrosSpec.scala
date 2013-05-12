package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Macros._

import java.io.{ByteArrayOutputStream, PrintStream}

class MacrosSpec extends Specification {
  "profile" should {
    "work" in todo
  }

  "download" should {
    "work" in todo
  }

  "lruCache" should {
    "work" in todo
  }

  "debug" should {
    def captureOutput(f: => Unit) = {
      val stream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(stream))(f)
      stream.close()
      stream.toString
    }

    "work for 0 inputs" in todo
//    {
//      captureOutput {Macros.debug()} must be equalTo "\n"
//    }

    "work for 1 input" in {
      captureOutput {
        val y = 7
        val x = y
        Macros.debug(x)
      } must be equalTo "x = 7\n"
    }

    "work for arbitrary inputs" in {
      captureOutput {
        val (a, b, c) = (2, 4.513, "hello")
        Macros.debug(a, b, c, a+b, "I am a literal")
      } must be equalTo "a = 2, b = 4.513, c = hello, a.+(b) = 6.513, I am a literal\n"
    }
  }
}
