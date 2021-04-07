package com.communisolve.foodversyserverapp.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.communisolve.foodversyserverapp.databinding.FragmentOrderFilterBinding
import com.communisolve.foodversyserverapp.eventbus.LoadOrderEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.greenrobot.eventbus.EventBus

class BottomSheetOrderfragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentOrderFilterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderFilterBinding.inflate(inflater)
        //val itemView = inflater.inflate(R.layout.fragment_order_filter, container, false)
        binding.placedFilter.setOnClickListener {
            EventBus.getDefault().postSticky(LoadOrderEvent(0))
            dismiss()
        }

        binding.shippingFilter.setOnClickListener {
            EventBus.getDefault().postSticky(LoadOrderEvent(1))
            dismiss()
        }


        binding.shippedFilter.setOnClickListener {
            EventBus.getDefault().postSticky(LoadOrderEvent(2))
            dismiss()
        }

        binding.cancelledFilter.setOnClickListener {
            EventBus.getDefault().postSticky(LoadOrderEvent(-1))
            dismiss()
        }
        return binding.root
    }

    companion object {
        val instance: BottomSheetOrderfragment? = null
            get() = field ?: BottomSheetOrderfragment()
    }
}