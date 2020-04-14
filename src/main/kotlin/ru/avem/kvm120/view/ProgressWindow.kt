package ru.avem.kvm120.view

import tornadofx.View
import tornadofx.addClass
import tornadofx.anchorpane
import tornadofx.progressindicator


class ProgressWindow : View("Состояние защит") {
    override val root = anchorpane {
        progressindicator {
        }
    }.addClass(Styles.blueTheme)
}