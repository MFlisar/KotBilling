package com.michaelflisar.kotbilling

import com.android.billingclient.api.BillingResult

fun BillingResult.logInfo(): String = "BillingResult(responseCode=${this.responseCode},debugMessage=${this.debugMessage})"