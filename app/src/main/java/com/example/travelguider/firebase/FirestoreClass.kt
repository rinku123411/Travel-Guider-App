package com.example.travelguider.firebase

import android.app.Activity
import com.example.travelguider.activities.MainActivity
import com.example.travelguider.activities.MyProfileActivity
import com.example.travelguider.activities.SignInActivity
import com.example.travelguider.activities.SignUpActivity
import com.example.travelguider.models.User
import com.example.travelguider.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constant.USERS).document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
    }
}
    fun signInUser(activity: Activity){
        mFireStore.collection(Constant.USERS).document(getCurrentUserId()).get().addOnSuccessListener{
            document->
            val loggedInUser=document.toObject(User::class.java)!!
            when (activity){
                is SignInActivity->{
                    activity.signInSuccess(loggedInUser)
                }
                is MainActivity->{
                    activity.updateNavigationUserDetails(loggedInUser)
                }
                is MyProfileActivity->{
                    activity.setUserDataInUI(loggedInUser)
                }
            }

        }.addOnFailureListener{
            e->
            when(activity){
                is SignInActivity->{
                    activity.hideProgressDialog()
                }
                is MainActivity->{
                    activity.hideProgressDialog()
                }
            }
        }
    }
    fun getCurrentUserId():String{
        val currentUser= FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }

}