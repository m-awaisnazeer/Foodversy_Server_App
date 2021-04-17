package com.communisolve.foodversyserverapp.ui.shipper

import android.app.AlertDialog
import android.widget.Button
import android.widget.RadioButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.communisolve.foodversyserverapp.callbacks.IShipperLoadCallbackListner
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.model.CategoryModel
import com.communisolve.foodversyserverapp.model.OrderModel
import com.communisolve.foodversyserverapp.model.ShipperUserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShipperViewModel : ViewModel(), IShipperLoadCallbackListner {

    private var shippersListMutableLiveData: MutableLiveData<List<ShipperUserModel>>? = null
    private var messageError: MutableLiveData<String>? = null
    private val iShipperLoadCallbackListner: IShipperLoadCallbackListner


    init {
        iShipperLoadCallbackListner = this
    }
    override fun onShippersLoadSuccess(shipperUsersList: List<ShipperUserModel>) {
        shippersListMutableLiveData!!.value = shipperUsersList
    }

    override fun onShipperLoadSuccess(
        pos: Int,
        orderModel: OrderModel?,
        shipperUsersList: List<ShipperUserModel>?,
        dialog: AlertDialog?,
        ok: Button?,
        cancel: Button?,
        rdi_shipping: RadioButton,
        rdi_shipped: RadioButton,
        rdi_canceled: RadioButton,
        rdi_delete: RadioButton,
        rdi_restore: RadioButton?
    ) {
        // do nothing
    }


    fun getShippersList(): MutableLiveData<List<ShipperUserModel>> {
        if (shippersListMutableLiveData == null) {
            shippersListMutableLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadShippers()

        }
        return shippersListMutableLiveData!!
    }

    fun loadShippers() {
        val templist = ArrayList<ShipperUserModel>()
        val categoryRef = FirebaseDatabase.getInstance().getReference(Common.SHIPPERS_REF)
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemsnapshot in snapshot.children) {
                    val shipperUserModel = itemsnapshot.getValue(ShipperUserModel::class.java)
                    shipperUserModel!!.key = itemsnapshot.key!!
                    templist.add(shipperUserModel!!)
                }
                iShipperLoadCallbackListner.onShippersLoadSuccess(templist)
            }

            override fun onCancelled(error: DatabaseError) {
                iShipperLoadCallbackListner.onShippersLoadFailed(error.message)
            }

        })

    }


    override fun onShippersLoadFailed(message: String) {
        messageError!!.value = message
    }


}