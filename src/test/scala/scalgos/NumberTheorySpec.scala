package scalgos

import scalgos.NumberTheory._

class NumberTheorySpec extends ScalgosSpec {

  "catalan" should {
    "match known sequence" in {
      val expected: List[BigInt] = List(1, 1, 2, 5, 14, 42, 132, 429, 1430, 4862, 16796)
      ((0 to 10) map catalan).toList must be equalTo(expected)
    }
  }
}
