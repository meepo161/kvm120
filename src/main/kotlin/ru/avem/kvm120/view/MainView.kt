package ru.avem.kvm120.view

import com.ucicke.k2mod.modbus.util.ModbusUtil.sleep
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.stage.Modality
import javafx.stage.StageStyle
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kvm120.communication.CommunicationModel
import ru.avem.kvm120.communication.ModbusConnection
import ru.avem.kvm120.controllers.MainViewController
import ru.avem.kvm120.database.entities.Protocol
import ru.avem.kvm120.utils.Toast
import ru.avem.kvm120.utils.callKeyBoard
import ru.avem.kvm120.view.Styles.Companion.medium
import tornadofx.*
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.system.exitProcess


class MainView : View("КВМ-120") {
    private val controller: MainViewController by inject()
    private var min1: RadioMenuItem by singleAssign()
    private var min2: RadioMenuItem by singleAssign()
    private var min3: RadioMenuItem by singleAssign()
    private var min4: RadioMenuItem by singleAssign()
    private var min5: RadioMenuItem by singleAssign()
    private var mainMenubar: MenuBar by singleAssign()
    private var menuBd: Menu by singleAssign()
    private var mainTabPane: TabPane by singleAssign()
    var tfRms: TextField by singleAssign()
    var tfAvr: TextField by singleAssign()
    var tfAmp: TextField by singleAssign()
    var tfCoef: TextField by singleAssign()
    var tfFreq: TextField by singleAssign()
    var tfRazmah: TextField by singleAssign()
    var tfCoefAmp: TextField by singleAssign()
    private var comboboxNeedValue: ComboBox<String> by singleAssign()
    private var rms = "Действующее"
    private var avr = "Среднее"
    private var amp = "Амплитудное"
    private var form = "Форма"
    private var freq = "Частота"
    private val values: ObservableList<String> = observableList(rms, avr, amp, form, freq)
    var listOfValues = mutableListOf<String>()
    var btnTimeAveraging: Button by singleAssign()
    var btnStart: Button by singleAssign()
    private var btnPause: Button by singleAssign()
    private var btnStop: Button by singleAssign()
    private var btnRecord: Button by singleAssign()
    private var tfValueOnGraph: TextField by singleAssign()
    private var tfTimeAveraging: TextField by singleAssign()
    private var lineChart: LineChart<Number, Number> by singleAssign()
    private var series = XYChart.Series<Number, Number>()
    private var realTime = 0.0
    private var isStart = false
    private var isPause = false
    private var isStop = false
    private val togleGroup = ToggleGroup()
    private var timeOut = 60.0

    override fun onBeforeShow() {
        controller.setValues()
        if (!ModbusConnection.isModbusConnected) {
            Platform.runLater {
                Toast.makeText("Подключите преобразователь").show(Toast.ToastType.ERROR)
            }
        } else {
            tfTimeAveraging.text =
                String.format("%.1f", CommunicationModel.avem4VoltmeterController.readTimeAveraging())
        }
    }

    override fun onDock() {
        comboboxNeedValue.items = values
        comboboxNeedValue.selectionModel.selectFirst()
        btnPause.isDisable = true
        btnStop.isDisable = true
        btnRecord.isDisable = true
    }

