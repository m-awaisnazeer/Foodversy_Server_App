package com.communisolve.foodversyserverapp.ui.shipper

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.adapter.MyShipperAdapter
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.databinding.ShipperFragmentBinding
import com.communisolve.foodversyserverapp.eventbus.UpdateActiveEvent
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ShipperFragment : Fragment() {

    lateinit var binding: ShipperFragmentBinding

    private lateinit var dialog: AlertDialog
    private var mAdapter: MyShipperAdapter? = null

    companion object {
        fun newInstance() = ShipperFragment()
    }

    private val viewModel: ShipperViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShipperFragmentBinding.inflate(inflater)
        itemView()

        viewModel.getShippersList().observe(viewLifecycleOwner, Observer {
            dialog.dismiss()
            mAdapter = MyShipperAdapter(requireContext(), it)
            binding.recyclerShipper.adapter = mAdapter
            binding.recyclerShipper.layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        })

        return binding.root
    }

    private fun itemView() {
        binding.recyclerShipper.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }
        dialog = SpotsDialog.Builder().setContext(context)
            .setCancelable(false).build()
        dialog.show()

    }


    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateActiveEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateActiveEvent::class.java)

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onStop()

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onUpdateActiveEvent(updateActiveEvent: UpdateActiveEvent){
        val updateData = HashMap<String,Any>()
        updateData.put("active",updateActiveEvent.checked)

        FirebaseDatabase.getInstance().getReference(Common.SHIPPERS_REF)
            .child(updateActiveEvent.currentShipperUser.key)
            .updateChildren(updateData)
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "${it.message}",
                    Toast.LENGTH_SHORT
                ).show() }
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Status Updated",
                    Toast.LENGTH_SHORT
                ).show() }
    }
}