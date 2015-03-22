package com.github.pathikrit.scalgos

import scala.io.Source
import scala.collection.mutable
import scala.collection.JavaConversions._

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
  def lruCache[A, B](maxEntries: Int): mutable.Map[A, B] = new java.util.LinkedHashMap[A, B]() {
    override def removeEldestEntry(eldest: java.util.Map.Entry[A, B]) = size > maxEntries
  }
}
