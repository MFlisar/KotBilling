package com.michaelflisar.kotbilling.results

import com.michaelflisar.kotbilling.classes.WrappedPurchaseDetails

data class KBAcknowledgeResult(
    val type: Type,
    val details: WrappedPurchaseDetails
) : IKBAcknowledgeResult {

    enum class Type {
        AlreadyAcknowledged,
        SuccessfullyAcknowledges
    }
}