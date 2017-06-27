package main

import print.{HTMLPrinter, JSONPrinter, TextPrinter, XLSXPrinter}
import select._

/**
  * Runs the test selector. How to:
  *
  * 1. Create a set of directories to look in for images
  * (not recursively).
  * 2. Create a strategy for picking image pairs
  * a) PickEverySelection - Use every image once (Default)
  * b) PickFixedAmountStrategy - Fixed number of tests
  * 3. Create Selector with strategy.
  * 4. Define printers for various output formats
  * a) HTML - HTMLPrinter
  * b) JSON - JSONPrinter
  * c) Console - ConsolePrinter
  * 5. Run the selector on the sets to create tests.
  * 6. Use printers to save generated tests.
  */
object Main {

  private val htmlPrinter = new HTMLPrinter
  private val jsonPrinter = new JSONPrinter
  private val xlsxPrinter = new XLSXPrinter
  private val textPrinter = new TextPrinter

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      throw new Exception("Usage: command numberOfTests classDirectories...")
    }

    val command = args(0)
    val testCount = Integer.parseInt(args(1))
    val sets = args.splitAt(2)._2.toList

    command.toLowerCase match {
      case "even" => generateEvenDistribution(sets, testCount)
      case "rand" => generateRandomDistribution(sets, testCount)
      case _ => throw new Exception("Unknown distribution type: " + command)
    }
  }

  private def generateRandomDistribution(sets: List[String], count: Int) = {
    val strategy = new PickFixedAmountStrategy(count)
    val selector = new Selector(strategy)

    printTests(selector.selectTests(sets))
  }

  private def generateEvenDistribution(sets: List[String], count: Int) = {
    val sameStrategy = new PickSameClassStrategy(count / 2)
    val diffStrategy = new PickDiffClassStrategy(count / 2)
    val selectorSame = new Selector(sameStrategy)
    val selectorDiff = new Selector(diffStrategy)

    val same = selectorSame.selectTests(sets)
    val diff = selectorDiff.selectTests(sets)

    printTests(same.++(diff))
  }

  private def printTests (tests: List[Test]) = {
    htmlPrinter.print(tests)
    jsonPrinter.print(tests)
    xlsxPrinter.print(tests)
    textPrinter.print(tests)
  }

}
