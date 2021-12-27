package com.example.bisimulation.login

import android.util.Log
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email: String = ""
    var password: String = ""

    fun logIn(){
        Log.i("LoginViewModel", "Sign in triggered.")
    }

}