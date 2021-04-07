package com.communisolve.foodversyserverapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.callbacks.IOnOrderItemMenuClickListener
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.databinding.LayoutOrderItemBinding
import com.communisolve.foodversyserverapp.model.OrderModel
import java.text.SimpleDateFormat

class MyOrderAdapter(
    var context: Context,
    var orderList: MutableList<OrderModel>,
    var iOnOrderItemMenuClickListener: IOnOrderItemMenuClickListener
) : RecyclerView.Adapter<MyOrderAdapter.ViewHolder>() {
    private lateinit var binding: LayoutOrderItemBinding
    lateinit var simpleDateFormat: SimpleDateFormat

    init {
        simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(orderList!!.get(position)!!.cartItemList!![0]!!.foodImage)
            .into(binding.foodImage)
        binding.txtOrderNumber.setText(orderList[position].key)
        Common.setSpanStringColor(
            "Order date ", simpleDateFormat.format(orderList[position].createDate),
            binding.txtTime, Color.parseColor("#333639")
        )

        Common.setSpanStringColor(
            "Order State ", Common.convertStatustoString(orderList[position].orderStatus),
            binding.txtOrderStatus, Color.parseColor("#005758")
        )

        Common.setSpanStringColor(
            "Num of Items ", (if (orderList[position].cartItemList == null) "0"
            else orderList[position].cartItemList!!.size).toString(),
            binding.txtNumberOfItems, Color.parseColor("#00574B")
        )

        Common.setSpanStringColor(
            "Name ", orderList[position].userName,
            binding.txtName, Color.parseColor("#006061")
        )

        binding.moreMenuOptions.setOnClickListener {
            showPopupMenu(it, position, orderList[position])
        }
    }


    override fun getItemCount(): Int = orderList.size


    private fun showPopupMenu(view: View?, position: Int, orderModel: OrderModel) {
        var popupmenu: androidx.appcompat.widget.PopupMenu = androidx.appcompat
            .widget.PopupMenu(view!!.context, view!!)
        popupmenu.inflate(R.menu.order_item_menu)
        popupmenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_edit -> {
                    iOnOrderItemMenuClickListener.onEditSelectionCliclListener(position, orderModel)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_remove -> {
                    iOnOrderItemMenuClickListener.onRemoveSelectionCliclListener(
                        position,
                        orderModel
                    )
                    return@setOnMenuItemClickListener true
                }
                R.id.action_call -> {
                    iOnOrderItemMenuClickListener.onCallSelectionCliclListener(position, orderModel)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_direction -> {
                    iOnOrderItemMenuClickListener.onDirectionSelectionCliclListener(
                        position,
                        orderModel
                    )
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
        popupmenu.show()

    }

    fun removeItemAt(position: Int) {
//        orderList.removeAt(position)
        notifyItemRemoved(position)
    }

}