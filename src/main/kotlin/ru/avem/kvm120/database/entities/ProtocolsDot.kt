package ru.avem.kvm120.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ProtocolsDot : IntIdTable() {
    val dateDot = varchar("dateDot", 256)
    val timeDot = varchar("timeDot", 256)
    val rms = varchar("rms", 256)
    val avr = varchar("avr", 256)
    val amp = varchar("amp", 256)
    val freq = varchar("freq", 256)
    val сoefAmp = varchar("сoefAmp", 256)
    val сoefDop = varchar("сoefDop", 256)
}

class ProtocolDot(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProtocolDot>(ProtocolsDot)

    var dateDot by ProtocolsDot.dateDot
    var timeDot by ProtocolsDot.timeDot
    var rms by ProtocolsDot.rms
    var avr by ProtocolsDot.avr
    var amp by ProtocolsDot.amp
    var freq by ProtocolsDot.freq
    var сoefAmp by ProtocolsDot.сoefAmp
    var сoefDop by ProtocolsDot.сoefDop

    override fun toString(): String {
        return "$id"
    }
}
