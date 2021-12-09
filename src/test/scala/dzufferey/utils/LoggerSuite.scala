package dzufferey.utils

import org.scalatest.funsuite.AnyFunSuite

import LogLevel._

class LoggerSuite extends AnyFunSuite {

  test("lazy evaluation of message with priority") {
    Logger.setMinPriority(Debug)
    intercept[java.lang.RuntimeException] {
      Logger("LoggerSuite", Debug, sys.error("should happen"): String)
    }
    Logger.setMinPriority(Critical)
    assert(Logger("LoggerSuite", Critical))
    assert(!Logger("LoggerSuite", Error))
    Logger("LoggerSuite", Error, sys.error("should not happen"): String)
    Logger.reset
    intercept[java.lang.RuntimeException] {
      Logger("LoggerSuite", Error, sys.error("should happen"): String)
    }
  }

  test("lazy evaluation of message with disallow && allow message") {
    intercept[java.lang.RuntimeException] {
      Logger("LoggerSuite", Notice, sys.error("should happen"): String)
    }
    Logger.disallow("LoggerSuite2")
    intercept[java.lang.RuntimeException] {
      Logger("LoggerSuite", Notice, sys.error("should happen"): String)
    }
    Logger.disallow("LoggerSuite")
    Logger("LoggerSuite", Info, sys.error("should not happen"): String)
    Logger.allow("LoggerSuite")
    intercept[java.lang.RuntimeException] {
      Logger("LoggerSuite", Notice, sys.error("should happen"): String)
    }
    Logger.reset
    intercept[java.lang.RuntimeException] {
      Logger("LoggerSuite2", Notice, sys.error("should happen"): String)
    }
  }

  test("logAndThrow") {
    intercept[java.lang.RuntimeException] {
      Logger.logAndThrow("LoggerSuite", Notice, "should throw an exception")
    }
  }

}

