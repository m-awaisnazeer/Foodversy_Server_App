package com.communisolve.foodversyserverapp.model

class CategoryModel(
    var menu_id : String="",
    var name:String="",
    var image:String="",
    var foods:List<FoodModel>?=null
) {
}