package print
import java.io.{File, IOException, PrintWriter}

import select.Test

/**
  * Prints a List of Tests as a JSON file and saves it to disk.
  * All tests are contained in the array "tests".
  */
class JSONPrinter extends Printer {

  override def print (kind: String, tests: List[Test]) = {
    try {
      val writer = new PrintWriter("tests_" + kind + ".json", "UTF-8")

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
    } catch {
      case e: IOException => println("Error writing results:", e)
    }
  }

  private def printLine (writer: PrintWriter, test: Test) = {
    writer.println("    {")
    writer.println("      \"image1\": \"" + escape(test.image1) + "\",")
    writer.println("      \"image2\": \"" + escape(test.image2) + "\",")
    writer.println("      \"same_class\": " + test.isSimilar + "")
    writer.print("    }")
  }

  private def escape (file: File): String = {
    file.getAbsolutePath.replaceAllLiterally("\\", "/")
  }
}
