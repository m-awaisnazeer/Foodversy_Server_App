package com.communisolve.foodversyserverapp.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import com.communisolve.foodversyserverapp.model.CategoryModel
import com.communisolve.foodversyserverapp.model.FoodModel
import com.communisolve.foodversyserverapp.model.ServerUserModel

object Common {
    fun setSpanStringColor(welcome: String, name: String?, txtTime: TextView, parseColor: Int) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpanable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpanable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtSpanable.setSpan(
            ForegroundColorSpan(parseColor),
            0,
            name!!.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.append(txtSpanable)
        txtTime.setText(builder, TextView.BufferType.SPANNABLE)
    }

    fun convertStatustoString(orderStatus: Int): String? =
        when (orderStatus) {
            0 -> "Placed"
            1 -> "Shipping"
            2 -> "Shipped"
            -1 -> "Cancelled"
            else -> "Error"
        }


    val ORDER_REF: String="Orders"
    var foodSelected: FoodModel? = null
    var categorySelected: CategoryModel? = null
    val CATEGORY_REF: String = "Category"
    val SERVER_REF = "Server"
    var currentServerUser: ServerUserModel? = null

    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0
}