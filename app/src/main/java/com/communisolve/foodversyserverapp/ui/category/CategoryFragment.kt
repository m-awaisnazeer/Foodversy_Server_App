package com.communisolve.foodversyserverapp.ui.category

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.adapter.MyCategoriesAdapter
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.common.SpacesItemDecoration
import dmax.dialog.SpotsDialog

class CategoryFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel

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
            mAdapter = MyCategoriesAdapter(requireContext(), it)
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

}