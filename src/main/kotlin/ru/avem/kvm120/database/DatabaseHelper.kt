package ru.avem.kvm120.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kvm120.database.entities.*
import ru.avem.kvm120.database.entities.Users.login
import java.sql.Connection

fun validateDB() {
    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(Users, ProtocolsTable, DopView, ProtocolsDot)
    }

    transaction {
        if (User.all().count() < 2) {
            val admin = User.find {
                login eq "admin"
            }

            if (admin.empty()) {
                User.new {
                    login = "admin"
                    password = "avem"
                    fullName = "admin"
                }

                Protocol.new {
                    date = "10.03.2020"
                    time = "11:30:00"
                    typeOfValue = "Амплитудное значение"
                    values = "1"
                }
                Dop.new {
                    rmsDop = true
                    avrDop = true
                    ampDop = true
                    freqDop = false
                    coefAmpDop = false
                    coefDop = false
                }

                ProtocolDot.new {
                    dateDot = "10.03.2020"
                    timeDot = "11:30:00"
                    rms = "1"
                    avr = "2"
                    amp = "3"
                    freq = "4"
                    сoefAmp = "5"
                    сoefDop = "6"
                }
            }
        }
    }
}
