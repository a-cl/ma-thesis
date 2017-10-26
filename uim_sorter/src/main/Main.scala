package main

import java.io.{File, PrintWriter}

import scala.io.Source

class Result (val error: Float, val same: String, val image1: File, val image2: File) {

  def getShortPath (image: File): String = {
    image.getParentFile.getName + '/' + image.getName.replace(".jpg", "")
  }

  override def toString: String = {
    error + "\t(" + same + ")\t" + getShortPath(image1) + "\t" + getShortPath(image2)
  }

  def sameImages: Boolean = image1.equals(image2)

}

class Writer (target: String) {

  val pw = new PrintWriter(target)

  def write (results: List[Result]): Unit = {
    var same = 0.0
    var diff = 0.0
    var total = 0.0

    results.foreach { res =>
      total += 1
      if (res.same == "+") same += 1 else diff +=1
      val tp = same / total
      val fp = diff / total

      pw.write(res.toString + "\t(" + tp + ", " + fp + ")\n")
    }
    pw.close
  }

}

object Main extends App {

  override def main(args: Array[String]): Unit = {
    val source = new File("../data/test/2cats/tests200.txt")
    val writer = new Writer("../data/test/2cats/tests200.txt")
    val results = Source.fromFile(source).getLines().toList.map { line =>
      val tokens = line.split(" ")
      val image1 = new File(tokens(0))
      val image2 = new File(tokens(1))
      val same = tokens(2)
      val error = tokens(3).toFloat
      new Result(error, same, image1, image2)
    }.filter { res =>
      !res.sameImages
    }.sortWith { (res1, res2) =>
      res1.error < res2.error
    }

    val unique = results.filter { res =>
      var doublette = false

      results.foreach { current =>
        if (current.image1.equals(res.image1) && current.image2.equals(res.image2) ||
        current.image1.equals(res.image2) && current.image2.equals(res.image1)) {
          doublette = true
        }
      }
      doublette
    }

    writer.write(unique)
  }

}
