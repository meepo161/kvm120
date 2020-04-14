package ru.avem.kvm120.view

import javafx.geometry.Pos
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.TextField
import ru.avem.kvm120.utils.Singleton
import ru.avem.kvm120.utils.Toast
import ru.avem.kvm120.utils.callKeyBoard
import tornadofx.*

class GraphHistoryWindow : View("История графика") {
    private var form = "Форма"
    private var freq = "Частота"
    private var lineChart: LineChart<Number, Number> by singleAssign()
    private var series = XYChart.Series<Number, Number>()
    private var tfOt: TextField by singleAssign()
    private var tfDo: TextField by singleAssign()
    private val values = Singleton.currentProtocol.values.removePrefix("[").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)

    override fun onDock() {
        lineChart.title = Singleton.currentProtocol.toString()
        series.data.clear()
        lineChart.data.clear()
        val values = Singleton.currentProtocol.values.removePrefix("[").removeSuffix("]")
            .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
        var realTime = 0.0
        for (i in values.indices) {
            series.data.add(XYChart.Data(realTime, values[i]))
            realTime += 0.1
        }
        lineChart.data.add(series)
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
        prefWidth = 1200.0
        prefHeight = 720.0
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
                            var ot1 = 0
                            var do1 = 0
                            try {
                                ot1 = tfOt.text.toInt() * 10
                                do1 = tfDo.text.toInt() * 10
                            } catch (e: Exception) {
                                Toast.makeText("Проверьте правильность введенных данных")
                                    .show(Toast.ToastType.ERROR)
                            }
                            if (tfOt.text.toInt() * 10 < 0) {
                                ot1 = 0
                            }
                            series.data.clear()
                            lineChart.data.clear()
                            var realTime = ot1.toDouble() / 10
                            if (do1 > values.size) {
                                do1 = values.size
                                tfDo.text = (values.size / 10).toString()
                            }
                            for (i in ot1 until do1) {
                                series.data.add(XYChart.Data(realTime, values[i]))
                                realTime += 0.1
                            }
                            lineChart.xAxis.isAutoRanging = true
                            lineChart.data.add(series)
                        }
                    }
                }
            }
        }
    }.addClass(Styles.blueTheme, Styles.extraHard)
}