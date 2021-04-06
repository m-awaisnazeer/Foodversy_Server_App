package com.communisolve.foodversyserverapp.ui.category

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.adapter.MyCategoriesAdapter
import com.communisolve.foodversyserverapp.callbacks.IOnCategoriesItemMenuClickListner
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.common.SpacesItemDecoration
import com.communisolve.foodversyserverapp.model.CategoryModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dmax.dialog.SpotsDialog
import java.util.*
import kotlin.collections.HashMap

class CategoryFragment : Fragment(), IOnCategoriesItemMenuClickListner {

    private val SELECT_IMAGE_REQUEST_CODE: Int = 111
    private lateinit var categoryViewModel: CategoryViewModel

    //internal var categoryModels: List<CategoryModel> = ArrayList<CategoryModel>()
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var img_category: ImageView
    var imageUri: Uri? = null

    private lateinit var recycler_menu: RecyclerView
    private lateinit var dialog: AlertDialog
    private var mAdapter: MyCategoriesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryViewModel =
            ViewModelProvider(this).get(CategoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_category, container, false)
        recycler_menu = root.findViewById(R.id.recycler_menu)

        initViews()
        categoryViewModel.getCategoryList().observe(viewLifecycleOwner, Observer {
            dialog.dismiss()
            mAdapter = MyCategoriesAdapter(requireContext(), it, this)
            recycler_menu.adapter = mAdapter
            recycler_menu.layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        })
        categoryViewModel.getError().observe(viewLifecycleOwner, Observer {
            dialog.dismiss()
            Toast.makeText(context, "${it}", Toast.LENGTH_SHORT).show()
        })

        return root
    }

    private fun initViews() {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        dialog = SpotsDialog.Builder().setContext(context)
            .setCancelable(false).build()
        dialog.show()
        val mlayoutManager = GridLayoutManager(context, 2)
        mlayoutManager.orientation = RecyclerView.VERTICAL
        mlayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (mAdapter != null) {
                    when (mAdapter!!.getItemViewType(position)) {
                        Common.DEFAULT_COLUMN_COUNT -> 1
                        Common.FULL_WIDTH_COLUMN -> 2
                        else -> -1
                    }
                } else {
                    -1
                }
            }

        }
        recycler_menu.apply {
            setHasFixedSize(true)
            layoutManager = mlayoutManager
            addItemDecoration(SpacesItemDecoration(8))
        }
    }

    override fun onUpdateItemCLickListner(pos: Int, categoryModel: CategoryModel) {
        Toast.makeText(requireContext(), "Update ${categoryModel.name}", Toast.LENGTH_SHORT).show()
        Common.categorySelected = categoryModel

        showUpdateDialog()
    }

    private fun showUpdateDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Update Category")
        builder.setMessage("Please fill information")

        val itemView =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_update_category, null)
        var edt_category_name: EditText = itemView.findViewById(R.id.edt_category_name)
        img_category = itemView.findViewById(R.id.img_category)

        //setData
        edt_category_name.setText(Common.categorySelected!!.name)
        Glide.with(requireContext()).load(Common.categorySelected!!.image).into(img_category)

        //event
        img_category.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "SELECT PICTURE"),
                SELECT_IMAGE_REQUEST_CODE
            )
        }

        builder.setNegativeButton("CANCEL") { dialofInterface, _ -> dialofInterface.dismiss() }
        builder.setPositiveButton("UPDATE") { dialofInterface, _ ->

            val updateData = HashMap<String, Any>()
            updateData["name"] = edt_category_name.text.toString()
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
                        dialofInterface.dismiss()
                        imageFolder.downloadUrl.addOnSuccessListener { uri ->
                            updateData["image"] = uri.toString()
                            updateCategory(updateData)
                        }
                    }
            } else {
                updateCategory(updateData)
            }
        }

        builder.setView(itemView)
        val updateDialog = builder.create()
        updateDialog.show()
    }

    private fun updateCategory(updateData: java.util.HashMap<String, Any>) {
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
                categoryViewModel.loadCategory()
                Toast.makeText(requireContext(), "Update Success", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.data != null) {
                imageUri = data.data
                img_category.setImageURI(imageUri)
            }
        }
    }
}