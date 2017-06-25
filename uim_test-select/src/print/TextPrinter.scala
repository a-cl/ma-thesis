package print

import java.io.{IOException, PrintWriter}

import select.Test

/**
  * Prints a List of Tests to the Scala console.
  */
class TextPrinter extends Printer {

  override def print(kind: String, tests: List[Test]) = {
    try {
      val writer = new PrintWriter("tests_" + kind + ".html", "UTF-8")
      val stats = new Stats(tests)

      writer.print("Total " + stats.total + " ")
      writer.print("Same " + stats.same + " ")
      writer.println("Diff " + stats.diff + " ")

      tests.foreach { test =>
        val name1 = getExtendedName(test.image1)
        val name2 = getExtendedName(test.image2)
        val same = if (test.isSimilar()) "+" else "-"

        writer.println(name1 + " " + name2 + " " + same)
      }

      writer.close()
    } catch {
      case e: IOException => println("Error writing Text results:", e)
    }
  }

}
