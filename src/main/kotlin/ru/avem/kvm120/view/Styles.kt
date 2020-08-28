package ru.avem.kvm120.view

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val lineChart by cssclass()
        val blueTheme by cssclass()
        val customfont by cssclass()
        val medium by cssclass()
        val hard by cssclass()
        val extraHard by cssclass()
        val megaHard by cssclass()
        val bigger by cssclass()
        val stopStart by cssclass()
        val anchorPaneBorders by cssclass()
        val anchorPaneStatusColor by cssclass()
        val roundButton by cssclass()
        val powerButtons by cssclass()
        val kVArPowerButtons by cssclass()
        val tableRowCell by cssclass()
        val vboxTextArea by cssclass()
    }

    init {

        blueTheme {
            baseColor = c("#115a98")
        }

        customfont {
//            font = if (Toolkit.getDefaultToolkit().screenSize.height == 1080) {
//                loadFont("/font/GolosText.ttf", 80.0)!!
//            } else {
                loadFont("/font/GolosText.ttf", 40.0)!!
//            }
//            textFill = c("#ff0f0f")
        }

        medium {
            fontSize = 18.px
            fontWeight = FontWeight.BOLD
        }

        hard {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        extraHard {
            fontSize = 24.px
            fontWeight = FontWeight.EXTRA_BOLD
        }

        megaHard {
            fontSize = 28.px
            fontWeight = FontWeight.EXTRA_BOLD
        }

        bigger {
            fontSize = 60.px
            fontWeight = FontWeight.EXTRA_BOLD
        }

        stopStart {
            fontSize = 60.px
            fontWeight = FontWeight.EXTRA_BOLD
        }

        powerButtons {
            fontSize = 18.px
            baseColor = c("#2178CC")
            prefWidth = 50.px
        }

        kVArPowerButtons {
            fontSize = 18.px
            baseColor = c("#60C3CC")
            prefWidth = 50.px
        }

        anchorPaneBorders {
            borderColor += CssBox(
                top = c("grey"),
                bottom = c("grey"),
                left = c("grey"),
                right = c("grey")
            )
        }

        anchorPaneStatusColor {
            backgroundColor += c("#B4AEBF")
        }

        roundButton {
            backgroundRadius += CssBox(
                top = 30.px,
                bottom = 30.px,
                left = 30.px,
                right = 30.px
            )
        }

        tableColumn {
            alignment = Pos.CENTER
            fontWeight = FontWeight.EXTRA_BOLD
            fontSize = 22.px
        }

        tableRowCell {
            cellSize = 50.px
        }

        checkBox {
            selected {
                mark {
                    backgroundColor += c("black")
                }
            }
        }

        vboxTextArea {
            backgroundColor += c("#6696bd")
        }

        lineChart {
            chartSeriesLine {
                backgroundColor += c("red")
                stroke = c("red")
            }
            chartLineSymbol {
                backgroundColor += c("red")
            }
        }
    }
}
