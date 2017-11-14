package select

import java.io.{File, PrintWriter}

import scala.collection.mutable.ListBuffer

object Printer {

  def print(path: String, dataSet: DataSet): Unit = {
    writeTests(path + "/test.txt", dataSet.test)
    writeTrain(path + "/train.txt", dataSet.train)
  }

  private def writeTests (path: String, tests: List[Test]) = {
    val writer = getWriter(path)

    tests.foreach { test =>
      val name1 = test.image1.getAbsolutePath
      val name2 = test.image2.getAbsolutePath
      val same = if (test.isSameClass) "+" else "-"

      writer.println(name1 + " " + name2 + " " + same)
    }
    writer.close()
  }

  private def writeTrain (path: String, train: List[File]) = {
    val writer = getWriter(path)

    train.foreach(file => writer.println(file.getAbsoluteFile))
    writer.close()
  }

  private def getWriter (path: String): PrintWriter = {
    val file = new File(path)
    file.getParentFile.mkdirs()
    new PrintWriter(file)
  }

}