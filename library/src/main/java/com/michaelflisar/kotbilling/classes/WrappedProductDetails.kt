package com.michaelflisar.kotbilling.classes

import android.os.Parcelable
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import kotlinx.parcelize.Parcelize

data class WrappedProductDetails(
    internal val wrapped: ProductDetails,
    val productId: String,
    val productType: String,
    val title: String,
    val name: String,
    val description: String,
    val oneTimePurchaseDetails: OneTimePurchaseDetails?,
    val subscriptionDetails: List<SubscriptionDetails>?
) {

    override fun toString(): String {
        return "WrappedProductDetails(productId=$productId,productType=$productType,title=$title,name=$name,description=$description,oneTimePurchaseDetails=$oneTimePurchaseDetails,subscriptionDetails=${subscriptionDetails?.joinToString(",")})"
    }

    constructor(details: ProductDetails) : this(
        details,
        details.productId,
        details.productType,
        details.title,
        details.name,
        details.description,
        details.oneTimePurchaseOfferDetails?.let { OneTimePurchaseDetails(it) },
        details.subscriptionOfferDetails?.let { it.map { SubscriptionDetails(it) } }
    )

    // works if there are not more than one offer for the base plan... good enough for now
    val singlePrice = if (productType == BillingClient.ProductType.INAPP) {
        oneTimePurchaseDetails?.formattedPrice
    } else subscriptionDetails?.firstOrNull()?.pricingPhases?.firstOrNull()?.formattedPrice

    @Parcelize
    data class OneTimePurchaseDetails(
        val formattedPrice: String,
        val priceAmountMicros: Long,
        val priceCurrencyCode: String
    ) : Parcelable {
        constructor(details: ProductDetails.OneTimePurchaseOfferDetails) : this(
            details.formattedPrice,
            details.priceAmountMicros,
            details.priceCurrencyCode
        )
    }

    @Parcelize
    data class SubscriptionDetails(
        val basePlanId: String,
        val pricingPhases: List<PriceInfo>,
        val offerToken: String,
        val offerId: String?
    ) : Parcelable {
        constructor(details: ProductDetails.SubscriptionOfferDetails) : this(
            details.basePlanId,
            details.pricingPhases.pricingPhaseList.map { PriceInfo(it) },
            details.offerToken,
            details.offerId
        )
    }

    @Parcelize
    data class PriceInfo(
        val priceCurrencyCode: String,
        val priceAmountMicros: Long,
        val formattedPrice: String,
        val billingPeriod: String,
        val billingCycleCount: Int,
        val recurrenceMode: Int
    ) : Parcelable {
        constructor(pricingPhase: ProductDetails.PricingPhase) : this(
            pricingPhase.priceCurrencyCode,
            pricingPhase.priceAmountMicros,
            pricingPhase.formattedPrice,
            pricingPhase.billingPeriod,
            pricingPhase.billingCycleCount,
            pricingPhase.recurrenceMode
        )
    }
}