package com.communisolve.foodversyserverapp.callbacks

import com.communisolve.foodversyserverapp.model.OrderModel

interface IOnOrderItemMenuClickListener {
    fun onEditSelectionCliclListener(position:Int,orderModel: OrderModel)
    fun onRemoveSelectionCliclListener(position:Int,orderModel: OrderModel)
    fun onCallSelectionCliclListener(position:Int,orderModel: OrderModel)
    fun onDirectionSelectionCliclListener(position:Int,orderModel: OrderModel)

}