package com.communisolve.foodversyserverapp.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.communisolve.foodversyserverapp.R

class OdersFragment : Fragment() {

    private lateinit var ordersViewModel: OrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ordersViewModel =
            ViewModelProvider(this).get(OrdersViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        return root
    }
}