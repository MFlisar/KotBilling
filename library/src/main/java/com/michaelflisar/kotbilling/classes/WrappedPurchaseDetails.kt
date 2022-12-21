package com.michaelflisar.kotbilling.classes

import android.os.Parcelable
import com.android.billingclient.api.AccountIdentifiers
import com.android.billingclient.api.Purchase
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class WrappedPurchaseDetails(
    val purchaseState: Int,
    val purchaseTime: Long,
    val purchaseToken: String,
    val products: List<String>,
    val accountIdentifiers: AccountIdentifier?,
    val developerPayload: String,
    val isAcknowledged: Boolean,
    val isAutoRenewing: Boolean,
    val orderId: String,
    val quantity: Int,
    val signature: String
) : Parcelable {

    constructor(purchase: Purchase) : this(
        purchase.purchaseState,
        purchase.purchaseTime,
        purchase.purchaseToken,
        purchase.products,
        purchase.accountIdentifiers?.let { AccountIdentifier(it) },
        purchase.developerPayload,
        purchase.isAcknowledged,
        purchase.isAutoRenewing,
        purchase.orderId,
        purchase.quantity,
        purchase.signature
    )

    @IgnoredOnParcel
    val isPurchased = purchaseState == Purchase.PurchaseState.PURCHASED
    @IgnoredOnParcel
    val isOwned = isPurchased && isAcknowledged

    @Parcelize
    data class AccountIdentifier(
        val obfuscatedAccountId: String?,
        val obfuscatedProfileId: String?
    ) : Parcelable {
        constructor(identifier: AccountIdentifiers) : this(
            identifier.obfuscatedAccountId,
            identifier.obfuscatedProfileId
        )
    }
}
