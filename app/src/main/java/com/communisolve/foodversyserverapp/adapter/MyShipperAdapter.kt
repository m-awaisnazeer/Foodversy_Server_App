package com.communisolve.foodversyserverapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.foodversyserverapp.databinding.LayoutShipperBinding
import com.communisolve.foodversyserverapp.eventbus.UpdateActiveEvent
import com.communisolve.foodversyserverapp.model.ShipperUserModel
import org.greenrobot.eventbus.EventBus

class MyShipperAdapter(
    var context: Context,
    var shippersUsersList: List<ShipperUserModel>
) : RecyclerView.Adapter<MyShipperAdapter.ViewHolder>() {

    lateinit var binding: LayoutShipperBinding

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutShipperBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentShipperUser = shippersUsersList.get(position)
        binding.txtName.setText(currentShipperUser.name)
        binding.txtPhone.setText(currentShipperUser.phone)
        binding.btnEnable.isChecked = currentShipperUser.isActive

        binding.btnEnable.setOnCheckedChangeListener { buttonView, isChecked ->
            EventBus.getDefault().postSticky(UpdateActiveEvent(currentShipperUser,isChecked))
        }
    }

    override fun getItemCount(): Int = shippersUsersList.size
}