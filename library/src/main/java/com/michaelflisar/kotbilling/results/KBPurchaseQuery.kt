package com.michaelflisar.kotbilling.results

import com.michaelflisar.kotbilling.classes.Product
import com.michaelflisar.kotbilling.classes.ProductType
import com.michaelflisar.kotbilling.classes.WrappedPurchaseDetails

data class KBPurchaseQuery(
    val productType: ProductType,
    val details: List<WrappedPurchaseDetails>
) : IKBPurchaseQueryResult {
    fun get(product: Product) = details.filter { it.products.contains(product.id) }
    fun isAnyPurchaseOwned(vararg products: Product): Boolean {
        for (p in products) {
            if (get(p).map { it.isOwned }.any { true })
                return true
        }
        return false
    }
}