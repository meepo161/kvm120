package ru.avem.kvm120.communication.devices.enums

import ru.avem.kvm120.communication.devices.Parameter
import ru.avem.kvm120.communication.devices.avem4.AVEM4VoltmeterController

enum class DeviceType(val parameter: Class<out Parameter>) {
    AVEM4Voltmeter(AVEM4VoltmeterController.Parameters::class.java),
}
