package ru.avem.kvm120.view

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.avem.kvm120.database.entities.DopView
import ru.avem.kvm120.utils.Singleton.savedView
import ru.avem.kvm120.view.Styles.Companion.megaHard
import tornadofx.*


class DisplaySettingsWindow : View("Настройка отображения допольнительно") {

    private val view: MainView by inject()

    private var cmiRms: CheckBox by singleAssign()
    private var cmiAvr: CheckBox by singleAssign()
    private var cmiAmp: CheckBox by singleAssign()
    private var cmiCoef: CheckBox by singleAssign()
    private var cmiCoefAmp: CheckBox by singleAssign()
    private var cmiFreq: CheckBox by singleAssign()

    override val root = anchorpane {
        vbox(16.0, Pos.CENTER_LEFT) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }
            label("Выберите отображение дополнительных значений")
            cmiRms = checkbox("Действуйщее напряжение") {
                isSelected = savedView.rmsDop
                onAction = EventHandler {
                    if (cmiRms.isSelected) {
                        view.tfRmsDop.show()
                        view.labelRmsDop.show()
                    } else {
                        view.tfRmsDop.hide()
                        view.labelRmsDop.hide()
                    }
                }
            }
            cmiAvr = checkbox("Среднее напряжение") {
                isSelected = savedView.avrDop
                onAction = EventHandler {
                    if (cmiAvr.isSelected) {
                        view.tfAvrDop.show()
                        view.labelAvrDop.show()
                    } else {
                        view.tfAvrDop.hide()
                        view.labelAvrDop.hide()
                    }
                }
            }
            cmiAmp = checkbox("Амплитудное напряжение") {
                isSelected = savedView.ampDop
                onAction = EventHandler {
                    if (cmiAmp.isSelected) {
                        view.tfAmpDop.show()
                        view.labelAmpDop.show()
                    } else {
                        view.tfAmpDop.hide()
                        view.labelAmpDop.hide()
                    }
                }
            }
            cmiCoef = checkbox("Коэффицент формы") {
                isSelected = savedView.coefDop
                onAction = EventHandler {
                    if (cmiCoef.isSelected) {
                        view.tfCoefDop.show()
                        view.labelCoefDop.show()
                    } else {
                        view.tfCoefDop.hide()
                        view.labelCoefDop.hide()
                    }
                }
            }
            cmiCoefAmp = checkbox("Коэффицент амплитуды") {
                isSelected = savedView.coefAmpDop
                onAction = EventHandler {
                    if (cmiCoefAmp.isSelected) {
                        view.tfCoefAmpDop.show()
                        view.labelCoefAmpDop.show()
                    } else {
                        view.tfCoefAmpDop.hide()
                        view.labelCoefAmpDop.hide()
                    }
                }
            }
            cmiFreq = checkbox("Частота") {
                isSelected = savedView.freqDop
                onAction = EventHandler {
                    if (cmiFreq.isSelected) {
                        view.tfFreqDop.show()
                        view.labelFreqDop.show()
                    } else {
                        view.tfFreqDop.hide()
                        view.labelFreqDop.hide()
                    }
                }
            }
            hbox(0.0, Pos.CENTER) {
                button("ОК") {
                    action {
                        transaction {
                            DopView.update({
                                DopView.id eq 1
                            }) {
                                it[rmsDop] = cmiRms.isSelected
                                it[avrDop] = cmiAvr.isSelected
                                it[ampDop] = cmiAmp.isSelected
                                it[coefDop] = cmiCoef.isSelected
                                it[coefAmpDop] = cmiCoefAmp.isSelected
                                it[freqDop] = cmiFreq.isSelected
                            }
                        }
                        close()
                    }
                }
            }
        }
    }.addClass(Styles.blueTheme, megaHard)
}