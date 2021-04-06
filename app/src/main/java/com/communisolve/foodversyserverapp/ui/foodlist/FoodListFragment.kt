package com.communisolve.foodversyserverapp.ui.foodlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.communisolve.foodversyserverapp.R
import com.communisolve.foodversyserverapp.adapter.MyFoodListAdapter
import com.communisolve.foodversyserverapp.common.Common

class FoodListFragment : Fragment() {

    lateinit var recycler_food_list: RecyclerView
    lateinit var adapter: MyFoodListAdapter
    private lateinit var foodListViewModel: FoodListViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodListViewModel =
            ViewModelProvider(this).get(FoodListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_list, container, false)

        recycler_food_list = root.findViewById(R.id.recycler_food_list)
        recycler_food_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        }

        (activity as AppCompatActivity).supportActionBar!!.title = Common.categorySelected!!.name

        foodListViewModel.getMutavleFoodliveData().observe(viewLifecycleOwner, Observer {
            adapter = MyFoodListAdapter(requireContext(), it)
            recycler_food_list.adapter = adapter
        })
        return root
    }
}