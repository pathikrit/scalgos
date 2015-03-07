package com.github.pathikrit.scalgos.games

import org.specs2.mutable.Specification

import Card._
import PokerHandType._

class CardSpec extends Specification {

  "hand evaluation" should {

    implicit def toCardSeq(s: String): Seq[Card] = (s split " " map fromStr).toSeq

    "work" in {
      def test(cards: String, expectedHand: PokerHandType.Value, kickers: Seq[Card]) {
        val (hand, sorted) = classify(toCardSeq(cards).toSet)
        hand must be equalTo expectedHand
        sorted zip kickers foreach {p => p._1.rank must be equalTo p._2.rank}
      }

      // todo: use datatables
      examplesBlock {
        test("qc kc tc ac jc", StraightFlush , "Ac kc qc jc tc")
        test("5c 4c 3c ac 2c", StraightFlush , "5c 4c 3c 2c ac")
        test("5c 4c 3c 6c 2c", StraightFlush , "6c 5c 4c 3c 2c")
        test("5d As 5c 5h 5s", FourOfAKind   , "5s 5c 5d 5h As")
        test("Td KH tc Th ks", FullHouse     , "Td Th Tc Kh Ks")
        test("qc kc 9c ac jc", Flush         , "Ac kc qc jc 9c")
        test("2c 5H 4S 3D ad", Straight      , "5h 4s 3d 2c ac")
        test("kc qH aS jD td", Straight      , "as kc QH JD TD")
        test("9c AH AS AD 8d", ThreeOfAKind  , "AH AS AD 9c 8d")
        test("ac 2s 2d 3s 3d", TwoPair       , "3s 3d 2s 2d ac")
        test("3d 3s ah td js", OnePair       , "3s 3d ah js td")
        test("2d 3s ah td js", HighCard      , "ah js td 3s 2d")
      }
    }
  }
}
