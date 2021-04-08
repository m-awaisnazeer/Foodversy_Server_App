package com.communisolve.foodversyserverapp.ui.orders

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.communisolve.foodversy.model.FCMSendData
import com.communisolve.foodversy.remote.RetrofitFCMClient
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.adapter.MyOrderAdapter
import com.communisolve.foodversyserverapp.callbacks.IOnOrderItemMenuClickListener
import com.communisolve.foodversyserverapp.common.BottomSheetOrderfragment
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.databinding.FragmentOrdersBinding
import com.communisolve.foodversyserverapp.eventbus.ChangeMenuClick
import com.communisolve.foodversyserverapp.eventbus.LoadOrderEvent
import com.communisolve.foodversyserverapp.model.OrderModel
import com.communisolve.foodversyserverapp.model.TokenModel
import com.communisolve.foodversyserverapp.remote.IFCMService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OdersFragment : Fragment()//, IOnOrderItemMenuClickListener
{
    private var compositeDisposable = CompositeDisposable()
    private lateinit var ifcmService: IFCMService
    var currentOrdersStausFilter: Int = 0
    var adapter: MyOrderAdapter? = null
    var iOnOrderItemMenuClickListener = object : IOnOrderItemMenuClickListener {
        override fun onEditSelectionCliclListener(position: Int, orderModel: OrderModel) {
            showEditDialog(position, orderModel)
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
                            updateTextCounter()
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

    private fun showEditDialog(position: Int, orderModel: OrderModel) {
        var layout_dialog: View? = null
        var builder: AlertDialog.Builder? = null

        var rdi_shipping: RadioButton? = null
        var rdi_shipped: RadioButton? = null
        var rdi_cancelled: RadioButton? = null
        var rdi_deleted: RadioButton? = null
        var rdi_restore_placed: RadioButton? = null


        if (orderModel.orderStatus == -1) {
            layout_dialog = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_dialog_cancelled, null)
            builder = AlertDialog.Builder(requireContext()).setView(layout_dialog)

            rdi_restore_placed =
                layout_dialog.findViewById<View>(R.id.rdi_restore_placed) as RadioButton
            rdi_deleted = layout_dialog.findViewById<View>(R.id.rdi_deleted) as RadioButton


        } else if (orderModel.orderStatus == 0) {
            layout_dialog = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_dialog_shipping, null)
            builder = AlertDialog.Builder(
                requireContext(),
                android.R.style.Theme_Material_Light_NoActionBar_Fullscreen
            ).setView(layout_dialog)
            rdi_shipping = layout_dialog.findViewById<View>(R.id.rdi_shipping) as RadioButton
            rdi_cancelled = layout_dialog.findViewById<View>(R.id.rdi_cancelled) as RadioButton

        } else {
            layout_dialog = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_dialog_shipped, null)
            builder = AlertDialog.Builder(
                requireContext()
            ).setView(layout_dialog)
            rdi_shipped = layout_dialog.findViewById<View>(R.id.rdi_shipped) as RadioButton
            rdi_cancelled = layout_dialog.findViewById<View>(R.id.rdi_cancelled) as RadioButton

        }

        //view
        val btn_ok = layout_dialog.findViewById<View>(R.id.btn_ok) as Button
        val btn_cancel = layout_dialog.findViewById<View>(R.id.btn_cancel) as Button


        val txt_status = layout_dialog.findViewById<View>(R.id.txt_status) as TextView

        //set Data
        txt_status.setText("Order Status(${Common.convertStatustoString(orderModel.orderStatus)})")

        //create Dialog
        val dialog = builder.create()
        dialog.show()

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            dialog.dismiss()
            if (rdi_cancelled != null && rdi_cancelled.isChecked) {
                updateOrder(position, orderModel, -1)
                currentOrdersStausFilter = -1
                EventBus.getDefault().postSticky(LoadOrderEvent(-1))
            } else if (rdi_shipping != null && rdi_shipping.isChecked) {
                updateOrder(position, orderModel, 1)
                currentOrdersStausFilter = 1
                EventBus.getDefault().postSticky(LoadOrderEvent(1))
            } else if (rdi_shipped != null && rdi_shipped.isChecked) {
                updateOrder(position, orderModel, 2)
                currentOrdersStausFilter = 2
                EventBus.getDefault().postSticky(LoadOrderEvent(2))
            } else if (rdi_restore_placed != null && rdi_restore_placed.isChecked) {
                updateOrder(position, orderModel, 0)
                currentOrdersStausFilter = 0
                EventBus.getDefault().postSticky(LoadOrderEvent(0))
            } else if (rdi_deleted != null && rdi_deleted.isChecked) {
                deleteOrder(position, orderModel)
            }
        }
    }

    private fun deleteOrder(position: Int, orderModel: OrderModel) {
        if (!TextUtils.isEmpty(orderModel.key)) {

            FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .child(orderModel.key)
                .removeValue()
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener {
                    adapter!!.removeItemAt(position)
                    updateTextCounter()
                    Toast.makeText(requireContext(), "Update Order Success", Toast.LENGTH_SHORT)
                        .show()
                    populateData()
                }
        } else {
            Toast.makeText(requireContext(), "Order Number must not be empty", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun updateOrder(position: Int, orderModel: OrderModel, status: Int) {

        if (!TextUtils.isEmpty(orderModel.key)) {
            val update_data = HashMap<String, Any>()
            update_data.put("orderStatus", status)

            FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .child(orderModel.key)
                .updateChildren(update_data)
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener {

                    val dialog =
                        SpotsDialog.Builder().setContext(requireContext()).setCancelable(false)
                            .build()
                    dialog.show()

                    //loadToken
                    FirebaseDatabase.getInstance()
                        .getReference(Common.TOKEN_REF)
                        .child(orderModel.userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val tokenModel = snapshot.getValue(TokenModel::class.java)
                                    val notiData = HashMap<String, String>()
                                    notiData.put(Common.NOTI_TITLE!!, "Your order ${orderModel.key} was updated")
                                    notiData.put(
                                        Common.NOTI_CONTENT!!, StringBuilder("Your order ")
                                            .append("status")
                                            .append(" changed to ")
                                            .append(Common.convertStatustoString(status)).toString()
                                    )

                                    val sendData = FCMSendData(tokenModel!!.token, notiData)

                                    compositeDisposable.add(
                                        ifcmService.sendNotification(sendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({ FcmResponse ->
                                                if (FcmResponse.success != 0) {
                                                    dialog.dismiss()
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Update Order Successfully",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()

                                                } else {
                                                    dialog.dismiss()
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Failed to Send Notification",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                }
                                            }, {
                                                dialog.dismiss()
                                                Toast.makeText(
                                                    requireContext(),
                                                    "${it.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            })
                                    )


                                } else {
                                    dialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        "User token not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                dialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "${error.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        })

                    adapter!!.removeItemAt(position)
                    updateTextCounter()
                    populateData()

                }
        } else {
            Toast.makeText(requireContext(), "Order Number must not be empty", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun updateTextCounter() {
        if (adapter != null)
            binding.txtOrderFilter.setText("${Common.convertStatustoString(currentOrdersStausFilter)} Orders(${adapter!!.itemCount})")


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
        populateData()

        return binding.root
    }

    private fun populateData() {
        ordersViewModel.getOrderModelList().observe(viewLifecycleOwner, Observer {
            adapter = MyOrderAdapter(
                requireContext(),
                it as MutableList<OrderModel>, iOnOrderItemMenuClickListener
            )
            binding.recyclerOrder.adapter = adapter
            binding.recyclerOrder.layoutAnimation = layoutAnimationController
            updateTextCounter()
        })

        ordersViewModel.messageError.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), "${it}", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initViews() {
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService::class.java)
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
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
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

        compositeDisposable.clear()
        super.onStop()
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(ChangeMenuClick(true))
        super.onDestroy()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadOrder(event: LoadOrderEvent) {
        currentOrdersStausFilter = event.status
        ordersViewModel.loadOrder(event.status)
    }

}