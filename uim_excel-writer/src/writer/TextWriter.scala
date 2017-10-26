package writer

import java.io.PrintWriter

class TextWriter(target: String) {

  def write(k: Int, results: List[Test]): Unit = {
    val pw = new PrintWriter(target + "tests" + k + "_roc.txt")

    var same = 0.0
    var diff = 0.0
    var total = 0.0

    results.foreach { res =>
      total += 1
      if (res.same) same += 1 else diff += 1
      val tp = same / total
      val fp = diff / total

      pw.write(res.toString + "\t(" + tp + ", " + fp + ")\n")
    }
    pw.close()
  }

}

