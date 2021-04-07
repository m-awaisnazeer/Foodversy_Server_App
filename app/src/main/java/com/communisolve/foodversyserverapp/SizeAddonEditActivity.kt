package com.communisolve.foodversyserverapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.communisolve.foodversy.model.AddOnModel
import com.communisolve.foodversy.model.SizeModel
import com.communisolve.foodversyserverapp.adapter.MyAddOnAdapter
import com.communisolve.foodversyserverapp.adapter.MySizeAdapter
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.databinding.ActivitySizeAddonEditBinding
import com.communisolve.foodversyserverapp.eventbus.*
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SizeAddonEditActivity : AppCompatActivity() {

    //Variable
    var adapter: MySizeAdapter? = null
    var aadOnadapter: MyAddOnAdapter? = null
    private var foodEditPosition = -1
    private var needSave = false
    private var isAddon = false

    private lateinit var binding: ActivitySizeAddonEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySizeAddonEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setSupportActionBar(binding.toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        binding.recyclerAddonSize.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerAddonSize.layoutManager = layoutManager
        binding.recyclerAddonSize.addItemDecoration(
            DividerItemDecoration(
                this,
                layoutManager.orientation
            )
        )

        binding.btnCreate.setOnClickListener {
            if (!isAddon) {
                if (adapter != null) {
                    val sizeModel = SizeModel()
                    sizeModel.name = binding.edtName.text.toString()
                    sizeModel.price = binding.edtPrice.text.toString().toLong()
                    adapter!!.addNewSize(sizeModel)
                } else {
                    if (aadOnadapter != null) {
                        val addonModel = AddOnModel()
                        addonModel.name = binding.edtName.text.toString()
                        addonModel.price = binding.edtPrice.text.toString().toLong()
                        aadOnadapter!!.addNewAddOn(addonModel)
                    }
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            if (!isAddon) { //Size
                if (adapter != null) {
                    val sizeModel = SizeModel()
                    sizeModel.name = binding.edtName.text.toString()
                    sizeModel.price = binding.edtPrice.text.toString().toLong()
                    adapter!!.editSize(sizeModel)
                }

            } else { //AddOn
                if (aadOnadapter != null) {
                    val addonModel = AddOnModel()
                    addonModel.name = binding.edtName.text.toString()
                    addonModel.price = binding.edtPrice.text.toString().toLong()
                    aadOnadapter!!.editAddOn(addonModel)
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().removeStickyEvent(UpdateSizeModel::class.java)
        super.onStop()
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onAddSizeReceive(event: AddonSizeEditEvent) {
        if (!event.isAddon) {
            if (Common.foodSelected!!.size != null) {
                adapter = MySizeAdapter(this, Common.foodSelected!!.size.toMutableList())
                foodEditPosition = event.position
                binding.recyclerAddonSize.adapter = adapter
                isAddon = event.isAddon
            }
        }else{
            if (Common.foodSelected!!.addon != null) {
                aadOnadapter = MyAddOnAdapter(this, Common.foodSelected!!.addon!!.toMutableList())
                foodEditPosition = event.position
                binding.recyclerAddonSize.adapter = aadOnadapter
                isAddon = event.isAddon
            }
        }
    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSizeModelUpdate(event: UpdateSizeModel) {
        if (event.sizeModelList != null) {
            needSave = true
            Common.foodSelected!!.size = event.sizeModelList!!
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onAddonModelUpdate(event: UpdateAddonModel) {
        if (event.addonModelList != null) {
            needSave = true
            Common.foodSelected!!.addon = event.addonModelList!!
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSelectSizeEvent(event: SelectSizeModel) {
        if (event.sizeModel != null) {
            binding.edtName.setText(event!!.sizeModel!!.name!!.toString())
            binding.edtPrice.setText(event!!.sizeModel!!.price!!.toString())
            binding.btnEdit.isEnabled = true
        } else
            binding.btnEdit.isEnabled = false
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSelectAddonEvent(event: SelectAddonModel) {
        if (event.addOnModel != null) {
            binding.edtName.setText(event!!.addOnModel!!.name!!.toString())
            binding.edtPrice.setText(event!!.addOnModel!!.price!!.toString())
            binding.btnEdit.isEnabled = true
        } else
            binding.btnEdit.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_size_addon, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> saveData()
            android.R.id.home -> {
                if (needSave) {
                    val builder = AlertDialog.Builder(this)
                        .setTitle("CANCEL?")
                        .setMessage("Do you really want to close without saving?")
                        .setNegativeButton("CANCEL", { dialogInterface, _ -> })
                        .setPositiveButton("OK") { dialogInterface, _ ->
                            needSave = false

                            closeActivity()
                        }
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    closeActivity()
                }
            }
        }
        return true
    }

    private fun saveData() {
        if (foodEditPosition != -1) {
            Common.categorySelected!!.foods?.set(foodEditPosition, Common.foodSelected!!)
            val updateData: MutableMap<String, Any> = HashMap()
            updateData["foods"] = Common.categorySelected!!.foods!!

            FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected!!.menu_id)
                .updateChildren(updateData)
                .addOnFailureListener {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reload Success", Toast.LENGTH_SHORT).show()
                        needSave = false
                        binding.edtName.setText("")
                        binding.edtPrice.setText("0")

                    }
                }
        }
    }

    private fun closeActivity() {
        binding.edtPrice.setText("0")
        binding.edtName.setText("")
        finish()

    }
}