    override val root = borderpane {
        maxWidth = 1280.0
        maxHeight = 720.0
        top {
            mainMenubar = menubar {
                menu("Меню") {
                    item("Выход") {
                        action {
                            exitProcess(0)
                        }
                    }
                }
                menuBd = menu("База данных") {
                    item("Протоколы") {
                        action {
                            find<ProtocolListWindow>().openModal(
                                modality = Modality.APPLICATION_MODAL, escapeClosesWindow = true,
                                resizable = false, owner = this@MainView.currentWindow
                            )
                        }
                    }
                    menu("Время записи") {
                        min1 = radiomenuitem("1 мин", togleGroup) {
                            isSelected = true
                        }
                        min2 = radiomenuitem("2 мин", togleGroup) {
                        }
                        min3 = radiomenuitem("3 мин", togleGroup) {
                        }
                        min4 = radiomenuitem("4 мин", togleGroup) {
                        }
                        min5 = radiomenuitem("5 мин", togleGroup) {
                        }
                    }
                }
                menu("Информация") {
                    item("Версия ПО") {
                        action {
//                            controller.showAboutUs()
                            Toast.makeText("Неверное значение времени усреднения")
                                .show(Toast.ToastType.ERROR)
                        }
                    }
                }
            }.addClass(Styles.megaHard)
        }
        center {
            mainTabPane = tabpane {
                tab("Значения") {
                    isClosable = false
                    anchorpane {
                        vbox(16.0, Pos.CENTER) {
                            anchorpaneConstraints {
                                leftAnchor = 16.0
                                rightAnchor = 16.0
                                topAnchor = 16.0
                                bottomAnchor = 16.0
                            }
                            hbox(32.0, Pos.CENTER) {
                                vbox(16.0, Pos.CENTER) {
                                    label("Действующее значение, кВ")
                                    tfRms = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                        prefHeight = 200.0
                                    }.addClass(Styles.customfont)
                                }
                                vbox(16.0, Pos.CENTER) {
                                    label("Среднее значение, кВ")
                                    tfAvr = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                        prefHeight = 200.0
                                    }.addClass(Styles.customfont)
                                }
                                vbox(16.0, Pos.CENTER) {
                                    label("Амлитудное значение, кВ")
                                    tfAmp = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                        prefHeight = 200.0

                                    }.addClass(Styles.customfont)
                                }
                            }
                            hbox(32.0, Pos.CENTER) {
                                vbox(16.0, Pos.CENTER) {
                                    label("Коэффициент амплитуды")
                                    tfCoefAmp = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                        prefHeight = 200.0
                                    }.addClass(Styles.customfont)
                                }
                                vbox(16.0, Pos.CENTER) {
                                    label("Частота, Гц")
                                    tfFreq = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                        prefHeight = 200.0
                                    }.addClass(Styles.customfont)
                                }
                            }
                            hbox(16.0, Pos.CENTER) {
                                label("Время усреднения данных, мс:")
                                tfTimeAveraging = textfield {
                                    callKeyBoard()
                                    prefWidth = 300.0
                                }.addClass(Styles.customfont, Styles.bigger)
                                btnTimeAveraging = button("Применить") {
                                    action {
                                        CommunicationModel.avem4VoltmeterController.entryConfigurationMod()
                                        var timeAveraging = 1.0f
                                        try {
                                            timeAveraging = tfTimeAveraging.text.replace(',', '.').toFloat()
                                        } catch (e: Exception) {
                                            Toast.makeText("Неверное значение времени усреднения")
                                                .show(Toast.ToastType.ERROR)
                                        }
                                        CommunicationModel.avem4VoltmeterController.setTimeAveraging(timeAveraging)
                                        Platform.runLater {
                                            sleep(100)
                                            tfTimeAveraging.text = String.format(
                                                "%.1f",
                                                CommunicationModel.avem4VoltmeterController.readTimeAveraging()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                tab("График") {
                    isClosable = false
                    anchorpane {
                        vbox(spacing = 8.0) {
                            anchorpaneConstraints {
                                leftAnchor = 16.0
                                rightAnchor = 16.0
                                topAnchor = 16.0
                                bottomAnchor = 16.0
                            }
                            hbox(16.0, Pos.CENTER) {
                                label("График:")
                                comboboxNeedValue = combobox {
                                }
                                btnStart = button("Старт") {
                                    action {
                                        if (!isPause) {
                                            resetLineChart()
                                        }
                                        if (comboboxNeedValue.selectionModel.selectedItem == form) {
                                            showProgressIndicator()
                                            thread {
                                                isDisable = true
                                                val listOfDots = CommunicationModel.avem4VoltmeterController.readDotsF()
                                                drawGraphFormVoltage(listOfDots)
                                                recordFormGraphInDB(listOfDots)
                                                isDisable = false
                                            }
                                        } else {
                                            isDisable = true
                                            showGraph()
                                            isStart = true
                                            btnPause.isDisable = false
                                            btnStop.isDisable = false
                                            btnRecord.isDisable = false
                                            comboboxNeedValue.isDisable = true
                                        }
                                    }
                                }
                                btnPause = button("Пауза") {
                                    action {
                                        isStart = false
                                        isPause = true
                                        isStop = false
                                        btnStart.isDisable = false
                                        btnPause.isDisable = true
                                        btnRecord.isDisable = true
                                    }
                                }
                                btnStop = button("Стоп") {
                                    action {
                                        handleStop()
                                    }
                                }
                            }
                            hbox(16.0, Pos.CENTER) {
                                tfValueOnGraph = textfield {
                                    alignmentProperty().set(Pos.CENTER)
                                }.addClass(Styles.customfont, Styles.bigger)
                                btnRecord = button("Начать запись") {
                                    action {
                                        when {
                                            min1.isSelected -> {
                                                timeOut = 60.0
                                            }
                                            min2.isSelected -> {
                                                timeOut = 120.0
                                            }
                                            min3.isSelected -> {
                                                timeOut = 180.0
                                            }
                                            min4.isSelected -> {
                                                timeOut = 240.0
                                            }
                                            min5.isSelected -> {
                                                timeOut = 300.0
                                            }
                                        }
                                        isDisable = true
                                        recordGraphInDB()
                                    }
                                }
                            }
                            lineChart = linechart("", NumberAxis(), NumberAxis()) {
                                xAxis.label = "Время, с"
                                prefHeight = 600.0
                                data.add(series)
                                animated = false
                                createSymbols = false
                                isLegendVisible = false
                            }.addClass(Styles.lineChart)
                        }
                    }
                }
                tab("Дополнительные") {
                    isClosable = false
                    anchorpane {
                        vbox(16.0, Pos.CENTER) {
                            anchorpaneConstraints {
                                leftAnchor = 16.0
                                rightAnchor = 16.0
                                topAnchor = 16.0
                                bottomAnchor = 16.0
                            }
                            hbox(32.0, Pos.CENTER) {
                                vbox(16.0, Pos.CENTER) {
                                    label("Размах (двойная амплитуда), кВ")
                                    tfRazmah = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                        prefHeight = 200.0
                                    }.addClass(Styles.customfont)
                                }
                                vbox(16.0, Pos.CENTER) {
                                    label("Коэффицент формы")
                                    tfCoef = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                        prefHeight = 200.0

                                    }.addClass(Styles.customfont)
                                }
                            }
                        }
                    }
                }
            }.addClass(Styles.megaHard)
        }
    }.addClass(Styles.blueTheme, medium)

    private fun handleStop() {
        isStart = false
        isPause = false
        isStop = true
        btnStart.isDisable = false
        btnPause.isDisable = true
        btnStop.isDisable = true
        btnRecord.isDisable = true
        comboboxNeedValue.isDisable = false
    }

    private fun recordGraphInDB() {
        listOfValues.clear()
        thread {
            var realTime = 0.0
            while (isStart) {
                listOfValues.add(tfValueOnGraph.text)
                sleep(100)
                realTime += 0.1
                if (realTime > timeOut) {
                    handleStop()
                }
            }
            saveProtocolToDB(listOfValues)
        }
    }

    private fun recordFormGraphInDB(list: List<Float>) {
        listOfValues.clear()
        for (i in list.indices) {
            listOfValues.add(list[i].toString())
        }
        saveProtocolToDB(listOfValues)
    }

    private fun showProgressIndicator() {
        find<ProgressWindow>().openModal(
            stageStyle = StageStyle.TRANSPARENT,
            modality = Modality.APPLICATION_MODAL, escapeClosesWindow = true,
            owner = this@MainView.currentWindow, resizable = false
        )
    }

    private fun drawGraphFormVoltage(list1: List<Float>) {
        Platform.runLater {
            tfValueOnGraph.text = String.format("%4f", CommunicationModel.coef)
            resetLineChart()
            for (element in list1) {
                series.data.add(XYChart.Data(realTime++, element))
            }
            find<ProgressWindow>().close()
        }
    }

    private fun showGraph() {
        thread {
            Platform.runLater {
                setLabelYAxis()
                if (isStop) {
                    resetLineChart()
                }
            }
            while (isStart) {
                Platform.runLater {
                    when (comboboxNeedValue.selectedItem.toString()) {
                        rms -> {
                            series.data.add(XYChart.Data(realTime, CommunicationModel.uRms))
                            tfValueOnGraph.text = String.format("%.4f", CommunicationModel.uRms)
                        }
                        avr -> {
                            series.data.add(XYChart.Data(realTime, CommunicationModel.uAvr))
                            tfValueOnGraph.text = String.format("%.4f", CommunicationModel.uAvr)
                        }
                        amp -> {
                            series.data.add(XYChart.Data(realTime, CommunicationModel.uAmp))
                            tfValueOnGraph.text = String.format("%.4f", CommunicationModel.uAmp)
                        }
                        freq -> {
                            series.data.add(XYChart.Data(realTime, CommunicationModel.freq))
                            tfValueOnGraph.text = String.format("%.4f", CommunicationModel.freq)
                        }
                    }
                }
                sleep(100)
                realTime += 0.1
            }
        }
    }

    private fun setLabelYAxis() {
        lineChart.xAxis.label = "Время, с"
        when {
            comboboxNeedValue.selectedItem.toString() == form -> {
                lineChart.yAxis.label = "Напряжение, кВ"
                lineChart.xAxis.label = "Время"
            }
            comboboxNeedValue.selectedItem.toString() != freq -> {
                lineChart.yAxis.label = "Напряжение, кВ"
            }
            comboboxNeedValue.selectedItem.toString() == freq -> {
                lineChart.yAxis.label = "Частота, Гц"
            }
        }
    }

    private fun resetLineChart() {
        realTime = 0.0
        lineChart.data.clear()
        series.data.clear()
        lineChart.data.add(series)
    }

    private fun saveProtocolToDB(list: List<String>) {
        val dateFormatter = SimpleDateFormat("dd.MM.y")
        val timeFormatter = SimpleDateFormat("HH:mm:ss")

        val unixTime = System.currentTimeMillis()

        transaction {
            Protocol.new {
                date = dateFormatter.format(unixTime).toString()
                time = timeFormatter.format(unixTime).toString()
                typeOfValue = comboboxNeedValue.selectedItem.toString()
                values = list.toString()
            }
        }
    }
}
