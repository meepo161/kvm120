package ru.avem.kvm120.protocol

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.kvm120.app.Kvm120
import ru.avem.kvm120.utils.SHEET_PASSWORD
import ru.avem.kvm120.utils.copyFileFromStream
import ru.avem.kvm120.view.MainView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

fun saveProtocolAsWorkbook(path: String = "protocol.xlsx") {
    val template = File(path)
    copyFileFromStream(Kvm120::class.java.getResource("protocol.xlsx").openStream(), template)
    val mainView = MainView()

    try {
        XSSFWorkbook(template).use {
            val sheet = it.getSheet("Sheet1")
            val row = sheet.createRow(sheet.lastRowNum + 1)
            row.heightInPoints = 16.0f

            for (i in 0 until mainView.listOfValues.size) {
                val cell = row.createCell(i)
                cell.setCellValue(mainView.listOfValues[i])
            }
            sheet.protectSheet(SHEET_PASSWORD)
            val outStream = ByteArrayOutputStream()
            it.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {

    }
}
