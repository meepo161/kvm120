package ru.avem.kvm120.app

import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.stage.Stage
import javafx.stage.StageStyle
import ru.avem.kvm120.communication.CommunicationModel
import ru.avem.kvm120.communication.ModbusConnection
import ru.avem.kvm120.database.validateDB
import ru.avem.kvm120.view.MainView
import ru.avem.kvm120.view.Styles
import tornadofx.App
import tornadofx.FX

class Kvm120 : App(MainView::class, Styles::class) {
    override fun init() {
        validateDB()
    }

    override fun start(stage: Stage) {
        initializeSingletons()
//        stage.isFullScreen = true
//        stage.isResizable = false
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
//        stage.initStyle(StageStyle.TRANSPARENT)
        super.start(stage)
        FX.primaryStage.icons += Image("icon.png")
    }

    private fun initializeSingletons() {
        ModbusConnection
        CommunicationModel
    }

    override fun stop() {
        ModbusConnection.isAppRunning = false
    }
}
