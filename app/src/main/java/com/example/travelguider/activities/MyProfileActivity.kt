package com.example.travelguider.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.MimeTypeFilter
import com.bumptech.glide.Glide
import com.example.travelguider.R
import com.example.travelguider.firebase.FirestoreClass
import com.example.travelguider.models.User
import com.example.travelguider.utils.Constants
import com.example.travelguider.utils.Constants.IMAGE

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.io.IOError
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    companion object{
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2
    }

    private var mSelectedImageFileUri: Uri?=null
    private var mProfileImageURL:String=""
    private lateinit var mUserDetails:User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setupActionBar()
        FirestoreClass().signInUser(this)
        iv_profile_user_image.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                showImageChooser()
                }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE)
            }
        }
        update_btn.setOnClickListener{
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }
            else{
                showProgressDialog()
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
            else{
                Toast.makeText(this,"Oops! Permission denied",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showImageChooser(){
        var galleryIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK && requestCode== PICK_IMAGE_REQUEST_CODE && data!!.data!=null ){
            mSelectedImageFileUri=data.data
            try{
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_profile_user_image)
            }
            catch(e:IOException){
                e.printStackTrace()
            }

        }
    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title="My profile"
        }
        toolbar_my_profile_activity.setNavigationOnClickListener{onBackPressed()}
    }
    fun setUserDataInUI(user:User){
        mUserDetails=user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)

            et_name.setText(user.name)
        et_email.setText(user.email)
        if(user.mobile!=0L){
            et_mobile.setText(user.mobile.toString())
        }
    }
    private fun getFileExtension(uri:Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    private fun uploadUserImage(){
        showProgressDialog()
        if(mSelectedImageFileUri!=null){
            val sRef: StorageReference =FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()
            +"."+getFileExtension(mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    mProfileImageURL=uri.toString()
                    updateUserProfileData()
                    hideProgressDialog()

                }
            }.addOnFailureListener{
                exception->
                Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }
    fun updateUserProfileData(){
        var userHashMap=HashMap<String,Any>()
        var anyChanges=false
        if(mProfileImageURL.isNotEmpty()&& mProfileImageURL!=mUserDetails.image){
            userHashMap[com.example.travelguider.utils.Constants.IMAGE]=mProfileImageURL
            anyChanges=true
        }
        if(et_name.text.toString()!=mUserDetails.name){
            userHashMap[com.example.travelguider.utils.Constants.NAME]=et_name.text.toString()
            anyChanges=true
        }
        if(et_mobile.text.toString()!=mUserDetails.mobile.toString()){
            userHashMap[com.example.travelguider.utils.Constants.MOBILE]=et_mobile.text.toString().toLong()
            anyChanges=true
        }
        if(anyChanges)
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}