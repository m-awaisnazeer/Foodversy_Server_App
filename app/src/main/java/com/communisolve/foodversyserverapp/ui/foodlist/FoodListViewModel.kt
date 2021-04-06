package com.communisolve.foodversyserverapp.ui.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.model.FoodModel

class FoodListViewModel : ViewModel() {

    private var mutablefoodListData: MutableLiveData<List<FoodModel>>? = null

    fun getMutavleFoodliveData(): MutableLiveData<List<FoodModel>> {
        if (mutablefoodListData == null) {
            mutablefoodListData = MutableLiveData()
            mutablefoodListData!!.value = Common.categorySelected!!.foods

        }
        return mutablefoodListData!!
    }
}