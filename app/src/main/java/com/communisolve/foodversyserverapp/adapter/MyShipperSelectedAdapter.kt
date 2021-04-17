package com.communisolve.foodversyserverapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.callbacks.IRecyclerItemClickLitner
import com.communisolve.foodversyserverapp.databinding.LayoutShipperBinding
import com.communisolve.foodversyserverapp.databinding.LayoutShipperSelectedBinding
import com.communisolve.foodversyserverapp.model.ShipperUserModel

class MyShipperSelectedAdapter(
    var context: Context,
    var shipperList: List<ShipperUserModel>
) : RecyclerView.Adapter<MyShipperSelectedAdapter.ViewHolder>() {

    var lastCheckImageView:ImageView? = null
    var selectedShipper:ShipperUserModel ?=null
        private set

    lateinit var binding: LayoutShipperSelectedBinding

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var iRecyclerItemClickLitner:IRecyclerItemClickLitner?=null

        fun setClick(iRecyclerItemClickLitner: IRecyclerItemClickLitner){
            this.iRecyclerItemClickLitner = iRecyclerItemClickLitner
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            iRecyclerItemClickLitner!!.onItemClick(view!!,adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutShipperSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        binding.txtPhone.setText(shipperList[position].phone)
        binding.txtName.setText(shipperList[position].name)

        holder.setClick(object :IRecyclerItemClickLitner{
            override fun onItemClick(view: View, pos: Int) {
                if (lastCheckImageView !=null)
                    lastCheckImageView!!.setImageResource(0)
                binding.imgChecked.setImageResource(R.drawable.fui_ic_check_circle_black_128dp)
                lastCheckImageView =binding.imgChecked
                selectedShipper = shipperList[pos]
            }

        })
    }

    override fun getItemCount(): Int = shipperList.size
}