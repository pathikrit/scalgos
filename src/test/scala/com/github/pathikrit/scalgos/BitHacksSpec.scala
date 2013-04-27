package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import Implicits.Crossable
import BitHacks._

class BitHacksSpec extends Specification {

  "flipCase" should {
    "work for characters" in todo
//    {
//      val lower = "abcdefghijklmnopqrstuvwxyz"
//      val upper = lower map flipCase
//      upper must be equalTo lower.toUpperCase
//      val lower2 = upper map flipCase
//      lower2 must be equalTo lower
//    }

    "fail for non characters" in todo
  }

  "swap" should {
    "work" in {
      for (v <- (-100 to 100) X (-100 to 100)) {
        xorSwap(v) must be equalTo v.swap
        noTempSwap(v) must be equalTo v.swap
      }
    }

    "work for Int.MaxValue & Int.MinValue" in todo
  }
}
