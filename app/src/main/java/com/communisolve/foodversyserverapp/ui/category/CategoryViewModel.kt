package com.communisolve.foodversyserverapp.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.communisolve.foodversyserverapp.callbacks.ICategoryCallBackListner
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryViewModel : ViewModel(), ICategoryCallBackListner {

    private var categoryListMutableLiveData: MutableLiveData<List<CategoryModel>>? = null
    private var messageError: MutableLiveData<String>? = null
    private val iCategoryCallBackListner: ICategoryCallBackListner



    init {
        iCategoryCallBackListner = this
    }

    override fun onCategoryLoadSuccess(categoryModels: List<CategoryModel>) {
        categoryListMutableLiveData!!.value = categoryModels
    }

    override fun onCategoryLoadFailed(message: String) {
        messageError!!.value = message
    }

    fun getError(): MutableLiveData<String> = messageError!!


    fun getCategoryList(): MutableLiveData<List<CategoryModel>> {
        if (categoryListMutableLiveData == null) {
            categoryListMutableLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadCategory()

        }
        return categoryListMutableLiveData!!
    }

     fun loadCategory() {
        val templist = ArrayList<CategoryModel>()
        val categoryRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemsnapshot in snapshot.children) {
                    val model = itemsnapshot.getValue(CategoryModel::class.java)
                    model!!.menu_id = itemsnapshot.key!!
                    templist.add(model!!)
                }
                iCategoryCallBackListner.onCategoryLoadSuccess(templist)
            }

            override fun onCancelled(error: DatabaseError) {
                iCategoryCallBackListner.onCategoryLoadFailed(error.message)
            }

        })

    }
}