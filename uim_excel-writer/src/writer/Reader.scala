package writer

import java.io.File

import scala.collection.mutable
import scala.io.Source
import scala.util.matching.Regex

object Reader {

  type DataSheet = mutable.HashMap[Int, List[Test]]

  val numberRegex = "[0-9]".r

  val fileRegex = new Regex("test*")


  def read(path: String): DataSheet = {
    val file = new File(path)
    val map = new DataSheet

    readDir(file).foreach { set =>
      if (fileRegex.findFirstIn(set.getName).isDefined) {
        val tests = Source.fromFile(set).getLines().toList.map { line =>
          val tokens = line.split(" ")
          val image1 = new File(tokens(0))
          val image2 = new File(tokens(1))
          val same = if (tokens(2) == "+") true else false
          val error = tokens(3).toFloat
          new Test(image1, image2, same, error)
        }.sortWith { (test1, test2) =>
          test1.error < test2.error
        }

        val unique = tests.filter { res =>
          var doublette = false

          tests.foreach { current =>
            if (res.sameImages(current)) doublette = true
          }
          doublette
        }

        map.put(getK(set), unique)

      }
    }
    map
  }

  def readDir (path: File): List[File] = path.listFiles().filter { file =>
    file.isFile && file.getName.endsWith(".txt")
  }.toList

  def getK (file: File): Int = {
    Integer.parseInt(numberRegex.findAllIn(file.getName).mkString)
  }

}
