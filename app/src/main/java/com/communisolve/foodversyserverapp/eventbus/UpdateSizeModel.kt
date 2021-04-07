package com.communisolve.foodversyserverapp.eventbus

import com.communisolve.foodversy.model.SizeModel

class UpdateSizeModel {
    var sizeModelList :List<SizeModel>? = null

    constructor()
    constructor(sizeModelList :List<SizeModel>?){
        this.sizeModelList  = sizeModelList
    }
}