package com.communisolve.foodversyserverapp.model

import com.communisolve.foodversy.model.AddOnModel
import com.communisolve.foodversy.model.SizeModel

class FoodModel(
    var key: String="",
    var name: String="",
    var image:String="",
    var id:String="",
    var description:String="",
    var price:Int=0,
    var addon:List<AddOnModel>?=ArrayList<AddOnModel>(),
    var size:List<SizeModel> = ArrayList<SizeModel>()
) {
    var ratingValue:Double = 0.toDouble()
    var ratingCount:Long = 0.toLong()

     var userSelectedAddon:MutableList<AddOnModel>?=null
    var userSelectedSize:SizeModel?=null
}
