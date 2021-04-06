package com.communisolve.foodversyserverapp.callbacks

import com.communisolve.foodversyserverapp.model.CategoryModel


interface ICategoryCallBackListner {
    fun onCategoryLoadSuccess(categoryModels: List<CategoryModel>)
    fun onCategoryLoadFailed(message: String)
}
