---
icon: material/keyboard
---

#### Quering available product(s)

```kotlin

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

```

#### Quering purchase(s)

```kotlin

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

```

#### Purchasing a product

```kotlin

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