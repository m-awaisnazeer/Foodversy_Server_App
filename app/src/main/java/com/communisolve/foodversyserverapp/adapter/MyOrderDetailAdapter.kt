package com.communisolve.foodversyserverapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.communisolve.foodversyserverapp.databinding.LayoutOrderDetailItemBinding
import com.communisolve.foodversyserverapp.model.CartItem
import com.google.gson.Gson

class MyOrderDetailAdapter(
    internal var context: Context,
    internal var cartItemList: List<CartItem>
) : RecyclerView.Adapter<MyOrderDetailAdapter.ViewHolder>() {

    val gson: Gson = Gson()
    lateinit var binding: LayoutOrderDetailItemBinding

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            LayoutOrderDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(cartItemList[position].foodImage)
            .into(binding.foodImage)


        binding.txtFoodName.setText(
            StringBuilder("Name: ").append(cartItemList[position].foodName).toString()
        )
        binding.txtFoodQuantity.setText(
            StringBuilder("Quantity: ").append(cartItemList[position].foodQuantity).toString()
        )
        binding.txtSize.setText(
            StringBuilder("Size: ").append(cartItemList[position].foodSize).toString()
        )
//        val sizeModel: SizeModel = gson.fromJson(
//            cartItemList[position].foodSize,
//            object : TypeToken<SizeModel>() {}.type
//        )
//        if (sizeModel != null) {
//            binding.txtSize.setText(StringBuilder("Size: ").append(sizeModel.name))
//        }
        if (!cartItemList[position].foodAddon.equals("Default")) {
//            val addonModels: List<AddOnModel> = gson.fromJson(
//                cartItemList[position].foodAddon,
//                object : TypeToken<List<AddOnModel>>() {}.type
//            )
//
//            val addonString = StringBuilder()
//            if (addonModels != null) {
//                for (addonModel in addonModels) addonString.append(addonModel.name).append(",")
//                addonString.delete(addonString.length - 1, addonString.length)
//            }

            binding.txtFoodAddOn.setText(
                java.lang.StringBuilder("Addon: ").append(cartItemList[position].foodAddon)
                    .toString()
            )

        } else
            binding.txtFoodAddOn.setText(StringBuilder("Addon: Default"))

    }

    override fun getItemCount(): Int = cartItemList.size
}