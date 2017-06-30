package select

import java.io.File

import scala.collection.mutable.{HashMap, ListBuffer}

class DataSet (val train: List[File], val test: List[Test])

class Database (path: String) {

  type Sets = HashMap[File, List[File]]

  val root = new File(path)

  if (!root.isDirectory) {
    throw new Exception("Invalid image root: " + root.getAbsoluteFile)
  }

  def createDataSet (setCount: Int, trainPerSet: Int, testPerSet: Int, testCount: Int): DataSet = {
    val selection = new PickEvenSelection(testCount)
    val sets = getSets(setCount)
    val (train, rest) = getSamples(sets, trainPerSet)
    val (tests, _) = getSamples(rest, testPerSet)

    new DataSet(train, selection.select(tests))
  }

  private def getSets (count: Int): Sets = {
    val files = root.listFiles.filter(f => f.isDirectory).to[ListBuffer]
    val sets = getRandomFiles(files, count)
    val map = new Sets()

    sets.foreach { set =>
      val images = set.listFiles.filter(isImage).toList
      map.put(set, images)
    }
    map
  }

  private def getSamples (sets: Sets, count: Int): (List[File], Sets) = {
    val rest = new Sets()
    val files = sets.keys.flatMap { key =>
      getRandomFiles(sets(key).to[ListBuffer], count)
    }.toList

    sets.keys.foreach { key =>
      rest.put(key, sets(key).filter(f => !files.contains(f)))
    }

    (files, rest)
  }

  private def getRandomFiles (files: ListBuffer[File], count: Int): List[File] = {
    val max = if (count > files.length) files.length else count

    Range(0, max).map { _ =>
      val index = (Math.random() * (files.length - 1)).toInt
      val file = files(index)
      files.-=(file)
      file
    }.toList
  }

  private def isImage(file: File): Boolean = {
    def isPNG(f: File): Boolean = f.getName.endsWith(".png")
    def isJPG(f: File): Boolean = f.getName.endsWith(".jpg")
    def isGIF(f: File): Boolean = f.getName.endsWith(".gif")

    file.isFile && (isPNG(file) || isGIF(file) || isJPG(file))
  }

}
