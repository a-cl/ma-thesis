package print

import java.io.{File, PrintWriter}

import select.Test

class Stats(val tests: List[Test]) {

  def diff: Int = total - same

  def total: Int = tests.length

  def same: Int = tests.count(test => test.isSimilar())

}

trait Printer {

  /**
    * The path to the file that will be created by the printer.
    *
    * @return String
    */
  def RESULT_PATH: String

  /**
    * Prints a List of Tests. The concrete printing method depends on
    * the implementation.
    *
    * @param tests The tests to Print
    */
  def print(tests: List[Test])

  protected def getExtendedName(file: File): String = {
    file.getParentFile.getName + "/" + file.getName
  }

  protected def getPrintWriter (): PrintWriter = {
    new PrintWriter(RESULT_PATH, "UTF-8")
  }

}

