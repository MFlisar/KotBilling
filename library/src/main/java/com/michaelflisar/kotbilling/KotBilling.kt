package com.michaelflisar.kotbilling

import android.app.Activity
import android.content.Context
import com.michaelflisar.kotbilling.classes.*
import com.michaelflisar.kotbilling.results.*

object KotBilling {

    private lateinit var manager: KotBillingManager

    var logger: ((level: Int, info: String, e: Exception?) -> Unit)? = null

    internal fun init(context: Context,) {
        manager = KotBillingManager(context)
    }

    fun reset() {
        manager.reset()
    }

    // -----------------
    // query product(s) functions
    // -----------------

    suspend fun queryProducts(products: List<Product>): IKBProductResult {
        val connectionState = manager.connect()
        when (connectionState) {
            ConnectionState.Connected -> {
                // connected, we continue...
            }
            ConnectionState.Disconnected,
            is ConnectionState.Error -> {
                return KBError(
                    KBError.ErrorType.ConnectionFailed,
                    connectionState
                )
            }
        }

        // we are connected => we query the product now
        return manager.queryProducts(products, connectionState)
    }

    // -----------------
    // query purchase(s) functions
    // -----------------

    suspend fun queryPurchases(productType: ProductType): IKBPurchaseQueryResult {

        val connectionState = manager.connect()
        when (connectionState) {
            ConnectionState.Connected -> {
                // connected, we continue...
            }
            ConnectionState.Disconnected,
            is ConnectionState.Error -> {
                return KBError(
                    KBError.ErrorType.ConnectionFailed,
                    connectionState
                )
            }
        }

        // we are connected => we query the purchases now
        return manager.queryPurchases(productType, connectionState)
    }

    // -----------------
    // purchase functions
    // -----------------

    suspend fun purchase(
        activity: Activity,
        product: Product,
        offerToken: String? = null
    ): IKBPurchaseResult {

        val connectionState = manager.connect()
        when (connectionState) {
            ConnectionState.Connected -> {
                // connected, we continue...
            }
            ConnectionState.Disconnected,
            is ConnectionState.Error -> {
                return KBError(
                    KBError.ErrorType.ConnectionFailed,
                    connectionState
                )
            }
        }

        val products = queryProducts(listOf(product))
        when (products) {
            is KBError -> return products
            is KBProductDetailsList -> {
                // continue
            }
        }

        val productWithDetails = products.details.find { it.product == product }
        if (productWithDetails == null) {
            // WTF - product details not found in successful result - I assume this will never happen...
            return KBError(KBError.ErrorType.ProductDetailsNotFound(product), connectionState)
        }

        return purchase(activity, productWithDetails, offerToken)
    }

    suspend fun purchase(
        activity: Activity,
        product: ProductWithDetails,
        offerToken: String?
    ): IKBPurchaseResult = purchase(activity, product.product, product.details, offerToken)

    suspend fun purchase(
        activity: Activity,
        product: Product,
        details: WrappedProductDetails,
        offerToken: String?
    ): IKBPurchaseResult {

        val connectionState = manager.connect()
        when (connectionState) {
            ConnectionState.Connected -> {
                // connected, we continue...
            }
            ConnectionState.Disconnected,
            is ConnectionState.Error -> {
                return KBError(
                    KBError.ErrorType.ConnectionFailed,
                    connectionState
                )
            }
        }

        // we are connected => we purchase the product now
        return manager.purchase(activity, product, details, offerToken, connectionState)
    }
}