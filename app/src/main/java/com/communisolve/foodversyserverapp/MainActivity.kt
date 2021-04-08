package com.communisolve.foodversyserverapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.communisolve.foodversyserverapp.common.Common
import com.communisolve.foodversyserverapp.model.ServerUserModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import dmax.dialog.SpotsDialog
import java.util.*


val APP_REQUEST_CODE = 2222

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var authListner: FirebaseAuth.AuthStateListener? = null
    private var dialog: AlertDialog? = null
    private var serverRef: DatabaseReference? = null
    private var providers: List<AuthUI.IdpConfig>? = null

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authListner)
    }

    override fun onStop() {
        firebaseAuth.removeAuthStateListener(authListner)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        init()
    }

    private fun init() {
        providers = Arrays.asList(AuthUI.IdpConfig.PhoneBuilder().build())

        serverRef = FirebaseDatabase.getInstance().getReference(Common.SERVER_REF)
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        authListner = FirebaseAuth.AuthStateListener {
            val user = firebaseAuth.currentUser

            if (user != null) {

                checkServerUserFromeFirebaseDatabase(user)
            } else {
                phoneLogin()
            }
        }
    }

    private fun checkServerUserFromeFirebaseDatabase(user: FirebaseUser) {
        dialog!!.show()
        serverRef!!.child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dialog!!.dismiss()
                    if (snapshot.exists()) {
                        val userModel = snapshot.getValue(ServerUserModel::class.java)
                        if (userModel!!.isActive) {
                            goToHomeActivity(userModel)
                        } else {
                            dialog!!.dismiss()
                            Toast.makeText(
                                this@MainActivity,
                                "You must be allowed from Admin to access this app",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        dialog!!.dismiss()
                        showRegisterDialog(user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog!!.dismiss()
                    Toast.makeText(
                        this@MainActivity, "${error.message}", Toast.LENGTH_SHORT
                    ).show()

                }

            })
    }

    private fun goToHomeActivity(userModel: ServerUserModel) {
        dialog!!.dismiss()
//        Common.currentServerUser = userModel
//        startActivity(Intent(this,HomeActivity::class.java))
//        finish()
        FirebaseInstanceId.getInstance().instanceId
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                Common.currentServerUser = userModel
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnCompleteListener {
                Common.currentServerUser = userModel
                Common.updateToken(this, it.result!!.token)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
    }

    private fun showRegisterDialog(user: FirebaseUser) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Register")
        builder.setTitle("Please fill information \n Admin will accept your account late")

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null)
        val edt_name = itemView.findViewById<EditText>(R.id.edt_name)
        val edt_phone = itemView.findViewById<EditText>(R.id.edt_phone)

        edt_phone.setText(user.phoneNumber)

        builder.setNegativeButton("CANCEL") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.setPositiveButton("REGISTER") { dialogInterface, _ ->
            if (TextUtils.isEmpty(edt_name.text)) {
                Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val serverModel =
                ServerUserModel(user.uid, edt_name.text.toString(), edt_name.text.toString(), true)

            dialog!!.show()
            serverRef!!.child(serverModel.uid).setValue(serverModel).addOnFailureListener {
                dialog!!.dismiss()
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                dialog!!.dismiss()
                Toast.makeText(
                    this,
                    "Register success! Admin will check and active user soon",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        builder.setView(itemView)
        val registerDialog = builder.create()
        registerDialog.show()

    }

    private fun phoneLogin() {

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.foodversy_iconpng)
                .setTheme(R.style.LogInTheme)
                .setAvailableProviders(providers!!)
                .build(), APP_REQUEST_CODE
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE) {

            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(this, "Failed to Sign In ${response!!.error}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }



}