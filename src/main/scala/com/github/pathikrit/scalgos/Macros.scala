package com.github.pathikrit.scalgos

import io.Source
import collection.mutable
import collection.JavaConversions._
import reflect.macros.Context

/**
 * Collection of code snippets that do common tasks such as profiling, downloading a webpage, debugging variables etc
 */
object Macros {

  /**
   * Profile a block of code
   * e.g. use val (result, time, mem) = profile { ... code ... }
   *
   * @param code the block of code to profile
   * @tparam R return type of
   * @return (res, time, mem) where res is result of profiled code, time is time used in ms and mem is memory used in MB
   */
  def profile[R](code: => R) = {
    import System.{currentTimeMillis => time}
    val t = time
    (code, time - t, Runtime.getRuntime.totalMemory>>20)
  }

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

  /**
   * Implementation of the debug macro
   */
  def debugImpl(c: Context)(params: c.Expr[Any]*) = {
    import c.universe._

    val trees = params map {param => (param.tree match {
        case Literal(Constant(_)) => reify { print(param.splice) }
        case _ => reify {
          val variable = c.Expr[String](Literal(Constant(show(param.tree)))).splice
          print(s"$variable = ${param.splice}")
        }
      }).tree
    }

    val separators = (1 until trees.size).map(_ => (reify { print(", ") }).tree) :+ (reify { println() }).tree
    val treesWithSeparators = trees zip separators flatMap {p => List(p._1, p._2)}

    c.Expr[Unit](Block(treesWithSeparators.toList, Literal(Constant(()))))
  }
}
