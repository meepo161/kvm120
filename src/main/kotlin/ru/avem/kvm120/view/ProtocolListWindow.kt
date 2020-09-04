package ru.avem.kvm120.view

import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.stage.FileChooser
import javafx.stage.Modality
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kvm120.database.entities.Protocol
import ru.avem.kvm120.database.entities.ProtocolsTable
import ru.avem.kvm120.protocol.saveProtocolAsWorkbook
import ru.avem.kvm120.utils.Singleton
import ru.avem.kvm120.utils.callKeyBoard
import ru.avem.kvm120.utils.openFile
import tornadofx.*
import tornadofx.controlsfx.confirmNotification
import java.io.File

class ProtocolListWindow : View("Протоколы графиков") {
    private var tableViewProtocols: TableView<Protocol> by singleAssign()
    private lateinit var protocols: ObservableList<Protocol>
    override fun onDock() {
        protocols = transaction {
            Protocol.all().toList().observable()
        }
        tableViewProtocols.items = protocols
    }


    override val root = anchorpane {
        prefWidth = 900.0
        prefHeight = 500.0

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
                protocols = transaction {
                    Protocol.all().toList().observable()
                }
                items = protocols
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
                button("Открыть таблицу") {
                    action {
                        if (tableViewProtocols.selectedItem != null) {
                            Singleton.currentProtocol = transaction {
                                Protocol.find {
                                    ProtocolsTable.id eq tableViewProtocols.selectedItem!!.id
                                }.toList().observable()
                            }.first()
                            close()
                            saveProtocolAsWorkbook(Singleton.currentProtocol)
                            openFile(File("protocol.xlsx"))
                        }
                    }
                }
                button("Сохранить как") {
                    action {
                        if (tableViewProtocols.selectedItem != null) {
                            val files = chooseFile(
                                "Выберите директорию для сохранения",
                                arrayOf(FileChooser.ExtensionFilter("XSLX Files (*.xlsx)", "*.xlsx")),
                                FileChooserMode.Save,
                                this@ProtocolListWindow.currentWindow
                            ) {
                                this.initialDirectory = File(System.getProperty("user.home"))
                            }

                            if (files.isNotEmpty()) {
                                saveProtocolAsWorkbook(tableViewProtocols.selectedItem!!, files.first().absolutePath)

                                Platform.runLater {
                                    confirmNotification(
                                        "Готово",
                                        "Успешно сохранено",
                                        Pos.BOTTOM_CENTER,
                                        owner = this@ProtocolListWindow.currentWindow
                                    )
                                }
                            }
                        }
                    }
                }
                button("Сохранить все") {
                    action {
                        if (tableViewProtocols.items.size > 0) {
                            val dir = chooseDirectory(
                                "Выберите директорию для сохранения",
                                File(System.getProperty("user.home")),
                                this@ProtocolListWindow.currentWindow
                            )

                            if (dir != null) {
                                tableViewProtocols.items.forEach {
                                    val file = File(dir, "${it.id.value}.xlsx")
                                    saveProtocolAsWorkbook(it, file.absolutePath)
                                }
                                Platform.runLater {
                                    confirmNotification(
                                        "Готово",
                                        "Успешно сохранено",
                                        Pos.BOTTOM_CENTER,
                                        owner = this@ProtocolListWindow.currentWindow
                                    )
                                }
                            }
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
    }.addClass(Styles.blueThemeSmall, Styles.raspberryStyle)
}
