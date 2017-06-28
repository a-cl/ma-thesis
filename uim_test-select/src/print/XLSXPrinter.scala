package print

import java.io.{FileInputStream, FileOutputStream}
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}
import select.Test

/**
  * Reads in the template file for a ROC curve and copies the test data
  * to the worksheets. The original file will not be changed, a copy with
  * the data is created and saved to disk.
  */
class XLSXPrinter extends Printer {

  override def RESULT_PATH = "./gen/roc_tests.xlsx"

  override def print(tests: List[Test]): Unit = {
    val streamIn = new FileInputStream("./roc_template.xlsx")
    val streamOut = new FileOutputStream(RESULT_PATH)
    val workbook = new XSSFWorkbook(streamIn)
    val kValues = List(5, 20, 50, 90, 200, 500, 1000)

    kValues.foreach { k =>
      printTestSheet(tests, workbook.getSheet("k = " + k), k)
    }

    workbook.write(streamOut)
    streamIn.close()
    streamOut.close()
  }

  /**
    * Copies the test data of tests to the XSSFWorksheet of the workbook at
    * the supplied index.
    *
    * @param tests The test data
    * @param sheet The sheet to write to
    */
  private def printTestSheet (tests: List[Test], sheet: XSSFSheet, k: Int) = {
    val count = tests.length + 1
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
      row.createCell(3, 1).setCellValue(if (test.isSameClass()) "+" else "-")
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

  /*
  private def printResultSheet(resultSheet: XSSFSheet, sheets: List[XSSFSheet], count: Int) = {
    val drawing = resultSheet.createDrawingPatriarch
    val anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15)
    val chart = drawing.createChart(anchor)
    val legend = chart.getOrCreateLegend
    val data = chart.getChartDataFactory.createScatterChartData

    val bottomAxis = chart.getChartAxisFactory.createCategoryAxis(AxisPosition.BOTTOM)
    val leftAxis = chart.getChartAxisFactory.createValueAxis(AxisPosition.LEFT)

    legend.setPosition(LegendPosition.TOP_RIGHT)
    leftAxis.setCrosses(AxisCrosses.AUTO_ZERO)

    sheets.foreach { dataSheet =>
      val xs = DataSources.fromNumericCellRange(dataSheet, new CellRangeAddress(2, count, 7, 7))
      val ys = DataSources.fromNumericCellRange(dataSheet, new CellRangeAddress(2, count, 6, 6))
      val series = data.addSerie(xs, ys)
      series.setTitle(dataSheet.getSheetName)
    }

    chart.plot(data, bottomAxis, leftAxis)
  }
  */

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
