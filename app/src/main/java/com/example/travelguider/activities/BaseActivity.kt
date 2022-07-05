package com.example.travelguider.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.travelguider.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce=false
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
    fun showProgressDialog(){
        mProgressDialog= Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
    fun getCurrentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun doubleBackExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce=true
        Toast.makeText(this,"Please click once again to exit",Toast.LENGTH_SHORT).show()
        Handler().postDelayed({doubleBackToExitPressedOnce=false},2000)
    }
    fun showErrorSnackBar(message:String){
        val snackBar=Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView=snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
        snackBar.show()
    }
}