package com.michaelflisar.kotbilling.classes

import com.android.billingclient.api.BillingClient

enum class ProductType(val type: String) {
    InApp(BillingClient.ProductType.INAPP),
    Subscription(BillingClient.ProductType.SUBS)
}