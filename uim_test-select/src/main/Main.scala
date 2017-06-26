package main

import print.{HTMLPrinter, JSONPrinter, XLSXPrinter}
import select.{PickDiffClassStrategy, PickFixedAmountStrategy, PickSameClassStrategy, Selector}

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

  private val EVEN_DIST = "even"
  private val RAND_DIST = "rand"

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      throw new Exception("Usage: command numberOfTests classDirectories...")
    }

    val command = args(0)
    val testCount = Integer.parseInt(args(1))
    val sets = args.splitAt(2)._2.toList

    command.toLowerCase match {
      case EVEN_DIST => generateEvenDistribution(sets, testCount)
      case RAND_DIST => generateRandomDistribution(sets, testCount)
    }
  }

  private def generateRandomDistribution(sets: List[String], count: Int) = {
    val strategy = new PickFixedAmountStrategy(count)
    val selector = new Selector(strategy)
    val tests = selector.selectTests(sets)

    htmlPrinter.print(RAND_DIST, tests)
    jsonPrinter.print(RAND_DIST, tests)
    xlsxPrinter.print(RAND_DIST, tests)
  }

  private def generateEvenDistribution(sets: List[String], count: Int) = {
    val sameStrategy = new PickSameClassStrategy(count / 2)
    val diffStrategy = new PickDiffClassStrategy(count / 2)
    val selectorSame = new Selector(sameStrategy)
    val selectorDiff = new Selector(diffStrategy)

    val same = selectorSame.selectTests(sets)
    val diff = selectorDiff.selectTests(sets)
    val tests = same.++(diff)

    htmlPrinter.print(EVEN_DIST, tests)
    jsonPrinter.print(EVEN_DIST, tests)
    xlsxPrinter.print(EVEN_DIST, tests)
  }

}
