package select

import java.io.File

/**
  * Runs the test selector. How to:
  *
  * path:         Path to the image folders
  * setCount:     Number of categories to use
  * trainPerSet:  Number of training samples per set
  * testPerSet:   Number of test samples per set
  * testCount:    Number of tests to create
  */
object Main {

  def main(args: Array[String]): Unit = {
    val path = args(0)
    val setCount = args(1).toInt
    val trainPerSet = args(2).toInt
    val testPerSet = args(3).toInt
    val testCount = args(4).toInt

    val dataBase = new Database(path)
    val dataSet = dataBase.createDataSet(setCount, trainPerSet, testPerSet, testCount)
    val printer = new Printer

    printer.print(dataSet)
  }

}