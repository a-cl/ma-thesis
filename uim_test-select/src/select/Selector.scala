package select

import java.io.File
import scala.collection.mutable.ListBuffer

/**
  * A Selector selects random image pairs from the directories
  * contained in path and returns them as a List of Tests.
  *
  * @param strategy The strategy to use for the image selection.
  */
class Selector (strategy: SelectionStrategy) {

  /**
    * Reads all image files directly contained in the directories
    * of paths. Shuffles the images and so creates random Tests by
    * combining every image exclusively with another image.
    *
    * @param paths The directories with the images to use.
    * @return List[Test]
    */
  def selectTests (paths: List[String]): List[Test] = {
    val allSets = paths.map(path => new File(path))
    val invalid = allSets.filter(file => !file.isDirectory)

    if (invalid.nonEmpty) {
      throw new Exception("Invalid file(s): " + invalid.mkString(", "))
    }

    val images = allSets.foldLeft(ListBuffer[File]()) { (memo, set) =>
      memo.++=(set.listFiles.filter(isImage))
    }

    strategy.select(images.toList)
  }

  /**
    * Returns true if file is of type png, jpg or gif, otherwise false.
    *
    * @param file The file to check.
    * @return Boolean
    */
  private def isImage (file: File): Boolean = {
    def isPNG (f: File): Boolean = f.getName.endsWith(".png")
    def isJPG (f: File): Boolean = f.getName.endsWith(".jpg")
    def isGIF (f: File): Boolean = f.getName.endsWith(".gif")
    file.isFile && (isPNG(file) || isGIF(file) || isJPG(file))
  }

}
