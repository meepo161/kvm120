package ru.avem.kvm120.view

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.TextField
import ru.avem.kvm120.utils.Singleton
import ru.avem.kvm120.utils.callKeyBoard
import tornadofx.*
import tornadofx.controlsfx.errorNotification

class GraphHistoryWindow : View("История графика") {
    private var form = "Форма"
    private var freq = "Частота"
    private var lineChart: LineChart<Number, Number> by singleAssign()
    private var series = XYChart.Series<Number, Number>()
    private var tfOt: TextField by singleAssign()
    private var tfDo: TextField by singleAssign()
    private var values = Singleton.currentProtocol.values.removePrefix("[").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)

    override fun onDock() {
        var series = XYChart.Series<Number, Number>()
        series.data.clear()
        lineChart.data.clear()
        lineChart.title = Singleton.currentProtocol.toString()

        values = Singleton.currentProtocol.values.removePrefix("[").removeSuffix("]")
            .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
        var realTime = 0.0

        var needAddSeriesToChart = true
        for (i in values.indices) {
            if (values[i].isNaN()) {
                series = XYChart.Series<Number, Number>()
                needAddSeriesToChart = true
                realTime += 0.1
            } else {
                series.data.add(XYChart.Data(realTime, values[i]))
                realTime += 0.1

                if (needAddSeriesToChart) {
                    lineChart.data.add(series)
                    needAddSeriesToChart = false
                }
            }
        }

        tfOt.text = ""
        tfDo.text = ""

        lineChart.xAxis.label = "Время, с"
        when {
            Singleton.currentProtocol.typeOfValue == form -> {
                lineChart.yAxis.label = "Напряжение, кВ"
                lineChart.xAxis.label = "Время"
            }
            Singleton.currentProtocol.typeOfValue != freq -> {
                lineChart.yAxis.label = "Напряжение, кВ"
            }
            Singleton.currentProtocol.typeOfValue == freq -> {
                lineChart.yAxis.label = "Частота, Гц"
            }
        }
    }

    override val root = anchorpane {
        prefWidth = 900.0
        prefHeight = 500.0
        hbox(spacing = 16.0) {
            alignmentProperty().set(Pos.CENTER)
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }
            vbox(spacing = 16.0) {
                alignmentProperty().set(Pos.CENTER)
                hbox(spacing = 16.0) {
                    alignmentProperty().set(Pos.CENTER)
                    val numberAxis = NumberAxis()
                    numberAxis.isForceZeroInRange = false
                    lineChart = linechart("", numberAxis, NumberAxis()) {
                        prefWidth = 1200.0
                        animated = false
                        createSymbols = false
                        isLegendVisible = false
                    }
                }
                hbox(spacing = 16.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("Отобразить график в диапазоне от")
                    tfOt = textfield {
                        callKeyBoard()
                        prefWidth = 100.0
                    }
                    label("до")
                    tfDo = textfield {
                        callKeyBoard()
                        prefWidth = 100.0
                    }
                    button("Ок") {
                        isDefaultButton = true
                        action {
                            if (!tfOt.text.isNullOrEmpty() || !tfDo.text.isNullOrEmpty()) {
                                var series = XYChart.Series<Number, Number>()
                                var ot1 = 0.0
                                var do1 = 0.0
                                try {
                                    ot1 = if (tfOt.text.replace(',', '.').toDouble() * 10 < 0) {
                                        0.0
                                    } else {
                                        tfOt.text.replace(',', '.').toDouble() * 10
                                    }
                                    do1 = tfDo.text.replace(',', '.').toDouble() * 10
                                } catch (e: Exception) {
                                    Platform.runLater {
                                        errorNotification(
                                            "Ошибка",
                                            "Проверьте правильность введенных данных",
                                            Pos.BOTTOM_CENTER,
                                            owner = this@GraphHistoryWindow.currentWindow
                                        )
                                    }
                                }
                                series.data.clear()
                                lineChart.data.clear()
                                var realTime = ot1 / 10
                                if (do1 > values.size) {
                                    do1 = values.size.toDouble()
                                    tfDo.text = (values.size / 10).toString()
                                }
                                var needAddSeriesToChart = true
                                for (i in ot1.toInt() until do1.toInt()) {
                                    if (values[i].isNaN()) {
                                        series = XYChart.Series<Number, Number>()
                                        needAddSeriesToChart = true
                                        realTime += 0.1
                                    } else {
                                        series.data.add(XYChart.Data(realTime, values[i]))
                                        realTime += 0.1

                                        if (needAddSeriesToChart) {
                                            lineChart.data.add(series)
                                            needAddSeriesToChart = false
                                        }
                                    }
                                }
                                lineChart.xAxis.isAutoRanging = true
                            } else {
                                Platform.runLater {
                                    errorNotification(
                                        "Ошибка",
                                        "Пустые поля. Заполните данные",
                                        Pos.BOTTOM_CENTER,
                                        owner = this@GraphHistoryWindow.currentWindow
                                    )
                                }
                            }
                        }
                    }
                    button("Сброс") {
                        action {
                            var series = XYChart.Series<Number, Number>()
                            series.data.clear()
                            lineChart.data.clear()
                            lineChart.title = Singleton.currentProtocol.toString()

                            values = Singleton.currentProtocol.values.removePrefix("[").removeSuffix("]")
                                .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
                            var realTime = 0.0

                            var needAddSeriesToChart = true
                            for (i in values.indices) {
                                if (values[i].isNaN()) {
                                    series = XYChart.Series<Number, Number>()
                                    needAddSeriesToChart = true
                                    realTime += 0.1
                                } else {
                                    series.data.add(XYChart.Data(realTime, values[i]))
                                    realTime += 0.1

                                    if (needAddSeriesToChart) {
                                        lineChart.data.add(series)
                                        needAddSeriesToChart = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }.addClass(Styles.blueTheme, Styles.raspberryStyle)
}