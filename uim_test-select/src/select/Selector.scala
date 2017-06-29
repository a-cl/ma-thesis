package select

import java.io.File

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * A Test is a Product of two image files.
  *
  * @param image1 the first image file.
  * @param image2 the second image file.
  */
class Test(val image1: File, val image2: File, var similarity: Float = 0.0f) {

  /**
    * Returns true if and only if the parent of image1 is the same
    * as the parent of image2. Two images are assumed to be in the
    * same class, if there are in the same folder.
    *
    * @return Boolean
    */
  def isSameClass: Boolean = {
    image1.getParentFile.equals(image2.getParentFile)
  }

}

/**
  * A Selector selects random image pairs from the directories
  * contained in path and returns them as a List of Tests.
  *
  * @param strategy The strategy to use for the image selection.
  */
class Selector(strategy: SelectionStrategy) {

  /**
    * Reads all image files directly contained in the directories
    * of paths. Shuffles the images and so creates random Tests by
    * combining every image exclusively with another image.
    *
    * @param paths The directories with the images to use.
    * @return List[Test]
    */
  def selectTests(paths: List[String]): List[Test] = {
    val allSets = paths.map(path => new File(path))
    val invalid = allSets.filter(file => !file.isDirectory)

    if (invalid.nonEmpty) {
      throw new Exception("Invalid file(s): " + invalid.mkString(", "))
    }

    val images = allSets.foldLeft(ListBuffer[File]()) { (memo, set) =>
      memo.++=(set.listFiles.filter(isImage))
    }

    Random.shuffle(strategy.select(images.toList))
  }

  /**
    * Returns true if file is of type png, jpg or gif, otherwise false.
    *
    * @param file The file to check.
    * @return Boolean
    */
  private def isImage(file: File): Boolean = {
    def isPNG(f: File): Boolean = f.getName.endsWith(".png")
    def isJPG(f: File): Boolean = f.getName.endsWith(".jpg")
    def isGIF(f: File): Boolean = f.getName.endsWith(".gif")

    file.isFile && (isPNG(file) || isGIF(file) || isJPG(file))
  }

}
