package print

import java.io.{File, IOException, PrintWriter}

import select.Test

/**
  * Prints a List of Tests as a JSON file and saves it to disk.
  * All tests are contained in the array "tests".
  */
class JSONPrinter extends Printer {

  override def RESULT_PATH: String = "./gen/tests.json"

  override def print(tests: List[Test]): Unit = {
    val writer = getPrintWriter()

    writer.println("{")
    writer.println("  \"tests\": [")

    tests.zipWithIndex.foreach { case (test, i) =>
      printLine(writer, test)
      if (i != tests.length - 1) writer.println(",")
      else writer.println("")
    }

    writer.println("  ]")
    writer.println("}")
    writer.close()

  }

  private def printLine(writer: PrintWriter, test: Test) = {
    writer.println("    {")
    writer.println("      \"image1\": \"" + escape(test.image1) + "\",")
    writer.println("      \"image2\": \"" + escape(test.image2) + "\",")
    writer.println("      \"same_class\": " + test.isSameClass + "")
    writer.print("    }")
  }

  private def escape(file: File): String = {
    file.getAbsolutePath.replaceAllLiterally("\\", "/")
  }
}
