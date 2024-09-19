[![Maven](https://img.shields.io/maven-central/v/io.github.mflisar.kotbilling/core?style=for-the-badge&color=blue)](https://central.sonatype.com/namespace/io.github.mflisar.kotbilling)
[![API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/github/languages/top/mflisar/kotbilling.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![KMP](https://img.shields.io/badge/Kotlin_Multiplatform-blue?style=for-the-badge&label=Kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/github/license/MFlisar/KotBilling?style=for-the-badge)](LICENSE)

<h1 align="center">KotBilling</h1>

A kotlin coroutine based solution for handling in app purchases for **billing library version 7**.

## :heavy_check_mark: Features

* exception save, each functions returns a non null sealed class / interface instance
* simple query function to query a product state: `val result = KotBilling.queryProducts(... products ...)`
* simple purchase function to buy a product and acknowledge/consume it: `val result = KotBilling.purchase(actvity, product, offerToken)`

## :camera: Screenshots

--

## :link: Dependencies

| Dependency                                                                                                                    | Version |
|:------------------------------------------------------------------------------------------------------------------------------|--------:|
| [com.android.billingclient:billing-ktx](https://mvnrepository.com/artifact/com.android.billingclient/billing-ktx?repo=google) | `7.0.0` |

## :elephant: Gradle

This library is distributed via [maven central](https://central.sonatype.com/).

*build.gradle.kts*

```kts
val kotbilling = "<LATEST-VERSION>"

implementation("io.github.mflisar.kotbilling:library:$kotbilling")
```

## </> Basic Usage

It works as simple as following:

```kotlin

// --------------------
// Quering available product(s)
// --------------------

scope.launch(Dispatchers.IO) {

    val result = KotBilling.queryProducts(
        listOf(/* products to query */)
    )
    when (result) {
        is KBError -> {
            val connectionState = result.connectionState
            val errorType = result.errorType
            // errorType is a sealed class which will tell you the reason and type for the error (connection error, purchase error, acknowledge error, ...)
            // connectionState will tell you what the connection state was
        }
        is KBProductDetailsList -> {
            if (result.details.isEmpty()) {
                // most probably only happens if you try this inside a debug app or an app that's not released on the playstore yet
            } else {
                // in all other cases you will get a list with products and all their details (same size as the queried products) which you can handle here
                result.details.forEach {
                    val product = it.product
                    val details = it.details
                    // ... 
                }
            }
        }
    }
}

// --------------------
// Quering purchase(s)
// --------------------

scope.launch(Dispatchers.IO) {
    
    val result = KotBilling.queryPurchases(ProductType.InApp) // or ProductType.Subscription
    when (result) {
        is KBError -> {
            val connectionState = result.connectionState
            val errorType = result.errorType
            // errorType is a sealed class which will tell you the reason and type for the error (connection error, purchase error, acknowledge error, ...)
            // connectionState will tell you what the connection state was
        }
        is KBPurchaseQuery -> {
            if (result.details.isEmpty()) {
                // user did not purchase anything yet
            } else {
                val productType = result.productType
                val details = result.details
                // details holds a list of all purchase details which you can handle here
            }
        }
    }
}

// --------------------
// Purchasing a product
// --------------------

scope.launch(Dispatchers.IO) {
    val result = KotBilling.purchase(
        context,
        /* product */,
        /* optional offerToken */
    )
    when (result) {
        is KBError -> {
            val connectionState = result.connectionState
            val errorType = result.errorType
            // errorType is a sealed class which will tell you the reason and type for the error (connection error, purchase error, acknowledge error, ...)
            // connectionState will tell you what the connection state was
        }
        is KBPurchase -> {
            // success
            val purchase = result.purchase
            // purchase holds the purchase details which you can handle here
        }
    }
}

```

## :tada: Demo

A full [demo](demo) is included inside the demo module, it shows nearly every usage with working examples.

> [!IMPORTANT]
> But be aware, the demo app is not deployed on google play so it won't return any results - still it shows how to use the library.