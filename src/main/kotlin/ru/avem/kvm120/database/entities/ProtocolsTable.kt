package ru.avem.kvm120.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ProtocolsTable : IntIdTable() {
    val date = varchar("date", 256)
    val time = varchar("time", 256)
    val typeOfValue = varchar("typeOfValue", 256)
    val values =  varchar("values", 65536)
}

class Protocol(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Protocol>(ProtocolsTable)

    var date by ProtocolsTable.date
    var time by ProtocolsTable.time
    var typeOfValue by ProtocolsTable.typeOfValue
    var values by ProtocolsTable.values

    override fun toString(): String {
        return "$id. $typeOfValue - $date"
    }
}
