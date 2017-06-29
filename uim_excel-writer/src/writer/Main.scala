package writer

import java.io.File

import scala.io.Source

object Main {

  def main(args: Array[String]): Unit = {
    val path = args(0)
    val writer = new ExcelWriter

    val tests = Source.fromFile(path).getLines().toList.map { line =>
      val tokens = line.split(" ")
      val image1 = new File(tokens(0))
      val image2 = new File(tokens(1))
      val similarity = if (tokens.length > 3) tokens(3).toFloat else 0.0001f
      new Test(image1, image2, similarity)
    }

    writer.write(tests)
  }

}
