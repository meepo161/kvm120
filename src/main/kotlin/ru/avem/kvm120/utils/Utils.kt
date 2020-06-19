package ru.avem.kvm120.utils

import javafx.event.EventHandler
import javafx.scene.control.TextField
import java.awt.Desktop
import java.io.*
import java.nio.file.Paths

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

fun TextField.callKeyBoard() {
    onTouchPressed = EventHandler {
        Desktop.getDesktop()
            .open(Paths.get("C:/Program Files/Common Files/Microsoft Shared/ink/TabTip.exe").toFile())
        requestFocus()
    }
}

fun openFile(file: File) {
    try {
        Desktop.getDesktop().open(file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}