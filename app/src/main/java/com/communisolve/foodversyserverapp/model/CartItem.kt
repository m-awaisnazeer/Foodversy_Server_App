package com.communisolve.foodversyserverapp.model

class CartItem(
   var foodId: String = "",
   var foodName: String = "",
   var foodImage: String = "",
   var foodPrice: Double = 0.0,
   var foodQuantity: Int = 0,
   var foodAddon: String = "",
   var foodSize: String = "",
   var userPhone: String = "",
   var foodExtraPrice: Double = 0.0,
   var uid: String = "",
) {}
