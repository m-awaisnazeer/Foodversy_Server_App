package com.communisolve.foodversyserverapp.callbacks

import com.communisolve.foodversyserverapp.model.CategoryModel
import com.communisolve.foodversyserverapp.model.OrderModel

interface IOrderCallbackListner {
    fun onOrderLoadSuccess(ordersList: List<OrderModel>)
    fun onOrderLoadFailed(message: String)

}
