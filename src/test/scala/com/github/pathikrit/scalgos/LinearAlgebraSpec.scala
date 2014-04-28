package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

import RandomData.matrix

import LinearAlgebra._

class LinearAlgebraSpec extends Specification {

  "multiply" should {

    "work for 0/1 matrices" in todo

    "fail for incorrect size" in todo

    "work" in {
      val (a, b) = (matrix(15, 20), matrix(20, 30))
      val c = Array.ofDim[Double](a.rows, b.cols)

      for {
        i <- 0 until a.rows
        j <- 0 until b.cols
        k <- 0 until a.cols
      } c(i)(j) = c(i)(j) + a(i, k) * b(k, j)

      todo
    }
  }

  "evalPolynomial" should {

    "work for 0/1 length coeffs" in todo

    "match definition" in {
      def recursiveEval(coeffs: List[Double])(x: Double): Double = coeffs match {
        case Nil => 0
        case c :: cs => c + x*recursiveEval(cs)(x)
      }

      val n = 10
      val a = ((1 to n) map {i => RandomData.number(0, 10)}).toList
      val x = RandomData.number(-5, 5)
      Polynomial(a)(x) must be ~(recursiveEval(a)(x) +/- 1e-9)
    }
  }
}
