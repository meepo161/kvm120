package ru.avem.kvm120.view

import javafx.geometry.Pos
import javafx.stage.StageStyle
import tornadofx.*


class ProgressWindow : View("Состояние защит") {
    override val root = anchorpane {
        progressindicator {
        }
    }.addClass(Styles.blueTheme)
}