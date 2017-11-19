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
    val sourcePath = "E:/thesis_res/1/processed/shared/128"
    val targetPath = "data/1/roc_shared_128"
    val excelWriter = new ExcelWriter("data/template/roc90")
    val data = Reader.read(sourcePath)

    data.keys.foreach(k => excelWriter.write(k, data(k)))
    excelWriter.save(targetPath)
  }

}
