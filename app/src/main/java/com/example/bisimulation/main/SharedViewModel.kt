package com.example.bisimulation.main

import android.util.Log
import androidx.compose.animation.core.snap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class SharedViewModel : ViewModel() {
    // Firebase initialization
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser
    private val db = Firebase.firestore
    private val realtimeDb = FirebaseDatabase.getInstance("https://bisimulation-default-rtdb.europe-west1.firebasedatabase.app")

    private val _name = MutableLiveData<String>()
    private val _surname = MutableLiveData<String>()
    private val _username = MutableLiveData<String>()
    private val _victories = MutableLiveData<Int>()
    private val _losses = MutableLiveData<Int>()
    private val _activeUsers = MutableLiveData<Int>()
    val name: LiveData<String> = _name
    val surname: LiveData<String> = _surname
    val username: LiveData<String> = _username
    val victories: LiveData<Int> = _victories
    val losses: LiveData<Int> = _losses
    val activeUsers: LiveData<Int> = _activeUsers

    val email = currentUser?.email

    init {
        //TODO: crea oggetto "Repository" a parte, in modo da non mischiare model e viewModel
        if (currentUser != null) {
            //Get all the user extra info
            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    _name.value = document.data?.get("name").toString()
                    _surname.value = document.data?.get("surname").toString()
                    _username.value = document.data?.get("username").toString()
                }

            //Update stats in real time
            db.collection("stats")
                .document(currentUser.uid)
                .addSnapshotListener { value, error ->
                    if (error != null) return@addSnapshotListener

                    if (value != null && value.exists()){
                        try {
                            _victories.value = value.getLong("victories")?.toInt()
                            _losses.value = value.getLong("losses")?.toInt()
                        } catch (e: Exception){
                            _victories.value = 0
                            _losses.value = 0
                        }
                    }
                }

            //Update and manages currently active users count
            val activeUsersRef = realtimeDb.getReference("activeUsers")
            activeUsersRef.setValue(ServerValue.increment(1))
            activeUsersRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    _activeUsers.value = snapshot.getValue<Int>()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DB ERROR: ", error.toString())
                }
            })
            activeUsersRef.onDisconnect().setValue(ServerValue.increment(-1))
        }
    }
}