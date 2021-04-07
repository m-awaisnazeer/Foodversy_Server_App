package com.communisolve.foodversyserverapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.communisolve.foodversyserverapp.eventbus.CategoryClick
import com.communisolve.foodversyserverapp.eventbus.ChangeMenuClick
import com.communisolve.foodversyserverapp.eventbus.ToastEvent
import com.google.android.gms.common.internal.service.Common
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private var menuclick: Int=-1
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_category, R.id.nav_foodList, R.id.nav_list_orders
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerView = navView.getHeaderView(0)
        val txt_user = headerView.findViewById<TextView>(R.id.txt_user)
        txt_user.setText("Hey, ${com.communisolve.foodversyserverapp.common.Common.currentServerUser!!.name}")
        navView.menu.findItem(R.id.nav_signout).setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
            com.communisolve.foodversyserverapp.common.Common.currentServerUser = null
            com.communisolve.foodversyserverapp.common.Common.categorySelected = null
            com.communisolve.foodversyserverapp.common.Common.foodSelected = null

            startActivity(Intent(this,MainActivity::class.java))
            finish()
            return@setOnMenuItemClickListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
       return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick) {
        if (event.isClicked) {

            if (menuclick != R.id.nav_foodList){
                navController.navigate(R.id.nav_foodList)
                menuclick = R.id.nav_foodList
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onChangeMenuEvent(event: ChangeMenuClick) {
        if (!event.isFromFoodList) {
            //clear
            navController.popBackStack(R.id.nav_category, true)
            navController.navigate(R.id.nav_category)
        }
        menuclick = -1
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onToastEvent(event: ToastEvent) {

        if (event.isUpdate){
            Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
        }
        EventBus.getDefault().postSticky(ChangeMenuClick(event.isBackFromFoodList))
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}