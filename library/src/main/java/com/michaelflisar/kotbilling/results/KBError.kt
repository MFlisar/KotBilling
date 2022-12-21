package com.michaelflisar.kotbilling.results

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeResult
import com.michaelflisar.kotbilling.classes.*
import com.michaelflisar.kotbilling.logInfo

data class KBError(
    val errorType: ErrorType,
    val connectionState: ConnectionState,
) : IKBProductResult, IKBPurchaseDetailsResult, IKBPurchaseQueryResult, IKBAcknowledgeResult, IKBConsumeResult, IKBPurchaseResult {

    sealed class ErrorType {
        object Connection : ErrorType()
        data class QueryProductDetailsFailed(val products: List<Product>, val result: BillingResult) : ErrorType() {
            override fun toString(): String {
                return "QueryProductDetailsFailed(products=[${products.joinToString(",")}],result=${result.logInfo()})"
            }
        }
        data class QueryPurchasesFailed(val productType: ProductType, val result: BillingResult) : ErrorType(){
            override fun toString(): String {
                return "QueryPurchasesFailed(productType=$productType,result=${result.logInfo()}"
            }
        }
        data class PurchaseCancelled(val product: Product, val details: WrappedProductDetails): ErrorType()
        data class PurchaseFailed(val product: Product, val details: WrappedProductDetails, val result: BillingResult): ErrorType() {
            override fun toString(): String {
                return "PurchaseFailed(product=$product,details=$details,result=${result.logInfo()}"
            }
        }
        data class AcknowledgeFailedProductNotPurchased(val product: Product, val details: WrappedPurchaseDetails): ErrorType()
        data class AcknowledgeFailed(val product: Product, val details: WrappedPurchaseDetails, val result: BillingResult): ErrorType() {
            override fun toString(): String {
                return "AcknowledgeFailed(product=$product,details=$details,result=${result.logInfo()}"
            }
        }
        data class ConsumeFailed(val product: Product, val details: WrappedPurchaseDetails, val result: ConsumeResult): ErrorType()
        data class ConsumesFailed(val product: Product, val details: WrappedProductDetails, val results: List<IKBConsumeResult>): ErrorType()
        data class AcknowledgesFailed(val product: Product, val details: WrappedProductDetails, val results: List<IKBAcknowledgeResult>): ErrorType()
        data class ProductDetailsNotFound(val product: Product):  ErrorType()
    }
}