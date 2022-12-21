package com.michaelflisar.kotbilling.classes

data class ProductWithDetails(
    val product: Product,
    val details: WrappedProductDetails
)