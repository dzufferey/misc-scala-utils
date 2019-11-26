package dzufferey.utils

import scala.collection.mutable.HashMap

class Namer {

  private val map = new java.util.concurrent.ConcurrentHashMap[String, java.util.concurrent.atomic.AtomicInteger]()

  private def counter = new java.util.concurrent.atomic.AtomicInteger()
  
  def getPrefixAndVersion(prefix: String) = {
    val idx = prefix.lastIndexOf("$")
    if (idx == -1) {
      (prefix, 0)
    } else {
      try {
        (prefix.substring(0, idx), prefix.substring(idx+1).toInt)
      } catch {
        case e: java.lang.NumberFormatException =>
          (prefix, 0)
      }
    }
  }

  private def extractPrefix(prefix: String, preserve: Boolean) = {
    if (preserve) {
      (prefix, 0)
    } else {
      getPrefixAndVersion(prefix)
    }
  }

  private def getCounter(prefix: String) = {
    val c1 = counter
    val c2 = map.putIfAbsent(prefix, c1)
    if (c2 == null) c1 else c2
  }

  def warmup(prefix: String, preserve: Boolean = false): Unit = {
    val (realPrefix, current) = extractPrefix(prefix, preserve)
    val c3 = getCounter(realPrefix)
    var c = c3.get
    while (c < current) {
      c = c3.incrementAndGet
    }
  }

  def apply(prefix: String, preserve: Boolean = false): String = {
    val (realPrefix, current) = extractPrefix(prefix, preserve)
    val c3 = getCounter(realPrefix)
    var v = c3.incrementAndGet
    assert(current <= Int.MaxValue)
    while (v <= current) {
      v = c3.incrementAndGet
    }
    realPrefix.trim + "$" + v
  }

}

object Namer extends Namer {
}
