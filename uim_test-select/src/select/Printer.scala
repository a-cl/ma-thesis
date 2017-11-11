package select

import java.io.{File, PrintWriter}

import scala.collection.mutable.ListBuffer

object Printer {

  def print(path: String, dataSet: DataSet): Unit = {
    writeTestsBoVW(path + "/test_bovw.txt", dataSet.test)
    writeTestsAE(path + "/test_ae.txt", dataSet.test)
    writeTrain(path + "/train.txt", dataSet.train)
  }

  private def writeTestsBoVW (path: String, tests: List[Test]) = {
    val writer = getWriter(path)

    tests.foreach { test =>
      val name1 = test.image1.getAbsolutePath
      val name2 = test.image2.getAbsolutePath
      val same = if (test.isSameClass) "+" else "-"

      writer.println(name1 + " " + name2 + " " + same)
    }
    writer.close()
  }

  private def writeTestsAE (path: String, tests: List[Test]) = {
    val writer = getWriter(path)
    val unique = ListBuffer[String]()

    tests.foreach { test =>
      val path1 = test.image1.getAbsolutePath
      val path2 = test.image2.getAbsolutePath
      if (!unique.contains(path1)) unique.+=(path1)
      if (!unique.contains(path2)) unique.+=(path2)
    }

    unique.foreach { path =>
      writer.println(path)
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