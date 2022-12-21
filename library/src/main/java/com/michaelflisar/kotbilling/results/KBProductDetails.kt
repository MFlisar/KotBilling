package com.michaelflisar.kotbilling.results

import com.michaelflisar.kotbilling.classes.ProductWithDetails

data class KBProductDetailsList(
    val details: List<ProductWithDetails>
) : IKBProductResult