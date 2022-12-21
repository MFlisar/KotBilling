package com.michaelflisar.kotbilling.results

import com.michaelflisar.kotbilling.classes.Product
import com.michaelflisar.kotbilling.classes.WrappedProductDetails
import com.michaelflisar.kotbilling.classes.WrappedPurchaseDetails

data class KBPurchaseDetails(
    val product: Product,
    val productDetails: WrappedProductDetails,
    val purchaseDetails: List<WrappedPurchaseDetails>
) : IKBPurchaseDetailsResult