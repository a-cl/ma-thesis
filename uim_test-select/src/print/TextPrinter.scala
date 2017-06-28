package print

import java.io.{IOException, PrintWriter}

import select.Test

/**
  * Prints a List of Tests as a text file.
  */
class TextPrinter extends Printer {

  override def RESULT_PATH = "./gen/tests.txt"

  override def print(tests: List[Test]): Unit = {
    val writer = getPrintWriter()

    tests.foreach { test =>
      val name1 = test.image1.getAbsolutePath
      val name2 = test.image2.getAbsolutePath
      val same = if (test.isSameClass()) "+" else "-"

      writer.println(name1 + " " + name2 + " " + same)
    }

    writer.close()
  }

}
