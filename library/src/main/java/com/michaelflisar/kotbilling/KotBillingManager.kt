package com.michaelflisar.kotbilling

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.michaelflisar.kotbilling.classes.*
import com.michaelflisar.kotbilling.results.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlin.coroutines.suspendCoroutine

internal class KotBillingManager(
    context: Context
) {

    private var client: BillingClient
    private var purchaseCallback: PurchasesUpdatedListener? = null
    private val purchasesUpdatedListener: PurchasesUpdatedListener =
        PurchasesUpdatedListener { result, purchases ->
            purchaseCallback?.onPurchasesUpdated(result, purchases)
        }
    private val flowConnectionState = MutableStateFlow<ConnectionState?>(null)

    init {
        client = BillingClient.newBuilder(context.applicationContext)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    fun reset() {
        client.endConnection()
        resetConnectionState()
    }

    // -----------------
    // Connection
    // -----------------

    suspend fun connect(): ConnectionState {
        val state = flowConnectionState.first()
        return if (state == null) {
            startConnection()
            flowConnectionState.first { it != null }!!
        } else state
    }

    private fun resetConnectionState() {
        flowConnectionState.tryEmit(null)
    }

    private fun startConnection() {
        if (client.isReady) {
            // nothing to do... should never be called, because in this case the flowConnectionState should already have a state
            // for the sake of completeness, we emit the connected state here...
            flowConnectionState.tryEmit(ConnectionState.Connected)
        } else {
            client.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        KotBilling.logger?.invoke(
                            Log.DEBUG,
                            "[KotBilling::connect] Connected to PlayStore successful",
                            null
                        )
                        flowConnectionState.tryEmit(ConnectionState.Connected)
                    } else {
                        KotBilling.logger?.invoke(
                            Log.ERROR,
                            "[KotBilling::connect] Connecting to PlayStore failed | responseCode = ${result.responseCode} | message = ${result.debugMessage}",
                            null
                        )
                        flowConnectionState.tryEmit(
                            ConnectionState.Error(
                                result.responseCode,
                                result.debugMessage
                            )
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    KotBilling.logger?.invoke(
                        Log.DEBUG,
                        "[KotBilling::connect] Disconnected from PlayStore",
                        null
                    )
                    flowConnectionState.tryEmit(ConnectionState.Disconnected)
                }
            })
        }
    }

    // -----------------
    // Products
    // -----------------

    suspend fun queryProducts(
        products: List<Product>,
        connectionState: ConnectionState
    ): IKBProductResult {

        val productList = products.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it.id)
                .setProductType(it.type.type)
                .build()
        }

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

        val result = client.queryProductDetails(queryProductDetailsParams)
        val productDetailsList = result.productDetailsList

        return if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            val error = KBError(
                KBError.ErrorType.QueryProductDetailsFailed(products, result.billingResult),
                connectionState
            )
            KotBilling.logger?.invoke(
                Log.ERROR,
                "[KotBilling::queryProducts] Failed! | productDetailsList = ${result.productDetailsList?.size} | error = $error}",
                null
            )
            error
        } else {
            KotBilling.logger?.invoke(
                Log.DEBUG,
                "[KotBilling::queryProducts] Query executed | products = ${products.joinToString(",")}",
                null
            )
            // we keep the order of the products
            val details = products.mapNotNull { p ->
                val details = productDetailsList?.find { it.productId == p.id }
                details?.let { ProductWithDetails(p, WrappedProductDetails(it)) }
            }
            KBProductDetailsList(details)
        }
    }

    // -----------------
    // Purchases
    // -----------------

    suspend fun queryPurchases(
        productType: ProductType,
        connectionState: ConnectionState
    ): IKBPurchaseQueryResult {

        val queryPurchasesParams =
            QueryPurchasesParams
                .newBuilder()
                .setProductType(productType.type)
                .build()

        val result = client.queryPurchasesAsync(queryPurchasesParams)

        return if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            val error = KBError(
                KBError.ErrorType.QueryPurchasesFailed(productType, result.billingResult),
                connectionState
            )
            KotBilling.logger?.invoke(
                Log.ERROR,
                "[KotBilling::queryPurchases] Failed! | purchasesList = ${result.purchasesList.size} | error = $error}",
                null
            )
            error
        } else {
            KotBilling.logger?.invoke(
                Log.DEBUG,
                "[KotBilling::queryPurchases] Query executed | productType = $productType}",
                null
            )
            val details = result.purchasesList.map { WrappedPurchaseDetails(it) }
            KBPurchaseQuery(productType, details)
        }
    }

    // -----------------
    // Buy
    // -----------------

    suspend fun purchase(
        activity: Activity,
        product: Product,
        details: WrappedProductDetails,
        offerToken: String?,
        connectionState: ConnectionState
    ): IKBPurchaseResult {

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams
                .newBuilder()
                .setProductDetails(details.wrapped)
                .apply {
                    offerToken?.let {
                        setOfferToken(offerToken)
                    }
                }
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val purchaseDetailsResult = suspendCoroutine { cont ->
            purchaseCallback = PurchasesUpdatedListener { result, purchases ->
                when (result.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        KotBilling.logger?.invoke(
                            Log.DEBUG,
                            "[KotBilling::purchase] Purchase - PURCHASE SUCCESS | product = $product | purchases = ${purchases?.size}",
                            null
                        )
                        val purchaseDetails =
                            purchases?.map { WrappedPurchaseDetails(it) } ?: emptyList()
                        val purchaseDetailsResult =
                            KBPurchaseDetails(product, details, purchaseDetails)
                        cont.resumeWith(Result.success(purchaseDetailsResult))
                    }
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                        KotBilling.logger?.invoke(
                            Log.DEBUG,
                            "[KotBilling::purchase] Purchase - ALREADY OWNED | product = $product | purchases = ${purchases?.size}",
                            null
                        )
                        val purchaseDetails =
                            purchases?.map { WrappedPurchaseDetails(it) } ?: emptyList()
                        val purchaseDetailsResult =
                            KBPurchaseDetails(product, details, purchaseDetails)
                        cont.resumeWith(Result.success(purchaseDetailsResult))
                    }
                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        val error = KBError(
                            KBError.ErrorType.PurchaseCancelled(product, details),
                            connectionState
                        )
                        KotBilling.logger?.invoke(
                            Log.ERROR,
                            "[KotBilling::purchase] Purchase cancelled by user! | error = $error}",
                            null
                        )
                        cont.resumeWith(Result.success(error))
                    }
                    else -> {
                        // Handle any other error codes.
                        val error = KBError(
                            KBError.ErrorType.PurchaseFailed(product, details, result),
                            connectionState
                        )
                        KotBilling.logger?.invoke(
                            Log.ERROR,
                            "[KotBilling::purchase] Purchase failed by user! | error = $error}",
                            null
                        )
                        cont.resumeWith(Result.success(error))
                    }
                }
                purchaseCallback = null
            }

            val billingResult = client.launchBillingFlow(activity, billingFlowParams)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // we wait for purchaseCallback - it must be called soon
                KotBilling.logger?.invoke(
                    Log.DEBUG,
                    "[KotBilling::purchase] Waiting for purchase callback to retrieve result form PlayStore... | product = $product",
                    null
                )
            } else {
                // reset callback and handle error here...
                purchaseCallback = null
                val error = KBError(
                    KBError.ErrorType.PurchaseFailed(product, details, billingResult),
                    connectionState
                )
                KotBilling.logger?.invoke(
                    Log.ERROR,
                    "[KotBilling::purchase] launchBillingFlow failed! | error = $error}",
                    null
                )
                cont.resumeWith(Result.success(error))
            }
        }

        // finally acknowledge or consume the purchase
        return when (purchaseDetailsResult) {
            is KBError -> purchaseDetailsResult
            is KBPurchaseDetails -> {
                // consume / acknowledge all purchases
                val purchases = purchaseDetailsResult.purchaseDetails
                if (product.consumable) {
                    val results = purchases.map { consumePurchase(product, it, connectionState) }
                    // result should contain 1 entry that just got consumed...
                    val successfullyConsumed = results.count { it is KBConsumed } == 1
                    if (successfullyConsumed) {
                        KBPurchase(purchaseDetailsResult)
                    } else {
                        val error = KBError(
                            KBError.ErrorType.ConsumesFailed(product, details, results),
                            connectionState
                        )
                        KotBilling.logger?.invoke(
                            Log.ERROR,
                            "[KotBilling::purchase] Consume failed! | error = $error}",
                            null
                        )
                        error
                    }
                } else {
                    val results =
                        purchases.map { acknowledgePurchase(product, it, connectionState) }
                    // result should only contain acknowledged entries
                    // type... could be SuccessfullyAcknowledges or AlreadyAcknowledged => we handle both the same way => as success
                    val allSuccessfullyAcknowledged =
                        results.count { it is KBAcknowledgeResult } == results.size
                    if (allSuccessfullyAcknowledged) {
                        KBPurchase(purchaseDetailsResult)
                    } else {
                        val error = KBError(
                            KBError.ErrorType.AcknowledgesFailed(product, details, results),
                            connectionState
                        )
                        KotBilling.logger?.invoke(
                            Log.ERROR,
                            "[KotBilling::purchase] Acknowledge failed! | error = $error}",
                            null
                        )
                        error
                    }
                }
            }
        }
    }

    private suspend fun acknowledgePurchase(
        product: Product,
        details: WrappedPurchaseDetails,
        connectionState: ConnectionState
    ): IKBAcknowledgeResult {
        return if (details.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!details.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(details.purchaseToken)
                    .build()
                val result = client.acknowledgePurchase(acknowledgePurchaseParams)
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    KBAcknowledgeResult(KBAcknowledgeResult.Type.SuccessfullyAcknowledges, details)
                } else {
                    KBError(
                        KBError.ErrorType.AcknowledgeFailed(product, details, result),
                        connectionState
                    )
                }
            } else {
                KBAcknowledgeResult(KBAcknowledgeResult.Type.AlreadyAcknowledged, details)
            }
        } else {
            KBError(
                KBError.ErrorType.AcknowledgeFailedProductNotPurchased(product, details),
                connectionState
            )
        }
    }

    private suspend fun consumePurchase(
        product: Product,
        details: WrappedPurchaseDetails,
        connectionState: ConnectionState
    ): IKBConsumeResult {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(details.purchaseToken)
                .build()
        val result = client.consumePurchase(consumeParams)
        return if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            KBConsumed(details)
        } else {
            KBError(KBError.ErrorType.ConsumeFailed(product, details, result), connectionState)
        }
    }
}