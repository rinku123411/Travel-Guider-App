package com.example.travelguider.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.travelguider.R
import com.example.travelguider.firebase.FirestoreClass
import com.example.travelguider.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        signup_btn.setOnClickListener{
            registerUser()
        }
    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_button)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener{onBackPressed()}
    }
    fun userRegisteredSuccess(){
        Toast.makeText(this,"Users is successfully registered ",Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser(){
        val name:String=signup_name.text.toString().trim{it<=' '}
        val email:String=signup_email.text.toString().trim{it<=' '}
        val Password:String=signup_password.text.toString()
        if (validateForm(name,email,Password)){
            showProgressDialog()
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,Password)
                .addOnCompleteListener{
                    task->
                    if(task.isSuccessful){
                        val firebaseUser:FirebaseUser=task.result!!.user!!
                        val registeredEmail=firebaseUser.email!!
                        val user= User(firebaseUser.uid,name,registeredEmail)
                        FirestoreClass().registerUser(this,user)
                    }
                    else{
                        Toast.makeText(this,task.exception!!.message,Toast.LENGTH_LONG).show()
                    }
                }
        }
        else{
            Toast.makeText(this,"User is not registered",Toast.LENGTH_LONG).show()
        }
    }
    private fun validateForm(name:String,email:String,Password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please Enter the name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please Enter the email")
                false
            }
            TextUtils.isEmpty(Password)->{
                showErrorSnackBar("Please Enter the Password")
                false
            }
            else ->{
                true
            }
        }
    }
}