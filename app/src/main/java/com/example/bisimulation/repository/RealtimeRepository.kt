package com.example.bisimulation.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

object RealtimeRepository {
    private const val TAG = "RealtimeRepository"

    private val realtimeDb = FirebaseDatabase.getInstance("https://bisimulation-default-rtdb.europe-west1.firebasedatabase.app")
    val activeUsersRef = realtimeDb.getReference("activeUsers")

    // Manages the online presence of the current user
    fun setupUserPresence(){
        //Increment the user count
        activeUsersRef.setValue(ServerValue.increment(1))

        //Tell the server to decrement the user count when it detects that we disconnected
        activeUsersRef.onDisconnect().setValue(ServerValue.increment(-1))
    }
}