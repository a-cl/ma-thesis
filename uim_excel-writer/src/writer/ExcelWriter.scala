package writer

import java.io.{File, FileInputStream, FileOutputStream}

import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

class Test (val image1: File, val image2: File, val similarity: Float) {
  def isSameClass: Boolean = image1.getParentFile.equals(image2.getParentFile)
}

class ExcelWriter {

  val RESULT_PATH = "./gen/roc_tests.xlsx"
  val TEMPLATE_PATH = "roc_template.xlsx"

  private val streamIn = new FileInputStream(TEMPLATE_PATH)
  private val workbook = new XSSFWorkbook(streamIn)

  streamIn.close()

  def write (k: Int, tests: List[Test]): Unit = {
    val count = tests.length + 1
    val sheet = workbook.getSheet("k = " + k)
    val first = sheet.createRow(0)

    first.createCell(0, 1).setCellValue("IMG1")
    first.createCell(1, 1).setCellValue("IMG2")
    first.createCell(2, 1).setCellValue("RAND")
    first.createCell(3, 1).setCellValue("SAME_CLASS")
    first.createCell(4, 1).setCellValue("SIM (k=" + k + ")")
    first.createCell(5, 1).setCellValue("PRE")
    first.createCell(6, 1).setCellValue("TPR")
    first.createCell(7, 1).setCellValue("FPR")
    first.createCell(8, 1).setCellValue("")
    first.createCell(9, 1).setCellValue("AUC")

    tests.zipWithIndex.foreach { case (test, index) =>
      val rowIndex = index + 1
      val row = sheet.createRow(rowIndex)

      row.createCell(0, 1).setCellValue(test.image1.getAbsolutePath)
      row.createCell(1, 1).setCellValue(test.image2.getAbsolutePath)
      row.createCell(2, 1).setCellFormula("RAND()")
      row.createCell(3, 1).setCellValue(if (test.isSameClass) "+" else "-")
      row.createCell(4, 1).setCellValue(test.similarity)
      row.createCell(5, 2).setCellFormula(makePrecisionFormula(rowIndex + 1))
      row.createCell(6, 2).setCellFormula(makeTruePositiveFormula(rowIndex + 1, count))
      row.createCell(7, 2).setCellFormula(makeFalsePositiveFormula(rowIndex + 1, count))
      row.createCell(8, 2).setCellFormula(makeComplexMagic(rowIndex + 1))

      if (rowIndex == 1) {
        row.createCell(9, 2).setCellFormula("SUM(I2:I" + count + ")")
      }
    }
  }

  def save (): Unit = {
    val streamOut = new FileOutputStream(RESULT_PATH)
    workbook.write(streamOut)
    streamOut.close()
  }

  private def makePrecisionFormula (rowIndex: Int): String = {
    "COUNTIF($D$2:D" + rowIndex + ",\"+\")/COUNTA($D$2:$D" + rowIndex + ")"
  }

  private def makeTruePositiveFormula (rowIndex: Int, count: Int): String = {
    "COUNTIF($D$2:D" + rowIndex + ",\"+\")/COUNTIF($D$2:$D$" + count + ",\"+\")"
  }

  private def makeFalsePositiveFormula (rowIndex: Int, count: Int): String = {
    "COUNTIF($D$2:D" + rowIndex + ",\"-\")/COUNTIF($D$2:$D$" + count + ",\"-\")"
  }

  private def makeComplexMagic (rowIndex: Int): String = {
    if (rowIndex == 2) "0" else "(H" + rowIndex + "-H" + (rowIndex - 1) + ")*G" + rowIndex
  }

}
