package com.communisolve.foodversyserverapp.callbacks

import com.communisolve.foodversyserverapp.model.CategoryModel

interface IOnCategoriesItemMenuClickListner {
    fun onUpdateItemCLickListner(pos:Int,categoryModel: CategoryModel)
}