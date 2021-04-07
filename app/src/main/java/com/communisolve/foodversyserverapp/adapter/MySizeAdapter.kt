package com.communisolve.foodversyserverapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.foodversy.model.SizeModel
import com.communisolve.foodversyserverapp.callbacks.IRecyclerItemClickLitner
import com.communisolve.foodversyserverapp.databinding.LayoutAddonSizeItemBinding
import com.communisolve.foodversyserverapp.eventbus.SelectSizeModel
import com.communisolve.foodversyserverapp.eventbus.UpdateSizeModel
import org.greenrobot.eventbus.EventBus

class MySizeAdapter(var context: Context, var sizeModelList: MutableList<SizeModel>) :
    RecyclerView.Adapter<MySizeAdapter.ViewHolder>() {
    lateinit var binding: LayoutAddonSizeItemBinding
    var editPos: Int
    var updateSizeModel: UpdateSizeModel

    init {
        editPos = -1
        updateSizeModel = UpdateSizeModel()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        binding.txtName.text = sizeModelList.get(position).name
        binding.txtPrice.text = sizeModelList.get(position).price.toString()

        binding.imgDelete.setOnClickListener {
            sizeModelList.removeAt(position)
            notifyItemRemoved(position)
            updateSizeModel.sizeModelList = sizeModelList
            EventBus.getDefault().postSticky(updateSizeModel)
        }

        holder.setListner(object :IRecyclerItemClickLitner{
            override fun onItemClick(view: View, pos: Int) {
                editPos = pos
                EventBus.getDefault().postSticky(SelectSizeModel(sizeModelList[pos]))
            }

        })
    }

    override fun getItemCount(): Int = sizeModelList.size
    fun addNewSize(sizeModel: SizeModel) {
        sizeModelList.add(sizeModel)
        notifyItemInserted(sizeModelList.size-1)
        updateSizeModel.sizeModelList = sizeModelList
        EventBus.getDefault().postSticky(updateSizeModel)
    }

    fun editSize(sizeModel: SizeModel) {
        sizeModelList.set(editPos,sizeModel)
        notifyItemChanged(editPos)
        updateSizeModel.sizeModelList = sizeModelList
        EventBus.getDefault().postSticky(updateSizeModel)

    }

}
