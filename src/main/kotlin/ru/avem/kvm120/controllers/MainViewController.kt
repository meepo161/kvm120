package ru.avem.kvm120.controllers

import com.ucicke.k2mod.modbus.util.ModbusUtil
import javafx.application.Platform
import ru.avem.kvm120.communication.CommunicationModel
import ru.avem.kvm120.communication.ModbusConnection
import ru.avem.kvm120.communication.devices.enums.COMState
import ru.avem.kvm120.view.MainView
import tornadofx.Controller
import tornadofx.c
import kotlin.concurrent.thread

class MainViewController : Controller() {
    private val view: MainView by inject()

    init {
        ModbusConnection.indicateCOM = {
            when (it) {
                COMState.OPEN -> view.comIndicate.fill = c("green")
                COMState.CONNECTING -> view.comIndicate.fill = c("yellow")
                COMState.CLOSE -> view.comIndicate.fill = c("red")
            }
        }
    }

    fun setValues() {
        thread {
            while (ModbusConnection.isAppRunning) {
                Platform.runLater {
                    view.tfRms.text = String.format("%.3f", CommunicationModel.uRms)
                    view.tfAvr.text = String.format("%.3f", CommunicationModel.uAvr)
                    view.tfAmp.text = String.format("%.3f", CommunicationModel.uAmp)
                    view.tfFreq.text = String.format("%.3f", CommunicationModel.freq)
                    view.tfCoefAmp.text = String.format("%.3f", CommunicationModel.coefAmp)
                    view.tfRmsDop.text = String.format("%.3f", CommunicationModel.uRms)
                    view.tfAvrDop.text = String.format("%.3f", CommunicationModel.uAvr)
                    view.tfAmpDop.text = String.format("%.3f", CommunicationModel.uAmp)
                    view.tfFreqDop.text = String.format("%.3f", CommunicationModel.freq)
                    view.tfCoefAmpDop.text = String.format("%.3f", CommunicationModel.coefAmp)
                    view.tfCoefDop.text = String.format("%.3f", CommunicationModel.coef)
                    if (!CommunicationModel.avem4VoltmeterController.isResponding) {
                        view.comIndicateDevice.fill = c("red")
                    } else {
                        view.comIndicateDevice.fill = c("green")
                    }
//                    view.btnStart.isDisable = !ModbusConnection.isModbusConnected
//                    view.btnTimeAveraging.isDisable = !ModbusConnection.isModbusConnected
                }
                ModbusUtil.sleep(100)
            }
        }
    }
}
