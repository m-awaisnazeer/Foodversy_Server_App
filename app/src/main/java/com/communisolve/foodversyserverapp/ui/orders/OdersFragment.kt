package com.communisolve.foodversyserverapp.ui.orders

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.adapter.MyOrderAdapter
import com.communisolve.foodversyserverapp.common.BottomSheetOrderfragment
import com.communisolve.foodversyserverapp.databinding.FragmentOrdersBinding
import com.communisolve.foodversyserverapp.eventbus.ChangeMenuClick
import com.communisolve.foodversyserverapp.eventbus.LoadOrderEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OdersFragment : Fragment() {

    val ordersViewModel: OrdersViewModel by viewModels()
    private lateinit var binding: FragmentOrdersBinding
    lateinit var layoutAnimationController: LayoutAnimationController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val root = inflater.inflate(R.layout.fragment_food_list, container, false)
        binding = FragmentOrdersBinding.inflate(inflater)
        initViews()
        Toast.makeText(requireContext(), "test", Toast.LENGTH_SHORT).show()
        ordersViewModel.getOrderModelList().observe(viewLifecycleOwner, Observer {
            binding.recyclerOrder.adapter = MyOrderAdapter(requireContext(), it)
            binding.recyclerOrder.layoutAnimation = layoutAnimationController
            binding.txtOrderFilter.setText("Orders (${it.size})")
        })

        ordersViewModel.messageError.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), "${it}", Toast.LENGTH_SHORT).show()
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initViews() {
        setHasOptionsMenu(true)
        layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        binding.recyclerOrder.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.order_list_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_filter -> {
                val bottomSheet = BottomSheetOrderfragment.instance
                bottomSheet!!.show(requireActivity().supportFragmentManager,"OrderList")
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(LoadOrderEvent::class.java))
            EventBus.getDefault().removeStickyEvent(LoadOrderEvent::class.java)

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(ChangeMenuClick(true))
        super.onDestroy()
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onLoadOrder(event:LoadOrderEvent){
        Toast.makeText(requireContext(), "${event.status}", Toast.LENGTH_SHORT).show()
        ordersViewModel.loadOrder(event.status)
    }
}