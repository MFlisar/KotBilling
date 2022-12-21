### KotBilling [![Release](https://jitpack.io/v/MFlisar/KotBilling.svg)](https://jitpack.io/#MFlisar/KotBilling)

A kotlin coroutine based solution for handling in app purchases.

### Features

* exception save, each functions returns a non null sealed class / interface instance
* simple query function to query a product state: `val result = KotBilling.queryProducts(... products ...)`
* simple purchase function to buy a product and acknowledge/consume it: `val result = KotBilling.purchase(actvity, product, offerToken)`

### Gradle (via JitPack.io)

1) Add jitpack to your project's build.gradle:
```
repositories {
	maven { url "https://jitpack.io" }
}
```

2) Add the compile statement to your module's build.gradle:
```
dependencies {
	implementation 'com.github.MFlisar:KotBilling:<LAST VERSION>'
}
```

### How to start

For a full example, check out the demo app
