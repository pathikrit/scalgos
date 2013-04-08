package scalgos

import scalgos.Randomized._

class RandomizedSpec extends RandomData {

  "quickSelect" should {
    "match naive sort based algorithm" in {
      val s = randomSeq()
      val k = randomInteger(0, s.length)
      quickSelect(s, k) must be equalTo(s.sorted.apply(k))
    }

    "match median-of-medians algorithm" in todo
  }

}
