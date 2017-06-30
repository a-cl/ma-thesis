package writer

import java.io.File

import scala.collection.mutable
import scala.io.Source

object Reader {

  type DataSheet = mutable.HashMap[Int, List[Test]]

  def read(path: String): DataSheet = {
    val file = new File(path)
    val map = new DataSheet

    readDir(file).foreach { set =>
      println("Reading" + set.getName)

      val tests = Source.fromFile(set).getLines().toList.map { line =>
        val tokens = line.split(" ")
        val image1 = new File(tokens(0))
        val image2 = new File(tokens(1))
        val similarity = if (tokens.length > 3) tokens(3).toFloat else 0.0001f
        new Test(image1, image2, similarity)
      }.sortWith { (test1, test2) =>
        test1.similarity < test2.similarity
      }

      map.put(getK(set), tests)
    }
    map
  }

  def readDir (path: File): List[File] = path.listFiles().filter { file =>
    file.isFile && hasK(file) && file.getName.endsWith(".txt")
  }.toList

  def hasK (file: File): Boolean = try {
    Integer.parseInt(file.getName.replaceAll("[\\D]", ""))
    true
  } catch {
    case _: Throwable => false
  }

  def getK (file: File): Int = {
    if (hasK(file)) {
      Integer.parseInt(file.getName.replaceAll("[\\D]", ""))
    } else {
      throw new Exception("Invalid name (no k specified):" + file.getName)
    }
  }

}
