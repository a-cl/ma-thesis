package writer

import java.io.{File, FileFilter}

import scala.io.Source

object Main {

  val DEBUG = true

  def main(args: Array[String]): Unit = {
    val file = new File(args(0))

    if (!file.exists || !file.isDirectory) {
      throw new Exception("Invalid source for image sets: " + file.getAbsoluteFile)
    }

    val writer = new ExcelWriter
    val sets = readDir(file)

    sets.foreach { set =>
      val tests = Source.fromFile(set).getLines().toList.map { line =>
        val tokens = line.split(" ")
        val image1 = new File(tokens(0))
        val image2 = new File(tokens(1))
        val similarity = if (tokens.length > 3) tokens(3).toFloat else 0.0001f
        new Test(image1, image2, similarity)
      }.sortWith { (test1, test2) =>
        test1.similarity < test2.similarity
      }

      writer.write(getK(set), tests)
    }

    writer.save()
  }

  def readDir (path: File): List[File] = {
    path.listFiles(new FileFilter {
      override def accept(file: File): Boolean = {
        file.isFile && file.getName.endsWith(".txt")
      }
    }).toList
  }

  def getK (file: File): Int = {
    try {
      Integer.parseInt(file.getName.replaceAll("[\\D]", ""))
    } catch {
      case _: Throwable => {
        throw new Exception("Invalid name (no k specified):" + file.getName)
      }
    }
  }
}
