package main

import java.io.File

import print.{HTMLPrinter, JSONPrinter, TextPrinter, XLSXPrinter}
import select._

import scala.io.Source

/**
  * Runs the test selector. How to:
  *
  * [command] [testCount] [...sets]
  *
  * command:   gen (generate Tests)
  *            res (generate Results)
  * testCount: number of tests to create
  * sets:      paths of directories to use
  */
object Main {

  private val htmlPrinter = new HTMLPrinter
  private val jsonPrinter = new JSONPrinter
  private val xlsxPrinter = new XLSXPrinter
  private val textPrinter = new TextPrinter

  private val DEBUG = true

  def main(args: Array[String]): Unit = {
    val command = if (DEBUG) "res" else args(0)

    command.toLowerCase match {
      case "gen" => {
        val testCount = Integer.parseInt(args(1))
        val sets = args.splitAt(2)._2.toList
        generateTests(sets, testCount)
      }
      case "res" => {
        val filePath = if (DEBUG) "gen/tests.txt/" else args(1)
        generateExcel(filePath)
      }
    }
  }

  /**
    * Generates image test pairs and saves the result to /gen as a JSON, HTML
    * and TXT file.
    *
    * @param sets The directories to use for the candidates.
    * @param testCount The number of tests to create.
    */
  private def generateTests (sets: List[String], testCount: Int) = {
    val strategy = new PickEvenStrategy(testCount)
    val tests = new Selector(strategy).selectTests(sets)

    htmlPrinter.print(tests)
    jsonPrinter.print(tests)
    textPrinter.print(tests)
  }

  /**
    * Generates an Excel file from the output of a file by generateTests.
    * This is done, so previous result can be processed externally (running
    * tests to fill the similarity. Otherwise the generated Excel file does
    * not contain the results but NaN values instead.
    *
    * @param path The path to the generated file. At the moment only .txt
    *             ist supported.
    */
  private def generateExcel (path: String) = {
    val tests = Source.fromFile(path).getLines().toList.map { line =>
      val tokens = line.split(" ")
      val image1 = new File(tokens(0))
      val image2 = new File(tokens(1))
      val similarity = if (tokens.length > 3) tokens(3).toFloat else 0.0001f
      new Test(image1, image2, similarity)
    }

    xlsxPrinter.print(tests)
  }

}