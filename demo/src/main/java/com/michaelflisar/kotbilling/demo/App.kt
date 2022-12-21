package com.michaelflisar.kotbilling.demo

import android.app.Application
import com.michaelflisar.kotbilling.KotBilling
import com.michaelflisar.kotbilling.classes.LogLevel
import com.michaelflisar.lumberjack.L

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        KotBilling.init(this) { level, message ->
            if (level == LogLevel.Info) {
                L.callStackCorrection(2).d { message }
            } else {
                L.callStackCorrection(2).e { message }
            }
        }
    }
}