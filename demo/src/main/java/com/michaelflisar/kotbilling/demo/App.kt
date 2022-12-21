package com.michaelflisar.kotbilling.demo

import android.app.Application
import com.michaelflisar.kotbilling.KotBilling
import com.michaelflisar.lumberjack.L

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        KotBilling.init(this) { level, info, exception ->
            if (exception != null) {
                L.callStackCorrection(2).tag("KOTBILLING-LOG").log(level, exception) { info }
            } else {
                L.callStackCorrection(2).tag("KOTBILLING-LOG").log(level) { info }
            }
        }
    }
}