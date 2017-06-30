package writer

/**
  * Generates an Excel file with a sheet for every file named "test"<k>".txt" where
  * <k> is an integer. However, the excel template currently only supports a <k> of
  * [5, 20, 50, 90, 200, 500, 1000] in the visualisation.
  *
  * sourcePath: The directory where to test files are located.
  * targetPath: The directory where to write the excel file to.
  */
object Main {

  def main(args: Array[String]): Unit = {
    val sourcePath = args(0)
    val targetPath = args(1)
    val writer = new ExcelWriter
    val data = Reader.read(sourcePath)

    data.keys.foreach(k => writer.write(k, data(k)))
    writer.save(targetPath)
  }

}
