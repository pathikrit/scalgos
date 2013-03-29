package scalgos.games

import scala.collection.mutable

case class Card(rank: Int, suit: Int) { override def toString = s"${Card ranks rank}${Card suits suit}" }

object Card {
  implicit val rankOrdering = Ordering by {card: Card => card.rank}
  val (ranks, suits) = ("23456789TJQKA", "♣♠♦♥")
  val all = for {rank <- 0 until ranks.length; suit <- 0 until suits.length} yield Card(rank, suit)
  implicit def fromStr(s: String) = Card((ranks indexOf s(0).toUpper), ("CSDH" indexOf s(1).toUpper))
}

class Deck {
  val cards = mutable.Queue() ++ util.Random.shuffle(Card.all)
  def deal = cards dequeue
  def remove(discards: Set[Card]) { discards foreach {card: Card => cards dequeueFirst (_ == card)} }
}

object PokerHandType extends Enumeration {
  val HighCard, OnePair, TwoPair, ThreeOfAKind, Straight, Flush, FullHouse, FourOfAKind, StraightFlush = Value

  def classify(hand: Set[Card]) = {
    def rankMatches(card: Card) = hand count (_.rank == card.rank)
    val groups = hand groupBy rankMatches mapValues {_.toList.sorted}

    val isFlush = (hand groupBy {_.suit}).size == 1
    val isWheel = "A2345" forall {r => hand exists (_.rank == Card.ranks.indexOf(r))}   // A,2,3,4,5 straight
    val isStraight = groups.size == 1 && (hand.max.rank - hand.min.rank) == 4 || isWheel
    val (isThreeOfAKind, isOnePair) = (groups contains 3, groups contains 2)

    val handType = if (isStraight && isFlush)   StraightFlush
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
  implicit val handOrdering = Ordering by {hand: Set[Card] => classify(hand) }
}