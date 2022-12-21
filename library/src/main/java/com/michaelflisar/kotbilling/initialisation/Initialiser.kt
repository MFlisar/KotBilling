package com.michaelflisar.kotbilling.initialisation

import android.content.Context
import androidx.startup.Initializer
import com.michaelflisar.kotbilling.KotBilling

class Initialiser : Initializer<Unit> {
    override fun create(context: Context): Unit {
        KotBilling.init(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
