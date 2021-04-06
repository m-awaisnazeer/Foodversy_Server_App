package com.communisolve.foodversyserverapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.communisolve.foodversyserverapp.callbacks.IRecyclerItemClickLitner
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.databinding.LayoutCategoryItemBinding
import com.communisolve.foodversyserverapp.eventbus.CategoryClick
import com.communisolve.foodversyserverapp.model.CategoryModel
import org.greenrobot.eventbus.EventBus

class MyCategoriesAdapter(
    internal var context: Context,
    internal var CategoriesList: List<CategoryModel>

) : RecyclerView.Adapter<MyCategoriesAdapter.ViewHolder>() {
    var binding: LayoutCategoryItemBinding? = null

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
            LayoutCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(CategoriesList.get(position).image).into(binding!!.imgCategory)
        binding!!.txtCategoryName.setText(CategoriesList.get(position).name)

        holder.itemView.setOnClickListener {
            Common.categorySelected = CategoriesList.get(position)
            Log.d("MTAG", "onBindViewHolder:${Common.categorySelected!!.menu_id} ")
            EventBus.getDefault().postSticky(CategoryClick(true,CategoriesList.get(position)))
        }

//        holder.setListner(object :IRecyclerItemClickLitner{
//            override fun onItemClick(view: View, pos: Int) {
//                Common.categorySelected = CategoriesList.get(pos)
//                EventBus.getDefault().postSticky(CategoryClick(true,CategoriesList.get(pos)))
//            }
//
//        })
    }

    override fun getItemViewType(position: Int): Int {
        return if (CategoriesList.size == 1)
            Common.DEFAULT_COLUMN_COUNT
        else {
            if (CategoriesList.size % 2 == 0) {
                Common.DEFAULT_COLUMN_COUNT
            } else {
                if (position > 1 && position == CategoriesList.size - 1) Common.FULL_WIDTH_COLUMN else Common.DEFAULT_COLUMN_COUNT
            }
        }

//        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return CategoriesList.size
    }
}