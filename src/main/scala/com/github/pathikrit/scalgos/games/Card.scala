package com.github.pathikrit.scalgos.games

import collection.mutable

import com.github.pathikrit.scalgos.Implicits.Crossable

/**
 * Class to model a playing card
 */
case class Card(rank: Int, suit: Int) {
  override def toString = s"${Card ranks rank}${Card suits suit}"
}

object Card {
  implicit val rankOrdering = Ordering by {card: Card => card.rank}

  val (ranks, suits) = ("23456789TJQKA", "♣♠♦♥")

  val all = for {(rank, suit) <- ranks.indices X suits.indices} yield Card(rank, suit)

  def fromStr(s: String) = Card(ranks indexOf s(0).toUpper, "CSDH" indexOf s(1).toUpper)
}

/**
 * Class to model a deck of cards
 */
class Deck {
  val cards = mutable.Queue() ++ util.Random.shuffle(Card.all)

  def deal = cards.dequeue()

  def remove(discards: Set[Card]) = discards foreach {card => cards dequeueFirst {_ == card}}
}

/**
 * A class to model hand types in poker
 */
object PokerHandType extends Enumeration {
  val HighCard, OnePair, TwoPair, ThreeOfAKind, Straight, Flush, FullHouse, FourOfAKind, StraightFlush = Value

  /**
   * TODO: memoize this/bithack version
   * @return (h,s) where h is the hand type and s is sorted cards to break ties
   */
  def classify(hand: Set[Card]) = {
    def rankMatches(card: Card) = hand count {_.rank == card.rank}
    val groups = hand groupBy rankMatches mapValues {_.toList.sorted}

    val isFlush = (hand groupBy {_.suit}).size == 1
    val isWheel = "A2345" forall {r => hand exists {_.rank == Card.ranks.indexOf(r)}}   // A,2,3,4,5 straight
    val isStraight = groups.size == 1 && (hand.max.rank - hand.min.rank) == 4 || isWheel
    val (isThreeOfAKind, isOnePair) = (groups contains 3, groups contains 2)

    val handType = if (isStraight && isFlush)     StraightFlush
      else if (groups contains 4)                 FourOfAKind
      else if (isThreeOfAKind && isOnePair)       FullHouse
      else if (isFlush)                           Flush
      else if (isStraight)                        Straight
      else if (isThreeOfAKind)                    ThreeOfAKind
      else if (isOnePair && groups(2).size == 4)  TwoPair
      else if (isOnePair)                         OnePair
      else                                        HighCard

    val kickers = (1 until 5 flatMap groups.get).flatten.reverse
    require(hand.size == 5 && kickers.size == 5)
    (handType, if (isWheel) (kickers takeRight 4) :+ kickers.head else kickers)
  }

  import scala.math.Ordering.Implicits._
  implicit val handOrdering: Ordering[Set[Card]] = Ordering.by(classify)
}
