package com.example.bisimulation.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

object RealtimeRepository {
    private val realtimeDb = FirebaseDatabase.getInstance("https://bisimulation-2ed25-default-rtdb.europe-west1.firebasedatabase.app/")
    val activeUsersListRef = realtimeDb.getReference("activeUsersList")

    // Manages the online presence of the current user
    fun setupUserPresence(userUid: String,username: String){
        // Add the user to the activeUserList
        activeUsersListRef.child(userUid).setValue(username)

        // Tell te server to remove the user from the list when it detects that we disconnected
        activeUsersListRef.child(userUid).onDisconnect().removeValue()
    }

    fun getActiveUsersListReference(): Query {
        return realtimeDb.reference.child("activeUsersList")
    }

}