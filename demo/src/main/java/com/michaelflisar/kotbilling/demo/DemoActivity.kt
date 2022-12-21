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
import com.michaelflisar.kotbilling.demo.databinding.ActivityDemoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DemoActivity : AppCompatActivity() {

    lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btAction.setOnClickListener {
            showActionMenu(it)
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
                            val result = KotBilling.queryProducts(listOf(DemoBillingSetup.PRODUCT_NOT_CONSUMABLE))
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
                            val result = KotBilling.purchase(this@DemoActivity, DemoBillingSetup.PRODUCT_NOT_CONSUMABLE, null)
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

    private suspend fun addInfo(title: String, infos: List<String>) {

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