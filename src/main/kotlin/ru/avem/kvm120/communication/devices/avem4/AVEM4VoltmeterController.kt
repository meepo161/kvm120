package ru.avem.kvm120.communication.devices.avem4

import java.util.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.slf4j.LoggerFactory
import ru.avem.kvm120.communication.ModbusConnection
import ru.avem.kvm120.communication.devices.Device
import ru.avem.kvm120.communication.devices.Parameter
import ru.avem.kvm120.communication.devices.enums.DeviceType
import ru.avem.kvm120.communication.devices.enums.UnitID
import ru.avem.kvm120.communication.devices.parameters.DeviceParameter
import ru.avem.kvm120.utils.toInt
import com.ucicke.k2mod.modbus.procimg.SimpleRegister

class AVEM4VoltmeterController(private val unitID: UnitID, observer: Observer) : Observable(), Device {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        const val U_RMS_REGISTER = 0x1010
        const val TIME_MEASURE = 0x10C8
        const val SERIAL_NUMBER = 0x1108
        val DEVICE_ID = DeviceType.AVEM4Voltmeter
    }

    enum class Parameters : Parameter {
        IS_RESPONDING,
        VOLTAGE_AMP,
        VOLTAGE_AVERAGE,
        VOLTAGE_RMS,
        FREQUENCY,
        COEFFICENT_FORM,
        TIME_AVERAGING
    }

    override var isResponding = false
        set(value) {
            field = value
            notice(DeviceParameter(unitID.id, DEVICE_ID, Parameters.IS_RESPONDING, field.toInt()))
        }

    init {
        addObserver(observer)
    }

    fun stopRewriteDotesFlag() {
        try {
            ModbusConnection.writeSingleRegister(unitID.id, 4284, SimpleRegister(0.toShort()))
        } catch (e: Exception) {
            isResponding = false
        }
    }

    fun startRewriteDotesFlag() {
        try {
            ModbusConnection.writeSingleRegister(unitID.id, 4284, SimpleRegister(1.toShort()))
        } catch (e: Exception) {
            isResponding = false
        }
    }

    fun readDotsF(): List<Float> {
        stopRewriteDotesFlag()
        val startRegister = 6656
        val endRegister = startRegister + 2200
        val dots = mutableListOf<Float>()
        for (registerIndex in startRegister..endRegister - 50 step 50) {
            try {
                val shorts = ModbusConnection.readInputRegisters(unitID.id, registerIndex, 50)
                for (index in shorts.indices step 2) {
                    dots.add(
                        (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
                            .putShort(shorts[index].toShort())
                            .putShort(shorts[index + 1].toShort())
                            .flip() as ByteBuffer)
                            .float
                    )
                }
            } catch (e: Exception) {
            }
        }
        startRewriteDotesFlag()
        return dots
    }

    fun readValues() {
        val readInputRegisters = ModbusConnection.readInputRegisters(unitID.id, U_RMS_REGISTER, 16)

        val voltageAmp = (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
            .putShort(readInputRegisters[0].toShort())
            .putShort(readInputRegisters[1].toShort())
            .flip() as ByteBuffer).float

        val voltageAvr = (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
            .putShort(readInputRegisters[2].toShort())
            .putShort(readInputRegisters[3].toShort())
            .flip() as ByteBuffer).float

        val voltageRms = (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
            .putShort(readInputRegisters[4].toShort())
            .putShort(readInputRegisters[5].toShort())
            .flip() as ByteBuffer).float

        val frequency = (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
            .putShort(readInputRegisters[6].toShort())
            .putShort(readInputRegisters[7].toShort())
            .flip() as ByteBuffer).float

        val coefficentForm = (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
            .putShort(readInputRegisters[14].toShort())
            .putShort(readInputRegisters[15].toShort())
            .flip() as ByteBuffer).float

        notice(DeviceParameter(unitID.id, DeviceType.AVEM4Voltmeter, Parameters.VOLTAGE_AMP, voltageAmp))
        notice(DeviceParameter(unitID.id, DeviceType.AVEM4Voltmeter, Parameters.VOLTAGE_AVERAGE, voltageAvr))
        notice(DeviceParameter(unitID.id, DeviceType.AVEM4Voltmeter, Parameters.VOLTAGE_RMS, voltageRms))
        notice(DeviceParameter(unitID.id, DeviceType.AVEM4Voltmeter, Parameters.FREQUENCY, frequency))
        notice(DeviceParameter(unitID.id, DeviceType.AVEM4Voltmeter, Parameters.COEFFICENT_FORM, coefficentForm))
    }

    fun entryConfigurationMod() {
        val readInputRegisters = ModbusConnection.readInputRegisters(unitID.id, SERIAL_NUMBER, 2)
        try {
            ModbusConnection.writeSingleRegister(
                unitID.id,
                SERIAL_NUMBER,
                SimpleRegister(readInputRegisters[0].toShort())
            )
            ModbusConnection.writeSingleRegister(
                unitID.id,
                SERIAL_NUMBER + 1,
                SimpleRegister(readInputRegisters[1].toShort())
            )
        } catch (e: Exception) {
            isResponding = false
        }
    }

    fun readTimeAveraging(): Float {
        val readInputRegisters = ModbusConnection.readInputRegisters(unitID.id, TIME_MEASURE, 2)
        return (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
            .putShort(readInputRegisters[0].toShort())
            .putShort(readInputRegisters[1].toShort())
            .flip() as ByteBuffer).float
    }

    fun setTimeAveraging(time: Float) {
        val dataBuffer = ByteBuffer.allocate(4).putFloat(time).flip() as ByteBuffer
        try {
            ModbusConnection.writeSingleRegister(unitID.id, TIME_MEASURE, SimpleRegister(dataBuffer.getShort(1)))
            ModbusConnection.writeSingleRegister(unitID.id, TIME_MEASURE + 1, SimpleRegister(dataBuffer.getShort(0)))
        } catch (e: Exception) {
            isResponding = false
        }
    }

    private fun notice(parameter: DeviceParameter) {
        setChanged()
        notifyObservers(parameter)
    }
}