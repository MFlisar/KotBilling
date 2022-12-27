package com.michaelflisar.kotbilling.demo

import com.michaelflisar.kotbilling.classes.Product
import com.michaelflisar.kotbilling.classes.ProductType

object DemoBillingSetup {
    val PRODUCT_NOT_CONSUMABLE = Product("p1", ProductType.InApp, false)
    val PRODUCT_CONSUMABLE = Product("p2", ProductType.InApp, true)
}