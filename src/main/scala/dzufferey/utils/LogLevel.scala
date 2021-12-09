package dzufferey.utils

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

