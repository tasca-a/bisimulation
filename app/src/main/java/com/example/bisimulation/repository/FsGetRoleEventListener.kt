package com.example.bisimulation.repository

import androidx.lifecycle.MutableLiveData
import com.example.bisimulation.model.GameRole
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

class FsGetRoleEventListener(private val tobeUpdated: MutableLiveData<GameRole>) :
    EventListener<DocumentSnapshot> {

    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null && value.exists()){
            try {
                val state = value.getString("player1role")
                tobeUpdated.value = GameRole.valueOf(state!!)
            } catch (e: java.lang.Exception) {
                // In case of error, maintain the current status
                tobeUpdated.value = tobeUpdated.value
            }
        }
    }
}