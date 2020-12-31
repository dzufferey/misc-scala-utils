package dzufferey.utils

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

//logging facility (i.e. syslog alike)

object LogLevel {
    sealed abstract class Level(msg: String, prio: Int, col: String) {
      def message = msg
      def priority = prio
      def color = col
    }
    case object Critical extends Level("Critical", 32, Console.RED)
    case object Error    extends Level("Error",    16, Console.RED)
    case object Warning  extends Level("Warning",  8,  Console.YELLOW)
    case object Notice   extends Level("Notice",   4,  Console.BLUE)
    case object Info     extends Level("Info",     2,  Console.RESET)
    case object Debug    extends Level("Debug",    1,  Console.RESET)
}

import LogLevel._
  
/** Simple logger that outputs to stdout. */
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
  def apply(relatedTo: String, lvl: Level): Boolean =
    lvl.priority >= minPriority && !disallowed(relatedTo)

  //The evaluation of the content should *NOT* print. It can cause deadlocks.

  /** Log a message to the console.
   * @param relatedTo The package/file/class from where this message comes from.
   * @param lvl The priority of the message.
   * @param content The content of the message (evaluated only if needed).
   */
  def apply(relatedTo: String, lvl: Level, content: String): Unit = macro LoggerMacros.string
  
  def apply(relatedTo: String, lvl: Level, content: java.io.BufferedWriter => Unit): Unit = macro LoggerMacros.writer

  /** Log a message and throw an exception with the content. */
  def logAndThrow(relatedTo: String, lvl: Level, content: String): Nothing = macro LoggerMacros.logAndThrow

  def assert(cond: Boolean, relatedTo: String, content: String): Unit = macro LoggerMacros.assert

}

class LoggerMacros(val c: Context) {
  import c.universe._

  val isEnabled = System.getProperty("disableLogging") != "true"

  def string(relatedTo: c.Expr[String], lvl: c.Expr[Level], content: c.Expr[String]): c.Expr[Unit] = {
    val tree = if (isEnabled) {
        q"""
        if (dzufferey.utils.Logger($relatedTo, $lvl)) {
          val prefix = "[" + $lvl.color + $lvl.message + scala.Console.RESET + "]" + " @ " + $relatedTo + ": " 
          val indented = dzufferey.utils.Misc.indent(prefix, $content)
          dzufferey.utils.Logger.lock.lock
          try {
            scala.Console.println(indented)
          } finally {
            dzufferey.utils.Logger.lock.unlock
          }
        }
        """
      } else q"()"
    c.Expr[Unit](tree)
  }

  def writer(relatedTo: c.Expr[String], lvl: c.Expr[Level], content: c.Expr[java.io.BufferedWriter => Unit]): c.Expr[Unit] = {
    val tree = if (isEnabled) {
        q"""
        if (dzufferey.utils.Logger($relatedTo, $lvl)) {
          val prefix = "[" + $lvl.color + $lvl.message + scala.Console.RESET + "]" + " @ " + $relatedTo + ": " 
          val writer = new java.io.BufferedWriter(new dzufferey.utils.PrefixingWriter(prefix, scala.Console.out))
          dzufferey.utils.Logger.lock.lock
          try {
            $content(writer)
            writer.flush
          } finally {
            dzufferey.utils.Logger.lock.unlock
          }
        }
        """
      } else q"()"
    c.Expr[Unit](tree)
  }

  def logAndThrow(relatedTo: c.Expr[String], lvl: c.Expr[Level], content: c.Expr[String]): c.Expr[Nothing] = {
    val tree = if (isEnabled) {
        q"""
        {
          val c = $content
          dzufferey.utils.Logger($relatedTo, $lvl, c)
          scala.Console.flush()
          sys.error(c)
        }
        """
      } else {
        q"""
        sys.error($content)
        """
      }
    c.Expr[Nothing](tree)
  }

  def assert(cond: c.Expr[Boolean], relatedTo: c.Expr[String], content: c.Expr[String]): c.Expr[Unit] = {
    val tree = if (isEnabled) {
        q"""
        if (!$cond) {
          dzufferey.utils.Logger.logAndThrow($relatedTo, dzufferey.utils.LogLevel.Error, $content)
        }
        """
      } else q"()"
    c.Expr[Unit](tree)
  }

}
