package com.example.bisimulation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SharedViewModel : ViewModel() {
    // Firebase initialization
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    private val db = Firebase.firestore

    private val _name = MutableLiveData<String>()
    private val _surname = MutableLiveData<String>()
    private val _username = MutableLiveData<String>()
    val name: LiveData<String> = _name
    val surname: LiveData<String> = _surname
    val username: LiveData<String> = _username
    val email = currentUser?.email

    init {
        //TODO: crea oggetto "Repository" a parte, in modo da non mischiare model e viewModel
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    _name.value = document.data?.get("name").toString()
                    _surname.value = document.data?.get("surname").toString()
                    _username.value = document.data?.get("username").toString()
                }
        }
    }
}