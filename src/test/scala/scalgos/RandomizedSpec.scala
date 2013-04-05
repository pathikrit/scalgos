package scalgos

import scalgos.Randomized._

class RandomizedSpec extends ScalgosSpec {

  "quickSelect" should {
    "match naive sort based algorithm" in {
      val (s, k) = (randomSeq(500, -100, 100), randomInteger(end = 100))
      quickSelect(s, k) must be equalTo(s.sorted.apply(k))  //TODO: why apply?
    }

    todo("match median-of-medians algorithm")
  }

}
