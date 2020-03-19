package ru.avem.kvm120.view

import javafx.geometry.Pos
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Label
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kvm120.communication.CommunicationModel
import ru.avem.kvm120.database.entities.Protocol
import ru.avem.kvm120.database.entities.ProtocolsTable
import ru.avem.kvm120.utils.Singleton
import tornadofx.*

class GraphHistoryWindow : View("История графика") {
    var lineChart: LineChart<Number, Number> by singleAssign()
    var series = XYChart.Series<Number, Number>()

    override fun onDock() {
        lineChart.title = Singleton.currentProtocol.toString()
    }

    override val root = anchorpane {
        prefWidth = 1200.0
        prefHeight = 720.0

        vbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }
            alignmentProperty().set(Pos.CENTER)
            lineChart = linechart("", NumberAxis(), NumberAxis()) {
                xAxis.label = "мс"
                var value = Singleton.currentProtocol.values
                value = value.removeSuffix("]")
                value = value.removePrefix("[")
                value = value.replace(", ", " ")
                value = value.replace(",", ".")
                var valueString = ""
                var realTime = 0.0
                for (i in value.indices) {
                    if (value[i] != ' ') {
                        valueString += value[i]
                    }
                    if (value[i] == ' ') {
                        val valueDouble = valueString.toDouble()
                        series.data.add(XYChart.Data<Number, Number>(realTime, valueDouble))
                        realTime += 100.0
                        valueString = ""
                    }
                }
                prefHeight = 600.0
                data.add(series)
                animated = false
                createSymbols = false
            }
        }
    }.addClass(Styles.blueTheme, Styles.extraHard)
}