package com.example.bisimulation.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpViewModel : ViewModel() {
    var name: String = ""
    var surname: String = ""
    var email: String = ""
    var username: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    private var auth: FirebaseAuth = Firebase.auth

    private var _signUpStatus = MutableLiveData<Boolean>()
    val signUpStatus: LiveData<Boolean> = _signUpStatus
    fun signUp(){
        _signUpStatus.value = true
    }


}