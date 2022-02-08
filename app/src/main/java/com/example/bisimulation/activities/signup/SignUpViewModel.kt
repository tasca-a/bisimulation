package com.example.bisimulation.activities.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.bisimulation.R
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.ktx.firestore

class SignUpViewModel : ViewModel() {
    var name: String = ""
    var surname: String = ""
    var email: String = ""
    var username: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    private var auth: FirebaseAuth = Firebase.auth

    private var _signUpStatus = MutableLiveData<Int>()
    val signUpStatus: LiveData<Int> = _signUpStatus
    fun signUp() {
        val user = hashMapOf(
            "name" to name,
            "surname" to surname,
            "username" to username,
        )
        val stats = hashMapOf(
            "victories" to 0,
            "losses" to 0
        )

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (addUserDetail(user, stats))
                    _signUpStatus.value = R.string.signUpSuccessful_Toast
                else {
                    //TODO: if the user details are not stored, cancel the registration
                }
            } else {
                if (task.exception is FirebaseAuthUserCollisionException)
                    _signUpStatus.value = R.string.signUpUserCollisiom_Toast
                else
                    _signUpStatus.value = R.string.signUpFailed_Toast
            }
        }
    }

    // Add user details in a Firestore document that can be accessed with user UID
    private fun addUserDetail(user: HashMap<String, String>, stats: HashMap<String, Int>): Boolean {
        var result = true

        val db = Firebase.firestore
        val uid = auth.currentUser?.uid

        if (uid != null) {
            //Adds the details
            db.collection("users")
                .document(uid)
                .set(user)
                .addOnFailureListener { result = false }

            //Adds initial statistics
            db.collection("stats")
                .document(uid)
                .set(stats)
                .addOnFailureListener { result = false }
        } else
            result = false

        return result
    }
}