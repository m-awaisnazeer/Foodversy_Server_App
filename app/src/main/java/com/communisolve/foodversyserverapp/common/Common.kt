package com.communisolve.foodversyserverapp.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.model.CategoryModel
import com.communisolve.foodversyserverapp.model.FoodModel
import com.communisolve.foodversyserverapp.model.ServerUserModel
import com.communisolve.foodversyserverapp.model.TokenModel
import com.google.firebase.database.FirebaseDatabase

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

    fun getNewOrderTopic(): String? {
        return java.lang.StringBuilder("/topics/new_order").toString()
    }

    val NOTI_CONTENT: String?="content"
    val NOTI_TITLE: String?="title"
     val TOKEN_REF: String="Tokens"
    fun updateToken(context: Context, token: String) {
        if (currentServerUser!=null)
            FirebaseDatabase.getInstance().getReference(Common.TOKEN_REF)
            .child(Common.currentServerUser!!.uid)
            .setValue(TokenModel(currentServerUser!!.uid,token))
            .addOnFailureListener { Toast.makeText(context, "$token", Toast.LENGTH_SHORT).show() }
            .addOnSuccessListener {  }

    }

    fun showNotification(
        context: Context, id: Int, title: String?, content: String?,
        intent: Intent?
    ) {
        var pendingInent: PendingIntent? = null
        if (intent !=null)
            pendingInent = PendingIntent.getActivity(context,id,intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val NOTIFICATION_CHANNEL_ID = "com.communisolve.foodversyserverapp"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,"Foodversy_Server",
                NotificationManager.IMPORTANCE_DEFAULT)

            notificationChannel.description = "Foodversy_Server_App"
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0,1000,500,1000)

            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder  = NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)

        builder.setContentTitle(title).setContentText(content).setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.ic_baseline_restaurant_menu_24))
        if (pendingInent != null)
            builder.setContentIntent(pendingInent)

        val notification = builder.build()
        notificationManager.notify(id,notification)
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