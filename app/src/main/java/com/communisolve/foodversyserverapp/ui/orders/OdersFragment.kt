package com.communisolve.foodversyserverapp.ui.orders

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.communisolve.foodversyserverapp.callbacks.IOnOrderItemMenuClickListener
import com.communisolve.foodversyserverapp.common.BottomSheetOrderfragment
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.databinding.FragmentOrdersBinding
import com.communisolve.foodversyserverapp.eventbus.ChangeMenuClick
import com.communisolve.foodversyserverapp.eventbus.LoadOrderEvent
import com.communisolve.foodversyserverapp.model.OrderModel
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OdersFragment : Fragment()//, IOnOrderItemMenuClickListener
{
    var adapter: MyOrderAdapter? = null
    var iOnOrderItemMenuClickListener = object : IOnOrderItemMenuClickListener {
        override fun onEditSelectionCliclListener(position: Int, orderModel: OrderModel) {
            Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
        }

        override fun onRemoveSelectionCliclListener(position: Int, orderModel: OrderModel) {
            val builder = AlertDialog.Builder(requireContext())
                .setTitle("Delete")
                .setMessage("Do you really want to delete this order?")
                .setNegativeButton("CANCEL") { dialogInterface: DialogInterface, i: Int ->

                }
                .setPositiveButton("DELETE") { dialogInterface: DialogInterface, i: Int ->
                    FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                        .child(orderModel.key)
                        .removeValue()
                        .addOnCompleteListener {
                            Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                            adapter!!.removeItemAt(position)
                            binding.txtOrderFilter.setText("Orders (${adapter!!.itemCount})")
                            dialogInterface.dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                            dialogInterface.dismiss()

                        }
                }

            val dialog = builder.create()
            dialog.show()

            val btn_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            btn_negative.setTextColor(Color.LTGRAY)

            val btn_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            btn_positive.setTextColor(Color.RED)

        }

        override fun onCallSelectionCliclListener(position: Int, orderModel: OrderModel) {
            Dexter.withActivity(requireActivity())
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val intent = Intent()
                        intent.action = Intent.ACTION_DIAL
                        intent.setData(
                            Uri.parse(
                                StringBuilder("tel: ")
                                    .append(orderModel.userPhone).toString()
                            )
                        )
                        startActivity(intent)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            requireContext(),
                            "You must accept this permission to use this functionality",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        TODO("Not yet implemented")
                    }

                }).check()
        }

        override fun onDirectionSelectionCliclListener(position: Int, orderModel: OrderModel) {

        }

    }
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
            adapter = MyOrderAdapter(
                requireContext(),
                it as MutableList<OrderModel>, iOnOrderItemMenuClickListener
            )
            binding.recyclerOrder.adapter = adapter
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
        when (item.itemId) {
            R.id.action_filter -> {
                val bottomSheet = BottomSheetOrderfragment.instance
                bottomSheet!!.show(requireActivity().supportFragmentManager, "OrderList")
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadOrder(event: LoadOrderEvent) {
        Toast.makeText(requireContext(), "${event.status}", Toast.LENGTH_SHORT).show()
        ordersViewModel.loadOrder(event.status)
    }

//    override fun onEditSelectionCliclListener(position: Int, orderModel: OrderModel) {
//
//    }
//
//    override fun onRemoveSelectionCliclListener(position: Int, orderModel: OrderModel) {
//
//    }
//
//    override fun onCallSelectionCliclListener(position: Int, orderModel: OrderModel) {
//
//    }
//
//    override fun onDirectionSelectionCliclListener(position: Int, orderModel: OrderModel) {
//
//    }
}