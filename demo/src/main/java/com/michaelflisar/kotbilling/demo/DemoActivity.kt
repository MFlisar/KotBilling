package com.michaelflisar.kotbilling.demo

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotbilling.KotBilling
import com.michaelflisar.kotbilling.classes.ProductType
import com.michaelflisar.kotbilling.results.KBError
import com.michaelflisar.kotbilling.results.KBProductDetailsList
import com.michaelflisar.kotbilling.results.KBPurchase
import com.michaelflisar.kotbilling.results.KBPurchaseQuery
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoAppThemeRegionDetailed
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoCollapsibleRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.rememberDemoExpandedRegions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DemoActivity : com.michaelflisar.toolbox.androiddemoapp.DemoActivity(
    scrollableContent = false
) {
    //override val initialExpandedRegions = listOf(1, 2)

    @Composable
    override fun ColumnScope.Content(
        themeState: ComposeTheme.State
    ) {
        val regionsState = rememberDemoExpandedRegions(listOf(1, 2))
        val infoData = remember { mutableStateListOf<Info>() }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {

                addInfo(infoData, "Quering prodcuts...")
                val result = KotBilling.queryProducts(
                    listOf(
                        DemoBillingSetup.PRODUCT_NOT_CONSUMABLE,
                        DemoBillingSetup.PRODUCT_CONSUMABLE
                    )
                )
                when (result) {
                    is KBError -> {
                        val connectionState = result.connectionState
                        val errorType = result.errorType
                        //when (errorType) {
                        //    // handle all possible error cases differently if needed
                        //}
                        addInfo(
                            infoData,
                            "Query Products - Error",
                            listOf(connectionState.toString(), errorType.toString())
                        )
                    }

                    is KBProductDetailsList -> {
                        if (result.details.isEmpty()) {
                            addInfo(
                                infoData,
                                "Query Products - Result",
                                listOf("No products found!")
                            )
                        } else {
                            result.details.forEach {
                                val product = it.product
                                val details = it.details
                                addInfo(infoData, "Product $product", listOf(details.toString()))
                            }
                        }
                    }
                }

                addInfo(infoData, "Quering purchases...")
                val result2 = KotBilling.queryPurchases(ProductType.InApp)
                // or subscriptions
                // val result = KotBilling.queryPurchases(ProductType.Subscription)
                when (result2) {
                    is KBError -> {
                        val connectionState = result2.connectionState
                        val errorType = result2.errorType
                        //when (errorType) {
                        //    // handle all possible error cases differently if needed
                        //}
                        addInfo(
                            infoData,
                            "Query Purchases - Error",
                            listOf(connectionState.toString(), errorType.toString())
                        )
                    }

                    is KBPurchaseQuery -> {
                        if (result2.details.isEmpty()) {
                            addInfo(infoData, "Query Purchases", listOf("No purchases found!"))
                        } else {
                            val productType = result2.productType
                            result2.details.forEach {
                                addInfo(
                                    infoData,
                                    "Query Purchases - Purchase",
                                    listOf(it.toString())
                                )
                            }
                        }
                    }
                }

            }
        }

        DemoAppThemeRegionDetailed(
            state = regionsState
        )

        DemoCollapsibleRegion(
            regionId = 1,
            title = "Billing Example",
            state = regionsState
        ) {
            // Menu
            Button(
                onClick = {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val result =
                            KotBilling.queryProducts(listOf(DemoBillingSetup.PRODUCT_NOT_CONSUMABLE))
                        val title = "Query Purchased"
                        val info = result.toString()
                        addInfo(infoData, title, listOf(info))
                    }
                }
            ) {
                Text("QUERY One Time Purchases")
            }
            Button(
                onClick = {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val result = KotBilling.purchase(
                            this@DemoActivity,
                            DemoBillingSetup.PRODUCT_NOT_CONSUMABLE,
                            null
                        )
                        when (result) {
                            is KBError -> {
                                // error
                            }

                            is KBPurchase -> {
                                // success
                                val purchase = result.purchase
                                // here you can get the purchase details and
                            }
                        }
                        val title = "Test Purchase"
                        val info = result.toString()
                        addInfo(infoData, title, listOf(info))
                    }
                }
            ) {
                Text("EXECUTE Purchase")
            }
        }

        DemoCollapsibleRegion(
            modifier = Modifier.weight(1f),
            title = "Infos",
            regionId = 2,
            state = regionsState
        ) {
            LazyColumn {
                items(infoData.size) {
                    infoData.forEachIndexed { index, info ->

                        val sb = SpannableStringBuilder()

                        // 1) append title
                        val titleInfo = "[$index] ${info.title}"
                        val spannable = SpannableString(titleInfo).apply {
                            setSpan(
                                ForegroundColorSpan(Color.RED),
                                0,
                                titleInfo.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                        sb.append(spannable)
                        sb.append("\n")

                        // 2) append all info lines
                        info.infos.forEach {
                            sb.append(it)
                            sb.append("\n")
                        }

                        Text(sb.toString())
                    }
                }
            }
        }
    }

    // --------------
    // helper classes / functions (info related)
    // --------------

    class Info(
        val title: String,
        val infos: List<String>
    )

    private fun addInfo(
        infoData: SnapshotStateList<Info>,
        title: String,
        infos: List<String> = emptyList()
    ) {
        infoData.add(Info(title, infos))
    }
}