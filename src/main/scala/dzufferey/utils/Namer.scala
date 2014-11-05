package dzufferey.utils

import scala.collection.mutable.HashMap

class Namer {

  private val map = new java.util.concurrent.ConcurrentHashMap[String, java.util.concurrent.atomic.AtomicInteger]()

  private def counter = new java.util.concurrent.atomic.AtomicInteger()

  def apply(prefix: String, preserve: Boolean = false): String = {
    val (realPrefix, current) =
      if (preserve) {
        (prefix, 0)
      } else {
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
    val c1 = counter
    val c2 = map.putIfAbsent(realPrefix, c1)
    val c3 = if (c2 == null) c1 else c2
    var v = c3.incrementAndGet
    while (v <= current) {
      v = c3.incrementAndGet
    }
    realPrefix.trim + "$" + v
  }

}

object Namer extends Namer {
}
