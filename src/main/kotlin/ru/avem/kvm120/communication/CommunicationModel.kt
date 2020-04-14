package ru.avem.kvm120.communication

import ru.avem.kvm120.communication.ModbusConnection.isAppRunning
import ru.avem.kvm120.communication.ModbusConnection.isModbusConnected
import ru.avem.kvm120.communication.devices.Device
import ru.avem.kvm120.communication.devices.avem4.AVEM4VoltmeterController
import ru.avem.kvm120.communication.devices.enums.DeviceType
import ru.avem.kvm120.communication.devices.enums.UnitID
import ru.avem.kvm120.communication.devices.parameters.DeviceParameter
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.thread

object CommunicationModel : Observer {
    val avem4VoltmeterController = AVEM4VoltmeterController(UnitID.AVEM4, this)

    val deviceControllers =
        listOf<Device>(
            avem4VoltmeterController
        )

    var uRms = 0.0
    var uAmp = 0.0
    var uAvr = 0.0
    var freq = 0.0
    var coef = 0.0
    var razmah = 0.0
    var coefAmp = 0.0
    private var timeAveraging = 0.0

    init {
        thread {
            while (isAppRunning) {
                deviceControllers.forEach {
                    if (isModbusConnected) {
                        try {
                            when (it) {
                                is AVEM4VoltmeterController -> {
                                    try {
                                        it.readValues()
//                                        it.readTimeAveraging()
//                                        it.readDots()
                                        it.isResponding = true
                                    } catch (e: Exception) {
                                        it.isResponding = false
                                    }
                                }
                            }
                        } catch (e: NullPointerException) {
                        }
                    }
                }
                sleep(1)
            }
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        arg as DeviceParameter
        val value = arg.value
        when (arg.device) {
            DeviceType.AVEM4Voltmeter -> {
                when (arg.parameter) {
                    AVEM4VoltmeterController.Parameters.VOLTAGE_RMS -> uRms = value
                    AVEM4VoltmeterController.Parameters.VOLTAGE_AVERAGE -> uAvr = value
                    AVEM4VoltmeterController.Parameters.VOLTAGE_AMP -> uAmp = value
                    AVEM4VoltmeterController.Parameters.FREQUENCY -> freq = value
                    AVEM4VoltmeterController.Parameters.COEFFICENT_FORM -> coef = value
                    AVEM4VoltmeterController.Parameters.RAZMAH -> razmah = value
                    AVEM4VoltmeterController.Parameters.COEFFICENT_AMP -> coefAmp = value
                    AVEM4VoltmeterController.Parameters.TIME_AVERAGING -> timeAveraging = value
                }
            }
        }
    }
}
