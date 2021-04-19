package com.communisolve.foodversyserverapp.ui.orders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.communisolve.foodversyserverapp.callbacks.IOrderCallbackListner
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.collections.ArrayList

class OrdersViewModel : ViewModel(), IOrderCallbackListner {

    private val ordersList = MutableLiveData<List<OrderModel>>()
    val messageError = MutableLiveData<String>()
    private val iOrderCallbackListner: IOrderCallbackListner

    init {
        iOrderCallbackListner = this
    }

    fun getOrderModelList(): MutableLiveData<List<OrderModel>> {
        loadOrder(0)
        return ordersList
    }

    fun loadOrder(status: Int) {
        val tempList: MutableList<OrderModel> = ArrayList()
        val orderRef = FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
            .orderByChild("orderStatus")
            .equalTo(status.toDouble())
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val orderModel = itemSnapshot.getValue(OrderModel::class.java)
                    orderModel!!.key = itemSnapshot.key!!
                    tempList.add(orderModel)
                }
                iOrderCallbackListner.onOrderLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                iOrderCallbackListner.onOrderLoadFailed(error.message)
            }

        })
    }
    

    override fun onOrderLoadSuccess(ordersList: List<OrderModel>) {
        Log.d("VMTAG", "onOrderLoadSuccess: ${ordersList.size}")
        if (ordersList.size >= 0) {
            Collections.sort(ordersList) { t1, t2 ->
                if (t1.createDate < t2.createDate) return@sort -1
                if (t1.createDate == t2.createDate) 0 else 1
            }

        }
        this.ordersList.value = ordersList
    }

    override fun onOrderLoadFailed(message: String) {
        messageError.value = message
    }

}