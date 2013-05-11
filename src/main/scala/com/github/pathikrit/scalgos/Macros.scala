package com.github.pathikrit.scalgos

import System.{currentTimeMillis => time}
import io.Source
import collection.mutable
import collection.JavaConversions._
import language.experimental.macros
import reflect.macros.Context

/**
 * Collection of code snippets that do common tasks such as profiling, downloading a webpage etc
 */
object Macros {

  /**
   * Profile a block of code
   * e.g. use val (result, time, mem) = profile { ... code ... }
   *
   * @param code the block of code to profile
   * @param t by default is the current time
   * @tparam R return type of
   * @return (res, time, mem) where res is result of profiled code, time is time used in s and mem is memory used in KB
   */
  def profile[R](code: => R, t: Long = time) = (code, (time - t)/1000, Runtime.getRuntime.totalMemory>>10)

  /**
   * @return the contents of url (usually html)
   */
  def download(url: String) = (Source fromURL url).mkString

  /**
   * An LRU cache
   * @param maxEntries maximum number of entries to retain
   */
  def lruCache[A,B](maxEntries: Int): mutable.Map[A,B] = new java.util.LinkedHashMap[A,B]() {
    override def removeEldestEntry(eldest: java.util.Map.Entry[A,B]) = this.size > maxEntries
  }

  /**
   * Useful in debugging - prints variable names alongside values (also supports expressions e.g. debug(a+b))
   */
  def debug(params: Any*) = macro debugImpl

  def debugImpl(c: Context)(params: c.Expr[Any]*) = {
    import c.universe._

    val trees = params map {param => param.tree match {
        case Literal(Constant(const)) => reify { print(param.splice) }.tree
        case _ => reify {
          val variable = c.Expr[String](Literal(Constant(show(param.tree)))).splice
          print(variable + " = " + param.splice)
        }.tree
      }
    }

    val separators = (1 until trees.size).map(_ => (reify { print(", ") }).tree) :+ (reify { println() }).tree
    val treesWithSeparators = trees zip separators flatMap {p => List(p._1, p._2)}

    c.Expr[Unit](Block(treesWithSeparators.toList, Literal(Constant(()))))
  }
}
