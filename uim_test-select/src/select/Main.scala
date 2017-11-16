package select

/**
  * Runs the test selector. How to:
  *
  * path:         Path to the image folders and the resulting files
  * setCount:     Number of categories to use
  * trainPerSet:  Number of training samples per set
  * testPerSet:   Number of test samples per set
  * testCount:    Number of tests to create
  *
  * 1# test1 test1 2 15 35 2000
  * 2# test2 test2 2 30 70 2000
  * 3# test3 test3 3 20 60 2000
  */
object Main {

  def main(args: Array[String]): Unit = {
    val path = args(0)
    val setCount = args(1).toInt
    val trainPerSet = args(2).toInt
    val testPerSet = args(3).toInt
    val testCount = args(4).toInt

    val db = new Database(path)
    val data = db.createDataSet(setCount, trainPerSet, testPerSet, testCount)

    Printer.print(path, data)
  }

}