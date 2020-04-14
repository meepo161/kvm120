package ru.avem.kvm120.view

import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.stage.Modality
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kvm120.database.entities.Protocol
import ru.avem.kvm120.database.entities.ProtocolsTable
import ru.avem.kvm120.utils.Singleton
import ru.avem.kvm120.utils.callKeyBoard
import tornadofx.*

class ProtocolListWindow : View("Протоколы") {
    private var tableViewProtocols: TableView<Protocol> by singleAssign()
    private lateinit var protocols: ObservableList<Protocol>
    override fun onDock() {
        protocols = transaction {
            Protocol.all().toList().observable()
        }

        tableViewProtocols.items = protocols
    }

    override val root = anchorpane {
        prefWidth = 1200.0
        prefHeight = 720.0

        vbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }

            alignmentProperty().set(Pos.CENTER)

            textfield {
                callKeyBoard()
                prefWidth = 600.0

                promptText = "Фильтр"
                alignment = Pos.CENTER

                onKeyReleased = EventHandler {
                    if (!text.isNullOrEmpty()) {
                        tableViewProtocols.items = protocols.filter { it.date.contains(text) }.observable()
                    } else {
                        tableViewProtocols.items = protocols
                    }
                }
            }

            tableViewProtocols = tableview {
                prefHeight = 700.0
                columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY)

                column("Тип", Protocol::typeOfValue)
                column("Значения", Protocol::values)
                column("Дата", Protocol::date)
                column("Время", Protocol::time)
            }

            hbox(spacing = 16.0) {
                alignmentProperty().set(Pos.CENTER)

                button("Открыть") {
                    action {
                        if (tableViewProtocols.selectedItem != null) {
                            Singleton.currentProtocol = transaction {
                                Protocol.find {
                                    ProtocolsTable.id eq tableViewProtocols.selectedItem!!.id
                                }.toList().observable()
                            }.first()

                            find<GraphHistoryWindow>().openModal(
                                modality = Modality.APPLICATION_MODAL, escapeClosesWindow = true,
                                resizable = false, owner = this@ProtocolListWindow.currentWindow
                            )
                        }
                    }
                }
                button("Удалить") {
                    action {
                        if (tableViewProtocols.selectedItem != null) {
                            transaction {
                                ProtocolsTable.deleteWhere {
                                    ProtocolsTable.id eq tableViewProtocols.selectedItem!!.id
                                }
                            }

                            tableViewProtocols.items = transaction {
                                Protocol.all().toList().observable()
                            }
                        }
                    }
                }
            }
        }
    }.addClass(Styles.extraHard, Styles.blueTheme)
}
