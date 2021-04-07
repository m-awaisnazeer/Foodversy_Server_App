package com.communisolve.foodversyserverapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.foodversy.model.AddOnModel
import com.communisolve.foodversy.model.SizeModel
import com.communisolve.foodversyserverapp.callbacks.IRecyclerItemClickLitner
import com.communisolve.foodversyserverapp.databinding.LayoutAddonSizeItemBinding
import com.communisolve.foodversyserverapp.eventbus.SelectAddonModel
import com.communisolve.foodversyserverapp.eventbus.SelectSizeModel
import com.communisolve.foodversyserverapp.eventbus.UpdateAddonModel
import com.communisolve.foodversyserverapp.eventbus.UpdateSizeModel
import org.greenrobot.eventbus.EventBus

class MyAddOnAdapter(var context: Context, var addOnModelList: MutableList<AddOnModel>) :
    RecyclerView.Adapter<MyAddOnAdapter.ViewHolder>() {
    lateinit var binding: LayoutAddonSizeItemBinding
    var editPos: Int
    var updateAddonModel: UpdateAddonModel

    init {
        editPos = -1
        updateAddonModel = UpdateAddonModel()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var listener: IRecyclerItemClickLitner? = null

        fun setListner(listener: IRecyclerItemClickLitner?) {
            this.listener = listener
        }

        init {
            itemView.setOnClickListener { view->
                listener!!.onItemClick(view,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            LayoutAddonSizeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding.txtName.text = addOnModelList.get(position).name
        binding.txtPrice.text = addOnModelList.get(position).price.toString()

        binding.imgDelete.setOnClickListener {
            addOnModelList.removeAt(position)
            notifyItemRemoved(position)
            updateAddonModel.addonModelList = addOnModelList
            EventBus.getDefault().postSticky(updateAddonModel)
        }

        holder.setListner(object :IRecyclerItemClickLitner{
            override fun onItemClick(view: View, pos: Int) {
                editPos = pos
                EventBus.getDefault().postSticky(SelectAddonModel(addOnModelList[pos]))
            }

        })
    }

    override fun getItemCount(): Int = addOnModelList.size


    fun addNewAddOn(addOnModel: AddOnModel) {
        addOnModelList.add(addOnModel)
        notifyItemInserted(addOnModelList.size-1)
        updateAddonModel.addonModelList = addOnModelList
        EventBus.getDefault().postSticky(updateAddonModel)
    }

    fun editAddOn(addOnModel: AddOnModel) {
        addOnModelList.set(editPos,addOnModel)
        notifyItemChanged(editPos)
        updateAddonModel.addonModelList = addOnModelList
        EventBus.getDefault().postSticky(updateAddonModel)

    }

}
