package com.communisolve.foodversyserverapp.callbacks

import com.communisolve.foodversyserverapp.model.FoodModel

interface IOnFoodsListItemMenuClickListner {
    fun onUpdateItemCLickListner(position:Int,foodModel: FoodModel)
    fun onDeleteItemCLickListner(position:Int,foodModel: FoodModel)

}