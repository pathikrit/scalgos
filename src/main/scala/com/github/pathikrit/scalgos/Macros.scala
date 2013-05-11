package com.github.pathikrit.scalgos

import System.{currentTimeMillis => time}
import io.Source
import collection.mutable
import collection.JavaConversions._

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
  def download(url: String) = Source.fromURL(url).mkString

  /**
   * An LRU cache
   * @param maxEntries maximum number of entries to retain
   */
  def lruCache[A,B](maxEntries: Int): mutable.Map[A,B] = new java.util.LinkedHashMap[A,B]() {
    override def removeEldestEntry(eldest: java.util.Map.Entry[A,B]) = this.size > maxEntries
  }
}
