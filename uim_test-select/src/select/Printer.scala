package select

import java.io.{File, PrintWriter}

class Printer {

  def print(dataSet: DataSet): Unit = {
    writeTests(dataSet.test)
    writeTrain(dataSet.train)
  }

  private def writeTests (tests: List[Test]) = {
    val writer = new PrintWriter("gen/tests.txt")

    tests.foreach { test =>
      val name1 = test.image1.getAbsolutePath
      val name2 = test.image2.getAbsolutePath
      val same = if (test.isSameClass) "+" else "-"

      writer.println(name1 + " " + name2 + " " + same)
    }
    writer.close()
  }


  private def writeTrain (train: List[File]) = {
    val writer = new PrintWriter("gen/train.txt")

    train.foreach { file =>
      writer.println(file.getAbsoluteFile)
    }
    writer.close()
  }

}