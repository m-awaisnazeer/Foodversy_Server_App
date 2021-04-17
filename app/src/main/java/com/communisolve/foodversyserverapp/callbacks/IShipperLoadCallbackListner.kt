package com.communisolve.foodversyserverapp.callbacks

import android.app.AlertDialog
import android.widget.Button
import android.widget.RadioButton
import com.communisolve.foodversyserverapp.model.OrderModel
import com.communisolve.foodversyserverapp.model.ShipperUserModel

interface IShipperLoadCallbackListner {
    fun onShippersLoadSuccess(shipperUsersList: List<ShipperUserModel>)
    fun onShipperLoadSuccess(
        pos: Int, orderModel: OrderModel?,
        shipperUsersList: List<ShipperUserModel>?,
        dialog: AlertDialog?,
        ok: Button?, cancel: Button?,
        rdi_shipping: RadioButton,
        rdi_shipped: RadioButton,
        rdi_canceled: RadioButton,
        rdi_delete: RadioButton,
        rdi_restore: RadioButton?
    )

    fun onShippersLoadFailed(message: String)

}
