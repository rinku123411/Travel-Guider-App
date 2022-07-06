package com.example.travelguider.firebase

import com.example.travelguider.activities.SignInActivity
import com.example.travelguider.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FirestoreClass {
    private val mFirstStore=FirebaseFirestore.getInstance()
    fun registerUser(activity:SignInActivity,userInfo: User){

    }

}