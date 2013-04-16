package scalgos

/**
 * Collection of number theory algorithms
 */
object NumberTheory {

  /**
   * Calculate catalan number
   * O(n*n) - each recursive step takes O(n) time
   *
   * @return memoized function to calculate nth catalan number
   */
  val catalan: Memo[Int, BigInt] = Memo {n => if (n == 0) 1 else (0 until n) map {i => catalan(i) * catalan(n-i-1)} sum}

  /**
   * Fibonacci number calculator
   * O(n) - each number is calculated once in O(1) time
   *
   * @return memoized function to calculate nth fibonacci number
   */
  val fibonacci: Memo[Int, BigInt] = Memo {n => if (n <= 1) n else fibonacci(n-1) + fibonacci(n-2)}
}
