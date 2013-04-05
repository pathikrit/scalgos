package scalgos

import scalgos.Randomized._

class RandomizedSpec extends ScalgosSpec {

  "quickSelect" should {
    "match naive sort based algorithm" in {
      val s = randomSeq()
      val k = randomInteger(0, s.length)
      quickSelect(s, k) must be equalTo(s.sorted.apply(k))  //TODO: why apply?
    }

    TODO("match median-of-medians algorithm")
  }

}
