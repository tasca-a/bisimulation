package com.example.bisimulation.activities.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {
    var email: String = ""
    var password: String = ""

    private var auth: FirebaseAuth = Firebase.auth

    private var _logInStatus = MutableLiveData<Boolean>()
    val logInStatus: LiveData<Boolean> = _logInStatus
    fun logIn(){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _logInStatus.value = task.isSuccessful
        }
    }

    private var _resetPasswordStatus = MutableLiveData<Boolean>()
    val resetPasswordStatus: LiveData<Boolean> = _resetPasswordStatus
    fun resetPassword(emailToReset: String){
        auth.sendPasswordResetEmail(emailToReset).addOnCompleteListener { task ->
            _resetPasswordStatus.value = task.isSuccessful
        }
    }
}