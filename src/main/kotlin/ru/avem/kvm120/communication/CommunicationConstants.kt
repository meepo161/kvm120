package ru.avem.kvm120.communication

const val CONVERTER_NAME = "CP2103 USB to RS-485"
const val ENCODING = "rtu"
const val BAUD_RATE = 115200
const val DATABITS = 8
const val PARITY = 0
const val STOPBITS = 1


const val END_MIN = 0b1
const val END_MAX = 0b10
const val IZM_COIL = 0b100
const val MAGNIT_COIL = 0b1000
const val CURRENT_RELE = 0b10000
const val IZM1 = 0b100000
const val IZM2 = 0b1000000
const val IZM3 = 0b10000000