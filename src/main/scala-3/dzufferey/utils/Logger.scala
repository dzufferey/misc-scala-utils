package dzufferey.utils

import scala.quoted.{Quotes, Expr}
import LogLevel._

object Logger {

  val lock = new java.util.concurrent.locks.ReentrantLock

  private var minPriority = Notice.priority
  val disallowed = scala.collection.mutable.HashSet.empty[String]

  def reset = {
    minPriority = Info.priority
    disallowed.clear()
  }
  def getMinPriority = minPriority match {
    case x if x == Critical.priority => Critical
    case x if x == Error.priority =>    Error
    case x if x == Warning.priority =>  Warning
    case x if x == Notice.priority =>   Notice
    case x if x == Info.priority =>     Info
    case x if x == Debug.priority =>    Debug
    case p => sys.error("unknown priority ("+p+")")
  }
  def setMinPriority(lvl: Level) = minPriority = lvl.priority
  def setMinPriority(lvl: Int) = minPriority = lvl
  def disallow(str: String) = disallowed += str
  def allow(str: String) = disallowed -= str

  private def increaseLevel(l: Level): Level = l match {
    case Critical => Error
    case Error    => Warning
    case Warning  => Notice
    case Notice   => Info
    case Info     => Debug
    case Debug    => Debug
  }

  private def decreaseLevel(l: Level): Level = l match {
    case Critical => Critical
    case Error    => Critical
    case Warning  => Error
    case Notice   => Warning
    case Info     => Notice
    case Debug    => Info
  }

  def moreVerbose = setMinPriority( increaseLevel(getMinPriority))

  def lessVerbose = setMinPriority( decreaseLevel(getMinPriority))

  /** Should be dispayed ? */
  inline def apply(relatedTo: String, lvl: Level): Boolean =
    lvl.priority >= minPriority && !disallowed(relatedTo)

  //The evaluation of the content should *NOT* print. It can cause deadlocks.

  //FIXME with scala3, I to call content by name but the macros should take care of that ?!?

  /** Log a message to the console.
   * @param relatedTo The package/file/class from where this message comes from.
   * @param lvl The priority of the message.
   * @param content The content of the message (evaluated only if needed).
   */
  inline def apply(relatedTo: String, lvl: Level, content: => String): Unit = ${ LoggerMacros.string('relatedTo, 'lvl, 'content) }

  inline def apply(relatedTo: String, lvl: Level, content: java.io.BufferedWriter => Unit): Unit = ${ LoggerMacros.writer('relatedTo, 'lvl, 'content) }

  /** Log a message and throw an exception with the content. */
  inline def logAndThrow(relatedTo: String, lvl: Level, content: => String): Nothing = ${ LoggerMacros.logAndThrow('relatedTo, 'lvl, 'content) }

  inline def assert(cond: Boolean, relatedTo: String, content: => String): Unit = ${ LoggerMacros.assert('cond, 'relatedTo, 'content) }

}

object LoggerMacros {

  def string(relatedTo: Expr[String], lvl: Expr[Level], content: Expr[String])(using Quotes): Expr[Unit] = {
    if (System.getProperty("disableLogging") != "true") {
      '{
        if (dzufferey.utils.Logger($relatedTo, $lvl)) {
          val prefix = "[" + $lvl.color + $lvl.message + scala.Console.RESET + "]" + " @ " + $relatedTo + ": "
          val writer = new java.io.BufferedWriter(new dzufferey.utils.PrefixingWriter(prefix, scala.Console.out))
          dzufferey.utils.Logger.lock.lock
          try {
            writer.write($content)
            writer.append('\n')
            writer.flush()
          } finally {
            dzufferey.utils.Logger.lock.unlock
          }
        }
      }
    } else {
      '{ () }
    }
  }

  def writer(relatedTo: Expr[String], lvl: Expr[Level], content: Expr[java.io.BufferedWriter => Unit])(using Quotes): Expr[Unit] = {
    if (System.getProperty("disableLogging") != "true") {
      '{
        if (dzufferey.utils.Logger($relatedTo, $lvl)) {
          val prefix = "[" + $lvl.color + $lvl.message + scala.Console.RESET + "]" + " @ " + $relatedTo + ": "
          val writer = new java.io.BufferedWriter(new dzufferey.utils.PrefixingWriter(prefix, scala.Console.out))
          dzufferey.utils.Logger.lock.lock
          try {
            $content(writer)
            writer.flush()
          } finally {
            dzufferey.utils.Logger.lock.unlock
          }
        }
      }
    } else {
      '{ () }
    }
  }

  def logAndThrow(relatedTo: Expr[String], lvl: Expr[Level], content: Expr[String])(using Quotes): Expr[Nothing] = {
    if (System.getProperty("disableLogging") != "true") {
      '{
        {
          val c = $content
          dzufferey.utils.Logger($relatedTo, $lvl, c)
          scala.Console.flush()
          sys.error(c)
        }
      }
    } else {
      '{ sys.error($content) }
    }
  }

  def assert(cond: Expr[Boolean], relatedTo: Expr[String], content: Expr[String])(using Quotes): Expr[Unit] = {
    if (System.getProperty("disableLogging") != "true") {
      '{
        if (!$cond) {
          dzufferey.utils.Logger.logAndThrow($relatedTo, dzufferey.utils.LogLevel.Error, $content)
        }
      }
    } else {
      '{ () }
    }
  }

}
