package ru.avem.kvm120.utils

import java.awt.Desktop
import java.io.*
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.abs

fun formatRealNumber(num: Double): Double {
    val absNum = abs(num)

    var format = "%.0f"
    when {
        absNum == 0.0 -> format = "%.0f"
        absNum < 0.1f -> format = "%.5f"
        absNum < 1f -> format = "%.4f"
        absNum < 10f -> format = "%.3f"
        absNum < 100f -> format = "%.2f"
        absNum < 1000f -> format = "%.1f"
        absNum < 10000f -> format = "%.0f"
    }
    return String.format(Locale.US, format, num).toDouble()
}

fun openFile(file: File) {
    try {
        Desktop.getDesktop().open(file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun copyFileFromStream(_inputStream: InputStream, dest: File) {
    _inputStream.use { inputStream ->
        try {
            val fileOutputStream = FileOutputStream(dest)
            val buffer = ByteArray(1024)
            var length = inputStream.read(buffer)
            while (length > 0) {
                fileOutputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
        } catch (e: FileNotFoundException) {
        }
    }
}
