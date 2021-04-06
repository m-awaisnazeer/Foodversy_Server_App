package com.communisolve.foodversyserverapp.ui.foodlist

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.adapter.MyFoodListAdapter
import com.communisolve.foodversyserverapp.callbacks.IOnFoodsListItemMenuClickListner
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.eventbus.ChangeMenuClick
import com.communisolve.foodversyserverapp.eventbus.ToastEvent
import com.communisolve.foodversyserverapp.model.FoodModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FoodListFragment : Fragment(), IOnFoodsListItemMenuClickListner {
    private val SELECT_IMAGE_REQUEST_CODE: Int = 112
    lateinit var recycler_food_list: RecyclerView
    lateinit var adapter: MyFoodListAdapter
    private lateinit var foodListViewModel: FoodListViewModel
    private var foodModels: List<FoodModel> = ArrayList<FoodModel>()

    private lateinit var image_food: ImageView
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var dialog: AlertDialog
    var imageUri: Uri? = null


    private var foodModelList: List<FoodModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodListViewModel =
            ViewModelProvider(this).get(FoodListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_list, container, false)
        recycler_food_list = root.findViewById(R.id.recycler_food_list)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        dialog = SpotsDialog.Builder().setContext(context)
            .setCancelable(false).build()
        dialog.show()
        recycler_food_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        }

        (activity as AppCompatActivity).supportActionBar!!.title = Common.categorySelected!!.name
        populateRecyclerView()
        return root
    }

    private fun populateRecyclerView() {
        if (isAdded) {
            if (dialog.isShowing)
                dialog.dismiss()
        }

        foodListViewModel.getMutavleFoodliveData().observe(viewLifecycleOwner, Observer {
            foodModels = it
            adapter = MyFoodListAdapter(requireContext(), it, this)
            recycler_food_list.adapter = adapter
        })
    }

    override fun onUpdateItemCLickListner(position: Int, foodModel: FoodModel) {
        //Common.foodSelected = foodModel

        showUpdateDialog(position)
    }

    private fun showUpdateDialog(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update")
        builder.setMessage("Please fill information")

        val itemView =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_update_food, null)

        val edt_food_name: EditText = itemView.findViewById(R.id.edt_food_name)
        val edt_food_price: EditText = itemView.findViewById(R.id.edt_food_price)
        val edt_food_description: EditText = itemView.findViewById(R.id.edt_food_description)
        image_food = itemView.findViewById(R.id.image_food)

        edt_food_name.setText(Common.categorySelected!!.foods!![position].name)
        edt_food_price.setText(Common.categorySelected!!.foods!![position].price.toString())
        edt_food_description.setText(Common.categorySelected!!.foods!![position].description)

        Glide.with(requireContext()).load(Common.categorySelected!!.foods!![position].image)
            .into(image_food)

        image_food.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "SELECT PICTURE"),
                SELECT_IMAGE_REQUEST_CODE
            )
        }

        builder.setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.setPositiveButton("UPDATE") { dialogInterface, _ ->

            val updateFood = Common.categorySelected!!.foods!![position]
            updateFood.name = edt_food_name.text.toString()
            updateFood.price = if (TextUtils.isEmpty(edt_food_price.text))
                0
            else
                edt_food_price.text.toString().toLong().toInt()
            updateFood.description = edt_food_description.text.toString()

            if (imageUri != null) {
                dialog.setMessage("Uploading...")
                dialog.show()

                val imageName = UUID.randomUUID().toString()
                val imageFolder = storageReference.child("images/$imageName")
                imageFolder.putFile(imageUri!!)
                    .addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener { tasksnapshot ->
                        val progress =
                            100.0 * tasksnapshot.bytesTransferred / tasksnapshot.totalByteCount
                        dialog.setMessage("Uploaded $progress")
                    }.addOnSuccessListener {
                        dialogInterface.dismiss()
                        imageFolder.downloadUrl.addOnSuccessListener { uri ->
                            dialog.dismiss()
                            updateFood.image = uri.toString()
                            Common.categorySelected!!.foods!![position] = updateFood
                            updateFood(Common.categorySelected!!.foods!!, false)
                        }
                    }

            } else {
                Common.categorySelected!!.foods!![position] = updateFood
                updateFood(Common.categorySelected!!.foods!!, false)
            }

        }

        builder.setView(itemView)
        var updateDialog = builder.create()
        updateDialog.show()

    }


    override fun onDeleteItemCLickListner(position: Int, foodModel: FoodModel) {
        Common.foodSelected = foodModel

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete")
            .setMessage("Do you Really want to delete food ?")
            .setNegativeButton("CANCEL") { dialoginterface, _ -> dialoginterface.dismiss() }
            .setPositiveButton("DELETE") { dialoginterface, _ ->
                Common.categorySelected!!.foods!!.removeAt(position)
                updateFood(Common.categorySelected!!.foods, true)
            }

        val deleteDialog = builder.create()
        deleteDialog.show()
    }

    private fun updateFood(foods: MutableList<FoodModel>?, isDelete: Boolean) {

        Common.categorySelected!!.foods = foods
        val updateData = HashMap<String, Any>()
        updateData["foods"] = foods!!
        FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
            .child(Common.categorySelected!!.menu_id)
            .updateChildren(updateData)
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    populateRecyclerView()
                    EventBus.getDefault().postSticky(ToastEvent(isDelete, true))
                }

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                imageUri = data.data
                image_food.setImageURI(imageUri)
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(ChangeMenuClick(true))
        super.onDestroy()
    }
}