package ru.avem.kvm120.controllers

import com.ucicke.k2mod.modbus.util.ModbusUtil
import javafx.application.Platform
import ru.avem.kvm120.communication.CommunicationModel
import ru.avem.kvm120.communication.ModbusConnection
import ru.avem.kvm120.view.MainView
import tornadofx.Controller
import kotlin.concurrent.thread

class MainViewController : Controller() {
    private val view: MainView by inject()

    fun setValues() {
        thread {
            while (ModbusConnection.isAppRunning) {
                Platform.runLater {
                    view.tfRms.text = String.format("%.3f", CommunicationModel.uRms)
                    view.tfAvr.text = String.format("%.3f", CommunicationModel.uAvr)
                    view.tfAmp.text = String.format("%.3f", CommunicationModel.uAmp)
                    view.tfCoef.text = String.format("%.3f", CommunicationModel.coef)
                    view.tfFreq.text = String.format("%.3f", CommunicationModel.freq)
                    view.tfRazmah.text = String.format("%.3f", CommunicationModel.razmah)
                    view.tfCoefAmp.text = String.format("%.3f", CommunicationModel.coefAmp)
                    view.btnStart.isDisable = !ModbusConnection.isModbusConnected
                    view.btnTimeAveraging.isDisable = !ModbusConnection.isModbusConnected
                }
                ModbusUtil.sleep(100)
            }
        }
    }
}
