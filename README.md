### About

[![Release](https://jitpack.io/v/MFlisar/KotBilling.svg)](https://jitpack.io/#MFlisar/KotBilling)
![License](https://img.shields.io/github/license/MFlisar/KotBilling)

A kotlin coroutine based solution for handling in app purchases.

### Overview

* exception save, each functions returns a non null sealed class / interface instance
* simple query function to query a product state: `val result = KotBilling.queryProducts(... products ...)`
* simple purchase function to buy a product and acknowledge/consume it: `val result = KotBilling.purchase(actvity, product, offerToken)`

### Dependencies

| Dependency | Version |
|:-|-:|
| [Billing](com.android.billingclient:billing-ktx) | `6.0.1` |

### Gradle (via [JitPack.io](https://jitpack.io/))

1) Add jitpack to your project's build.gradle:

```groovy
repositories {
	maven { url "https://jitpack.io" }
}
```

2) Add the compile statement to your module's build.gradle:
3) 
```groovy
dependencies {
	implementation 'com.github.MFlisar:KotBilling:<LAST VERSION>'
}
```

The latest release can be found [here](https://github.com/MFlisar/KotBilling/releases/latest)

### Example

It works as simple as following:

```kotlin

// --------------------
// Quering available product(s)
// --------------------

lifecycleScope.launch(Dispatchers.IO) {

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

lifecycleScope.launch(Dispatchers.IO) {
    
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

lifecycleScope.launch(Dispatchers.IO) {
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

For a full example, check out the demo app. But be aware, the demo app is not deployed on google play so it won't return any results - still it shows how to use the library.
