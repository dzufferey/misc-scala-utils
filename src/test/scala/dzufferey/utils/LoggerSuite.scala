package dzufferey.utils

import org.scalatest._

import LogLevel._

class LoggerSuite extends FunSuite {
     
  test("lazy evaluation of message with priority") {
    val logger = new Logger
    logger.setMinPriority(Debug)
    intercept[java.lang.RuntimeException] {
      logger("LoggerSuite", Debug, sys.error("should happen"): String)
    }
    logger.setMinPriority(Critical)
    logger("LoggerSuite", Error, sys.error("should not happen"): String)
    logger.reset
    intercept[java.lang.RuntimeException] {
      logger("LoggerSuite", Error, sys.error("should happen"): String)
    }
  }

  test("lazy evaluation of message with disallow && allow message") {
    val logger = new Logger
    intercept[java.lang.RuntimeException] {
      logger("LoggerSuite", Notice, sys.error("should happen"): String)
    }
    logger.disallow("LoggerSuite2")
    intercept[java.lang.RuntimeException] {
      logger("LoggerSuite", Notice, sys.error("should happen"): String)
    }
    logger.disallow("LoggerSuite")
    logger("LoggerSuite", Info, sys.error("should not happen"): String)
    logger.allow("LoggerSuite")
    intercept[java.lang.RuntimeException] {
      logger("LoggerSuite", Notice, sys.error("should happen"): String)
    }
    logger.reset
    intercept[java.lang.RuntimeException] {
      logger("LoggerSuite2", Notice, sys.error("should happen"): String)
    }
  }

  test("logAndThrow") {
    val logger = new Logger
    intercept[java.lang.RuntimeException] {
      logger.logAndThrow("LoggerSuite", Notice, "should throw an exception")
    }
  }

}

