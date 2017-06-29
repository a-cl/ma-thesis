package select

/**
  * Runs the test selector. How to:
  *
  * [testCount] [...sets]
  *
  * testCount: number of tests to create
  * sets:      paths of directories to use
  */
object Main {


  def main(args: Array[String]): Unit = {
    val testCount = Integer.parseInt(args(0))
    val sets = args.splitAt(1)._2.toList

    val textPrinter = new TextPrinter
    val strategy = new PickEvenStrategy(testCount)
    val tests = new Selector(strategy).selectTests(sets)

    textPrinter.print(tests)
  }

}