package print

import java.io.{IOException, PrintWriter}

import select.Test

/**
  * Prints a list of test in the HTML format and saves the created
  * file to disk.
  */
class HTMLPrinter extends Printer {

  override def RESULT_PATH = "./gen/tests.html"

  def print(tests: List[Test]): Unit = {
    val writer = getPrintWriter()

    writer.println("<!DOCTYPE html>")
    writer.println("<html>")
    writer.println("<head>")
    writer.println("  <title>ROC Tests</title>")
    writer.println("  <meta charset=UTF-8>")
    writer.println("</head>")
    printCSS(writer)
    writer.println("<body>")
    printStats(writer, tests)
    printTests(writer, tests)
    writer.println("</body>")
    writer.println("</html>")
    writer.close()
  }

  private def printCSS(writer: PrintWriter) = {
    writer.println("<style>")
    writer.println("  * { font: 17px 'Consolas', 'Open Sans', arial; }")
    writer.println("  table { border-collapse: collapse; margin: 10px; }")
    writer.println("  th { font-weight: bold; background: #cccccc; }")
    writer.println("  th, td { border-top: 1px solid #7a7a7a; padding: 8px; }")
    writer.println("</style>")
  }

  private def printStats(writer: PrintWriter, tests: List[Test]) = {
    val stats = new Stats(tests)

    writer.println("  <table>")
    writer.println("    <tbody>")
    writer.println("      <tr>")
    writer.println("        <th>Total</th>")
    writer.println("        <th>Same</th>")
    writer.println("        <th>Different</td>")
    writer.println("      </tr>")
    writer.println("      <tr>")
    writer.println("        <td>" + stats.total + "</td>")
    writer.println("        <td>" + stats.same + "</td>")
    writer.println("        <td>" + stats.diff + "</td>")
    writer.println("      </tr>")
    writer.println("    </tbody>")
    writer.println("  </table>")
  }

  private def printTests(writer: PrintWriter, tests: List[Test]) = {
    writer.println("  <table>")
    writer.println("    <thead>")
    writer.println("      <tr>")
    writer.println("        <th>No.</th>")
    writer.println("        <th>Image 1</th>")
    writer.println("        <th>Image 2</th>")
    writer.println("        <th>Same Class</th>")
    writer.println("      </tr>")
    writer.println("    </thead>")
    writer.println("    <tbody>")
    tests.zipWithIndex.foreach { case (test, i) =>
      printTest(writer, test, i)
    }
    writer.println("    </tbody>")
    writer.println("  </table>")
  }

  private def printTest(writer: PrintWriter, test: Test, i: Int) = {
    writer.println("      <tr>")
    writer.println("        <td>" + (i + 1) + "</td>")
    writer.println("        <td>" + getExtendedName(test.image1) + "</td>")
    writer.println("        <td>" + getExtendedName(test.image2) + "</td>")
    writer.println("        <td>" + (if (test.isSameClass) "+" else "-") + "</td>")
    writer.println("      </tr>")
  }

}
