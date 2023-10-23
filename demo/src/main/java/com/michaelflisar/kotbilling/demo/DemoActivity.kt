package com.michaelflisar.kotbilling.demo

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.michaelflisar.kotbilling.KotBilling
import com.michaelflisar.kotbilling.classes.ProductType
import com.michaelflisar.kotbilling.demo.databinding.ActivityDemoBinding
import com.michaelflisar.kotbilling.results.KBError
import com.michaelflisar.kotbilling.results.KBProductDetailsList
import com.michaelflisar.kotbilling.results.KBPurchase
import com.michaelflisar.kotbilling.results.KBPurchaseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btAction.setOnClickListener {
            showActionMenu(it)
        }

        lifecycleScope.launch(Dispatchers.IO) {

            addInfo("Quering prodcuts...")

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
                    addInfo("Query Products - Error", listOf(connectionState.toString(), errorType.toString()))
                }
                is KBProductDetailsList -> {
                    if (result.details.isEmpty()) {
                        addInfo("Query Products - Result", listOf("No products found!"))
                    } else {
                        result.details.forEach {
                            val product = it.product
                            val details = it.details
                            addInfo("Product $product", listOf(details.toString()))
                        }
                    }
                }
            }

        }

        lifecycleScope.launch(Dispatchers.IO) {

            addInfo("Quering purchases...")

            val result = KotBilling.queryPurchases(ProductType.InApp)
            // or subscriptions
            // val result = KotBilling.queryPurchases(ProductType.Subscription)

            when (result) {
                is KBError -> {
                    val connectionState = result.connectionState
                    val errorType = result.errorType
                    //when (errorType) {
                    //    // handle all possible error cases differently if needed
                    //}
                    addInfo("Query Purchases - Error", listOf(connectionState.toString(), errorType.toString()))
                }
                is KBPurchaseQuery -> {
                    if (result.details.isEmpty()) {
                        addInfo("Query Purchases", listOf("No purchases found!"))
                    } else {
                        val productType = result.productType
                        result.details.forEach {
                            addInfo("Query Purchases - Purchase", listOf(it.toString()))
                        }
                    }
                }
            }
        }
    }

    // --------------
    // functions
    // --------------

    private fun showActionMenu(view: View) {
        val popupMenu = popupMenu {
            dropdownGravity = Gravity.BOTTOM
            section {
                title = "QUERY - One Time Purchases"
                item {
                    label = "Test Purchased"
                    callback = {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val result =
                                KotBilling.queryProducts(listOf(DemoBillingSetup.PRODUCT_NOT_CONSUMABLE))
                            val title = label.toString()
                            val info = result.toString()
                            addInfo(title, listOf(info))
                        }
                    }
                }
            }
            section {
                title = "PURCHASE - One Time Purchases"
                item {
                    label = "Test Purchase"
                    callback = {
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
                            val title = label.toString()
                            val info = result.toString()
                            addInfo(title, listOf(info))
                        }
                    }
                }
            }
        }
        popupMenu.show(this, view)
    }

    // --------------
    // helper classes / functions (info related)
    // --------------

    class Info(
        val title: String,
        val infos: List<String>
    )

    private val infoData = ArrayList<Info>()

    private suspend fun addInfo(title: String, infos: List<String> = emptyList()) {

        val sb = SpannableStringBuilder()
        infoData.add(Info(title, infos))

        withContext(Dispatchers.IO) {
            infoData.forEachIndexed { index, info ->

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

                // 3) append an extra empty line
                sb.append("\n")
            }
        }

        withContext(Dispatchers.Main) {
            binding.tvInfos.text = sb.toString()
        }
    }
}