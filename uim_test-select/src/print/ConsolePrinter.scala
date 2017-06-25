package print
import select.Test

/**
  * Prints a List of Tests to the Scala console.
  */
class ConsolePrinter extends Printer {

  override def print(kind: String, tests: List[Test]) = {
    val stats = new Stats(tests)

    println("Generated test candidates (" + kind + ").")
    println("Total: " + stats.total)
    println("Same:  " + stats.same)
    println("Diff:  " + stats.diff)

    tests.foreach { test =>
      val name1 = getExtendedName(test.image1)
      val name2 = getExtendedName(test.image2)
      val same = if (test.isSimilar) "+" else "-"

      println(name1 + " | " + name2 + " | " + same)
    }

  }

}
