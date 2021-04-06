package com.communisolve.foodversyserverapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.communisolve.foodversyserverapp.callbacks.IRecyclerItemClickLitner
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.databinding.LayoutFoodItemBinding
import com.communisolve.foodversyserverapp.model.FoodModel


class MyFoodListAdapter(
    internal var context: Context,
    internal var foodsList: List<FoodModel>

) : RecyclerView.Adapter<MyFoodListAdapter.ViewHolder>() {



    var binding: LayoutFoodItemBinding? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var listner: IRecyclerItemClickLitner? = null

        fun setListner(listner: IRecyclerItemClickLitner) {
            this.listner = listner
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listner!!.onItemClick(view!!, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            LayoutFoodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentFoodModel = foodsList.get(position)
        Glide.with(context).load(foodsList.get(position).image).into(binding!!.imgFoodList)
        binding!!.txtFoodName.setText(foodsList.get(position).name)
        binding!!.txtFoodPrice.setText("$${foodsList.get(position).price.toString()}")


        holder.setListner(object : IRecyclerItemClickLitner {
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected = foodsList.get(pos)
                Common.foodSelected!!.key = pos.toString()
                //EventBus.getDefault().postSticky(FoodItemClick(true, foodsList.get(pos)))
            }

        })

    }



    override fun getItemCount(): Int {
        return foodsList.size
    }
}