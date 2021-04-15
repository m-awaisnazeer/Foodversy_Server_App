package com.communisolve.foodversyserverapp.callbacks

import com.communisolve.foodversyserverapp.model.CategoryModel
import com.communisolve.foodversyserverapp.model.ShipperUserModel

interface IShipperLoadCallbackListner {
    fun onShippersLoadSuccess(shipperUsersList: List<ShipperUserModel>)
    fun onShippersLoadFailed(message: String)

}
