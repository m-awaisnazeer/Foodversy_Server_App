package com.communisolve.foodversyserverapp.eventbus

import com.communisolve.foodversyserverapp.model.ShipperUserModel

class UpdateActiveEvent(var currentShipperUser: ShipperUserModel, var checked: Boolean) {

}
