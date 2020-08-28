package ru.avem.kvm120.view

import com.ucicke.k2mod.modbus.util.ModbusUtil.sleep
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.shape.Circle
import javafx.stage.Modality
import javafx.stage.StageStyle
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kvm120.communication.CommunicationModel
import ru.avem.kvm120.communication.ModbusConnection
import ru.avem.kvm120.controllers.MainViewController
import ru.avem.kvm120.database.entities.Dop
import ru.avem.kvm120.database.entities.DopView
import ru.avem.kvm120.database.entities.Protocol
import ru.avem.kvm120.database.entities.ProtocolDot
import ru.avem.kvm120.utils.Singleton.savedView
import ru.avem.kvm120.utils.Toast
import ru.avem.kvm120.utils.callKeyBoard
import ru.avem.kvm120.view.Styles.Companion.medium
import ru.avem.kvm120.view.Styles.Companion.megaHard
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
    private var cmiRms: CheckMenuItem by singleAssign()
    private var cmiAvr: CheckMenuItem by singleAssign()
    private var cmiAmp: CheckMenuItem by singleAssign()
    private var cmiCoef: CheckMenuItem by singleAssign()
    private var cmiCoefAmp: CheckMenuItem by singleAssign()
    private var cmiFreq: CheckMenuItem by singleAssign()
    private var mainMenubar: MenuBar by singleAssign()
    private var menuBd: Menu by singleAssign()
    private var mainTabPane: TabPane by singleAssign()
    var tfRms: TextField by singleAssign()
    var tfAvr: TextField by singleAssign()
    var tfAmp: TextField by singleAssign()
    var tfCoefAmp: TextField by singleAssign()
    var tfFreq: TextField by singleAssign()
    var tfRmsDop: TextField by singleAssign()
    var tfAvrDop: TextField by singleAssign()
    var tfAmpDop: TextField by singleAssign()
    var tfCoefAmpDop: TextField by singleAssign()
    var tfFreqDop: TextField by singleAssign()
    var tfCoefDop: TextField by singleAssign()
    var labelRmsDop: Label by singleAssign()
    var labelAvrDop: Label by singleAssign()
    var labelAmpDop: Label by singleAssign()
    var labelCoefAmpDop: Label by singleAssign()
    var labelFreqDop: Label by singleAssign()
    var labelCoefDop: Label by singleAssign()
    private var comboboxNeedValue: ComboBox<String> by singleAssign()
    private var rms = "Действующее"
    private var avr = "Среднее"
    private var amp = "Амплитудное"
    private var form = "Форма"
    private var freq = "Частота"
    private var coefAmp = "Коэффицент амплитуды"
    private var coefForm = "Коэффицент формы"
    private val values: ObservableList<String> = observableList(rms, avr, amp, form, freq, coefAmp, coefForm)
    private var listOfValues = mutableListOf<String>()
    var btnTimeAveraging: Button by singleAssign()
    var btnStart: Button by singleAssign()
    private var btnPause: Button by singleAssign()
    private var btnStop: Button by singleAssign()
    private var checkBoxAuto: CheckBox by singleAssign()
    private var btnRecord: Button by singleAssign()
    private var btnStopRecord: Button by singleAssign()
    private var tfValueOnGraph: TextField by singleAssign()
    private var tfTimeAveraging: TextField by singleAssign()
    private var lineChart: LineChart<Number, Number> by singleAssign()
    private var series = XYChart.Series<Number, Number>()
    private var realTime = 0.0
    private var isStart = false
    private var isStartRecord = false
    private var isPause = false
    private var isStop = false
    private val togleGroup = ToggleGroup()
    private var timeOut = 60.0
    var comIndicate: Circle by singleAssign()
    var comIndicateDevice: Circle by singleAssign()


    init {
        savedView = transaction {
            Dop.find {
                DopView.id eq 1
            }.toList().observable()
        }.first()
    }

    override fun onBeforeShow() {
        if (!ModbusConnection.isModbusConnected) {
            Platform.runLater {
                Toast.makeText("Подключите преобразователь").show(Toast.ToastType.ERROR)
            }
        } else {
            try {
                tfTimeAveraging.text =
                    String.format("%.1f", CommunicationModel.avem4VoltmeterController.readTimeAveraging())
            } catch (e: Exception) {
            }
        }
        controller.setValues()
    }

    override fun onDock() {
        comboboxNeedValue.items = values
        comboboxNeedValue.selectionModel.selectFirst()
        btnPause.isDisable = true
        btnStop.isDisable = true
        btnRecord.isDisable = true
        btnStopRecord.isDisable = true
        showOrHideDopValues()
    }

    private fun showOrHideDopValues() {
        tfRmsDop.isVisible = savedView.rmsDop
        labelRmsDop.isVisible = savedView.rmsDop
        tfRmsDop.isManaged = savedView.rmsDop
        labelRmsDop.isManaged = savedView.rmsDop

        tfAvrDop.isVisible = savedView.avrDop
        labelAvrDop.isVisible = savedView.avrDop
        tfAvrDop.isManaged = savedView.avrDop
        labelAvrDop.isManaged = savedView.avrDop

        tfAmpDop.isVisible = savedView.ampDop
        labelAmpDop.isVisible = savedView.ampDop
        tfAmpDop.isManaged = savedView.ampDop
        labelAmpDop.isManaged = savedView.ampDop

        tfCoefDop.isVisible = savedView.coefDop
        labelCoefDop.isVisible = savedView.coefDop
        tfCoefDop.isManaged = savedView.coefDop
        labelCoefDop.isManaged = savedView.coefDop

        tfCoefAmpDop.isVisible = savedView.coefAmpDop
        labelCoefAmpDop.isVisible = savedView.coefAmpDop
        tfCoefAmpDop.isManaged = savedView.coefAmpDop
        labelCoefAmpDop.isManaged = savedView.coefAmpDop

        tfFreqDop.isVisible = savedView.freqDop
        labelFreqDop.isVisible = savedView.freqDop
        tfFreqDop.isManaged = savedView.freqDop
        labelFreqDop.isManaged = savedView.freqDop
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
                    item("Протоколы точек") {
                        action {
                            find<ProtocolDotListWindow>().openModal(
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
                menu("Настройка") {
                    item("Настройка отображения") {
                        action {
                            find<DisplaySettingsWindow>().openModal(
                                modality = Modality.APPLICATION_MODAL, escapeClosesWindow = true,
                                resizable = false, owner = this@MainView.currentWindow
                            )
                        }
                    }
                }
                menu("Информация") {
                    item("Версия ПО") {
                        action {
                            Toast.makeText("Версия ПО: 1.0.4\nДата: 16.04.2020").show(Toast.ToastType.INFORMATION)
                        }
                    }
                }
            }.addClass(megaHard)
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
                                button("Сохранить точку") {
                                    action {
                                        saveProtocolDotToDB()
                                    }
                                }

                                button("Пауза") {
                                    action {
                                        if (text == "Старт") {
                                            controller.isPausedValues = false
                                            text = "Пауза"
                                        } else {
                                            controller.isPausedValues = true
                                            text = "Старт"
                                        }
                                    }
                                }
                            }
                            hbox(16.0, Pos.CENTER) {
                                label("Время усреднения данных, мс:") {}
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
                                    onAction = EventHandler {
                                        if (selectedItem == form) {
                                            checkBoxAuto.show()
                                            btnRecord.hide()
                                            btnStopRecord.hide()
                                        } else {
                                            checkBoxAuto.hide()
                                            btnRecord.show()
                                            btnStopRecord.show()
                                        }
                                    }
                                }
                                btnStart = button("Старт") {
                                    action {
                                        setLabelYAxis()
                                        if (CommunicationModel.avem4VoltmeterController.isResponding) {
                                            if (!isPause) {
                                                resetLineChart()
                                                btnRecord.isDisable = false
                                            }
                                            if (comboboxNeedValue.selectionModel.selectedItem == form) {
                                                thread {
                                                    btnStop.isDisable = false
                                                    isDisable = true
                                                    do {
                                                        val listOfDots =
                                                            CommunicationModel.avem4VoltmeterController.readDotsF()
                                                        drawGraphFormVoltage(listOfDots)
                                                        recordFormGraphInDB(listOfDots)
                                                        sleep(2000)
                                                    } while (checkBoxAuto.isSelected)
                                                    isDisable = false
                                                    btnStop.isDisable = true
                                                }
                                            } else {
                                                showGraph()
                                                isStart = true
                                                isPause = false
                                                isStop = false
                                                btnPause.isDisable = false
                                                btnStop.isDisable = false
                                                comboboxNeedValue.isDisable = true
                                            }
                                        } else {
                                            Toast.makeText("Нет связи с прибором").show(Toast.ToastType.ERROR)
                                        }
                                    }
                                }
                                checkBoxAuto = checkbox("Авто") {
                                    action {
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
                                        isStartRecord = true
                                        btnRecord.isDisable = true
                                        btnStopRecord.isDisable = false
                                        recordGraphInDB()
                                    }
                                }
                                btnStopRecord = button("Стоп запись") {
                                    action {
                                        isStartRecord = false
                                        btnRecord.isDisable = false
                                        btnStopRecord.isDisable = true
                                    }
                                }
                            }
                            lineChart = linechart("", NumberAxis(), NumberAxis()) {
                                xAxis.label = "Время"
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
                        vbox(4.0, Pos.CENTER) {
                            anchorpaneConstraints {
                                leftAnchor = 16.0
                                rightAnchor = 16.0
                                topAnchor = 16.0
                                bottomAnchor = 16.0
                            }
                            hbox(16.0, Pos.CENTER) {
                                vbox(8.0, Pos.CENTER) {
                                    labelRmsDop = label("Действующее значение, кВ")
                                    tfRmsDop = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                    }.addClass(Styles.customfont)

                                    labelAvrDop = label("Среднее значение, кВ")
                                    tfAvrDop = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                    }.addClass(Styles.customfont)

                                    labelAmpDop = label("Амлитудное значение, кВ")
                                    tfAmpDop = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                    }.addClass(Styles.customfont)
                                }
                                vbox(8.0, Pos.CENTER) {
                                    labelCoefDop = label("Коэффицент формы")
                                    tfCoefDop = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                    }.addClass(Styles.customfont)

                                    labelCoefAmpDop = label("Коэффициент амплитуды")
                                    tfCoefAmpDop = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                    }.addClass(Styles.customfont)

                                    labelFreqDop = label("Частота, Гц")
                                    tfFreqDop = textfield {
                                        alignmentProperty().set(Pos.CENTER)
                                    }.addClass(Styles.customfont)
                                }
                            }
                            hbox(16.0, Pos.CENTER) {
                                button("Пауза") {
                                    action {
                                        if (text == "Старт") {
                                            controller.isPausedDopValues = false
                                            text = "Пауза"
                                        } else {
                                            controller.isPausedDopValues = true
                                            text = "Старт"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }.addClass(megaHard)
        }
        bottom = hbox {
            comIndicate = circle(radius = 18) {
                hboxConstraints {
                    alignment = Pos.CENTER_LEFT
                    hGrow = Priority.ALWAYS
                    marginLeft = 14.0
                    marginBottom = 8.0
                }
                fill = c("cyan")
                stroke = c("black")
                isSmooth = true
            }
            label("  Связь с преобразователем") {
            }.addClass(Styles.blueTheme, megaHard)

            hbox {
                hboxConstraints {
                    alignment = Pos.CENTER_RIGHT
                    hGrow = Priority.ALWAYS
                    marginRight = 14.0
                    marginBottom = 8.0
                }
                label("Связь с прибором  ") {
                }.addClass(Styles.blueTheme, megaHard)
                comIndicateDevice = circle(radius = 18) {
                    fill = c("cyan")
                    stroke = c("black")
                    isSmooth = true
                }
            }
        }
    }.addClass(Styles.blueTheme, medium)

    fun handleStop() {
        isStart = false
        isPause = false
        isStop = true
        btnStart.isDisable = false
        btnPause.isDisable = true
        btnStop.isDisable = true
        btnRecord.isDisable = true
        comboboxNeedValue.isDisable = false
        isStartRecord = false
        checkBoxAuto.isSelected = false
    }

    private fun recordGraphInDB() {
        listOfValues.clear()
        thread {
            var realTime = 0.0
            while (isStartRecord) {
                if (isPause) {
                    listOfValues.add("NaN")
                } else {
                    listOfValues.add(tfValueOnGraph.text)
                }
                sleep(100)
                realTime += 0.1
                if (realTime > timeOut) {
                    handleStop()
                }
            }
            btnRecord.isDisable = false
            btnStopRecord.isDisable = true
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
        }
    }

    private fun showGraph() {
        var series = XYChart.Series<Number, Number>()
        lineChart.data.add(series)
        thread {
            Platform.runLater {
                if (isStop) {
                    resetLineChart()
                }
            }
            while (!isStop) {
                Platform.runLater {
                    if (isPause) {
                        tfValueOnGraph.text = "-"
                        series = XYChart.Series<Number, Number>()
                    } else {
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
                            coefAmp -> {
                                series.data.add(XYChart.Data(realTime, CommunicationModel.coefAmp))
                                tfValueOnGraph.text = String.format("%.4f", CommunicationModel.coefAmp)
                            }
                            coefForm -> {
                                series.data.add(XYChart.Data(realTime, CommunicationModel.coef))
                                tfValueOnGraph.text = String.format("%.4f", CommunicationModel.coef)
                            }
                        }
                    }
                }
                sleep(100)
                realTime += 0.1
            }
        }
    }

    private fun setLabelYAxis() {
        when {
            comboboxNeedValue.selectedItem.toString() == form -> {
                lineChart.yAxis.label = "кВ"
                lineChart.xAxis.label = "Время"
            }
            comboboxNeedValue.selectedItem.toString() == freq -> {
                lineChart.yAxis.label = "Частота, Гц"
            }
            comboboxNeedValue.selectedItem.toString() != freq -> {
                lineChart.yAxis.label = "кВ"
                lineChart.xAxis.label = "Время, сек"
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

    private fun saveProtocolDotToDB() {
        val dateFormatter = SimpleDateFormat("dd.MM.y")
        val timeFormatter = SimpleDateFormat("HH:mm:ss")

        val unixTime = System.currentTimeMillis()

        transaction {
            ProtocolDot.new {
                dateDot = dateFormatter.format(unixTime).toString()
                timeDot = timeFormatter.format(unixTime).toString()
                rms = tfRms.text
                avr = tfAvr.text
                amp = tfAmp.text
                freq = tfFreq.text
                сoefAmp = tfCoefAmp.text
                сoefDop = tfCoefDop.text
            }
        }
    }
}
