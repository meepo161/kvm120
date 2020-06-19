package ru.avem.kvm120.utils

import ru.avem.kvm120.database.entities.Dop
import ru.avem.kvm120.database.entities.Protocol
import ru.avem.kvm120.database.entities.ProtocolDot

object Singleton {
    lateinit var currentProtocol: Protocol
    lateinit var savedView: Dop
    lateinit var currentProtocolDot: ProtocolDot
}
