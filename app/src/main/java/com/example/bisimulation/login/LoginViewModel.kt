package com.example.bisimulation.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bisimulation.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {
    var email: String = ""
    var password: String = ""

    private var auth: FirebaseAuth = Firebase.auth

    private var _logInStatus = MutableLiveData<Int>()
    val logInStatus: LiveData<Int> = _logInStatus

    fun logIn(){
        Log.i("LoginViewModel", "Sign in triggered.")

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.d(TAG, "Log In successful.")
                _logInStatus.value = R.string.logInSuccessful_Toast
            }
            else {
                Log.e(TAG, "Log In failed.")
                _logInStatus.value = R.string.logInFailed_Toast
            }
        }
    }

    companion object{
        private const val TAG = "LogInViewModel"
    }

}