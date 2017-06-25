package print

import java.io.{FileInputStream, FileOutputStream}

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFRow, XSSFSheet, XSSFWorkbook}
import select.Test

/**
  * Reads in the template file for a ROC curve and copies the test data
  * to the worksheets. The original file will not be changed, a copy with
  * the data is created and saved to disk.
  */
class XLSXPrinter extends Printer {

  val TEMPLATE_PATH = "./roc_template.xlsx"
  val RESULT_PATH = "./roc_tests.xlsx"

  override def print(kind: String, tests: List[Test]): Unit = {
    val streamIn = new FileInputStream(TEMPLATE_PATH)
    val streamOut = new FileOutputStream(RESULT_PATH)
    val workbook = new XSSFWorkbook(streamIn)

    printSheet(tests, workbook, 0)
    //printSheet(tests, workbook, 1)
    //printSheet(tests, workbook, 2)
    streamIn.close()

    workbook.write(streamOut)
    streamOut.close()
  }

  /**
    * Copies the test data of tests to the XSSFWorksheet of the workbook at
    * the supplied index.
    *
    * @param tests The test data
    * @param workbook The workbook to write to
    * @param index The index of the sheet to use
    */
  private def printSheet (tests: List[Test], workbook: XSSFWorkbook, index: Int) = {
    val sheet = workbook.getSheetAt(index)

    tests.zipWithIndex.foreach { case (test, rowIndex) =>
      val row = getOrCreateRow(sheet, rowIndex + 1)

      getOrCreateCell(row, 0, CellType.STRING).setCellValue(test.image1.getAbsolutePath)
      getOrCreateCell(row, 1, CellType.STRING).setCellValue(test.image2.getAbsolutePath)
      getOrCreateCell(row, 3, CellType.STRING).setCellValue(if (test.isSimilar()) "+" else "-")
    }
  }

  /**
    * Returns the XSSFRow of sheet at the supplied index. If no row is present
    * at the index, the row will be created and returned afterwards.
    *
    * @param sheet The worksheet containing the rows
    * @param index The index of the desired row
    * @return XSSFRow
    */
  private def getOrCreateRow (sheet: XSSFSheet, index: Int): XSSFRow = {
    if (sheet.getRow(index) == null) sheet.createRow(index)
    sheet.getRow(index)
  }

  /**
    * Returns the XSSFCell of row at the supplied index. If no cell is present
    * at the index, the cell will be created with the CellType kind and returned
    * afterwards.
    *
    * @param row The row containing the cells.
    * @param index The index of the desired row.
    * @param kind The type of the cell, if it needs to be created.
    * @return XSSFCell
    */
  private def getOrCreateCell (row: XSSFRow, index: Int, kind: CellType): XSSFCell = {
    if (row.getCell(index) == null) row.createCell(index, kind)
    row.getCell(index)
  }
}
