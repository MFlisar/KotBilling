package com.michaelflisar.kotbilling.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val type: ProductType,
    val consumable: Boolean = false
) : Parcelable