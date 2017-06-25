package print

import java.io.File

import select.Test

class Stats(val tests: List[Test]) {

  def diff = total - same

  def total = tests.length

  def same = tests.count(test => test.isSimilar)

}

trait Printer {

  /**
    * Prints a List of Tests. The concrete printing method depends on
    * the implementation.
    *
    * @param tests The tests to Print
    */
  def print(kind: String, tests: List[Test])

  protected def getExtendedName(file: File): String = {
    file.getParentFile.getName + "/" + file.getName
  }

}
