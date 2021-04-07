package com.communisolve.foodversyserverapp.eventbus

import com.communisolve.foodversy.model.AddOnModel
import com.communisolve.foodversy.model.SizeModel

class UpdateAddonModel {
    var addonModelList :List<AddOnModel>? = null

    constructor()
    constructor(addonModelList :List<AddOnModel>?){
        this.addonModelList  = addonModelList
    }
}