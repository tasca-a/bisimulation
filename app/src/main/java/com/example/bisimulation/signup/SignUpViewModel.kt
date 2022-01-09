package com.example.bisimulation.signup

import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.bisimulation.R
import com.google.firebase.auth.FirebaseAuthUserCollisionException

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
    fun signUp(){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                _signUpStatus.value = R.string.signUpSuccessful_Toast
            } else {
                if (task.exception is FirebaseAuthUserCollisionException)
                    _signUpStatus.value = R.string.signUpUserCollisiom_Toast
                else
                    _signUpStatus.value = R.string.signUpFailed_Toast
            }
        }
    }


}