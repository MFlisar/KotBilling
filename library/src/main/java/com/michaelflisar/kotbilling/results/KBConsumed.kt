package com.michaelflisar.kotbilling.results

import com.michaelflisar.kotbilling.classes.WrappedPurchaseDetails

data class KBConsumed(
    val details: WrappedPurchaseDetails
) : IKBConsumeResult