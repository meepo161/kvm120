package ru.avem.kvm120.view

import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.stage.FileChooser
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kvm120.database.entities.ProtocolDot
import ru.avem.kvm120.database.entities.ProtocolsDot
import ru.avem.kvm120.protocol.saveProtocolDotAsWorkbook
import ru.avem.kvm120.utils.Singleton
import ru.avem.kvm120.utils.Singleton.currentProtocolDot
import ru.avem.kvm120.utils.callKeyBoard
import ru.avem.kvm120.utils.openFile
import tornadofx.*
import tornadofx.controlsfx.confirmNotification
import java.io.File

class ProtocolDotListWindow : View("Протоколы") {
    private var tableViewDotProtocols: TableView<ProtocolDot> by singleAssign()
    private lateinit var protocols: ObservableList<ProtocolDot>
    override fun onDock() {
        protocols = transaction {
            ProtocolDot.all().toList().observable()
        }

        tableViewDotProtocols.items = protocols
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
                        tableViewDotProtocols.items = protocols.filter { it.dateDot.contains(text) }.observable()
                    } else {
                        tableViewDotProtocols.items = protocols
                    }
                }
            }

            tableViewDotProtocols = tableview {
                prefHeight = 700.0
                columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY)

                column("Дата", ProtocolDot::dateDot)
                column("Время", ProtocolDot::timeDot)
                column("Действуещее", ProtocolDot::avr)
                column("Амплитудное", ProtocolDot::amp)
                column("Среднее", ProtocolDot::rms)
                column("Частота", ProtocolDot::freq)
                column("Коэф амплитуды", ProtocolDot::сoefAmp)
                column("Коэф формы", ProtocolDot::сoefDop)
            }

            hbox(spacing = 16.0) {
                alignmentProperty().set(Pos.CENTER)

                button("Открыть") {
                    action {
                        if (tableViewDotProtocols.selectedItem != null) {
                            Singleton.currentProtocolDot = transaction {
                                ProtocolDot.find {
                                    ProtocolsDot.id eq tableViewDotProtocols.selectedItem!!.id
                                }.toList().observable()
                            }.first()

                            close()
                            saveProtocolDotAsWorkbook(currentProtocolDot)
                            openFile(File("protocolDot.xlsx"))
                        }
                    }
                }
                button("Сохранить как") {
                    action {
                        if (tableViewDotProtocols.selectedItem != null) {
                            val files = chooseFile(
                                "Выберите директорию для сохранения",
                                arrayOf(FileChooser.ExtensionFilter("XSLX Files (*.xlsx)", "*.xlsx")),
                                FileChooserMode.Save,
                                this@ProtocolDotListWindow.currentWindow
                            ) {
                                this.initialDirectory = File(System.getProperty("user.home"))
                            }

                            if (files.isNotEmpty()) {
                                saveProtocolDotAsWorkbook(tableViewDotProtocols.selectedItem!!, files.first().absolutePath)
                                confirmNotification(
                                    "Готово",
                                    "Успешно сохранено",
                                    Pos.BOTTOM_CENTER,
                                    owner = this@ProtocolDotListWindow.currentWindow
                                )
                            }
                        }
                    }
                }
                button("Сохранить все") {
                    action {
                        if (tableViewDotProtocols.items.size > 0) {
                            val dir = chooseDirectory(
                                "Выберите директорию для сохранения",
                                File(System.getProperty("user.home")),
                                this@ProtocolDotListWindow.currentWindow
                            )

                            if (dir != null) {
                                tableViewDotProtocols.items.forEach {
                                    val file = File(dir, "${it.id.value}.xlsx")
                                    saveProtocolDotAsWorkbook(it, file.absolutePath)
                                }
                                confirmNotification(
                                    "Готово",
                                    "Успешно сохранено",
                                    Pos.BOTTOM_CENTER,
                                    owner = this@ProtocolDotListWindow.currentWindow
                                )
                            }
                        }
                    }
                }
                button("Удалить") {
                    action {
                        if (tableViewDotProtocols.selectedItem != null) {
                            transaction {
                                ProtocolsDot.deleteWhere {
                                    ProtocolsDot.id eq tableViewDotProtocols.selectedItem!!.id
                                }
                            }

                            tableViewDotProtocols.items = transaction {
                                ProtocolDot.all().toList().observable()
                            }
                        }
                    }
                }
            }
        }
    }.addClass(Styles.extraHard, Styles.blueTheme)
}
