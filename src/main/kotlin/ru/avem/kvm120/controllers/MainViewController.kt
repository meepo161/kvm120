package ru.avem.kvm120.controllers

import com.ucicke.k2mod.modbus.util.ModbusUtil
import javafx.application.Platform
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kvm120.communication.CommunicationModel
import ru.avem.kvm120.communication.ModbusConnection
import ru.avem.kvm120.database.entities.Protocol
import ru.avem.kvm120.utils.Toast
import ru.avem.kvm120.view.MainView
import tornadofx.Controller
import java.text.SimpleDateFormat

class MainViewController : Controller() {
    val view: MainView by inject()

    private val isDevicesResponding: Boolean
        get() = CommunicationModel.avem4VoltmeterController.isResponding

    fun showAboutUs() {
        Toast.makeText("Версия ПО: 0.0.0a, Дата: 04.03.2020").show(Toast.ToastType.INFORMATION)
    }

    fun setValues() {
        Thread {
            while (ModbusConnection.isAppRunning) {
                Platform.runLater {
                    view.tfRms.text = String.format("%.4f", CommunicationModel.uRms)
                    view.tfAvr.text = String.format("%.4f", CommunicationModel.uAvr)
                    view.tfAmp.text = String.format("%.4f", CommunicationModel.uAmp)
                    view.tfCoef.text = String.format("%.4f", CommunicationModel.coef)
                    view.tfFreq.text = String.format("%.4f", CommunicationModel.freq)
                    view.tfRazmah.text = String.format("%.4f", CommunicationModel.razmah)
                    view.tfCoefAmp.text = String.format("%.4f", CommunicationModel.coefAmp)
                }
                ModbusUtil.sleep(100)
            }
        }.start()
    }

    private fun saveProtocolToDB() {
        val dateFormatter = SimpleDateFormat("dd.MM.y")
        val timeFormatter = SimpleDateFormat("HH:mm:ss")

        val unixTime = System.currentTimeMillis()

        transaction {
            Protocol.new {
                date = dateFormatter.format(unixTime).toString()
                time = timeFormatter.format(unixTime).toString()
            }
        }
    }
}
