package com.example.bisimulation.repository

import androidx.lifecycle.MutableLiveData
import com.example.bisimulation.model.GameState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

class FsGetStatusEventListener(private val tobeUpdated: MutableLiveData<GameState>, val field: String) :
    EventListener<DocumentSnapshot> {

    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null && value.exists()){
            try {
                val state = value.getString(field)
                tobeUpdated.value = GameState.valueOf(state!!)
                //tobeUpdated.value = value.getString(field)
            } catch (e: java.lang.Exception) {
                // In case of error, maintain the current status
                tobeUpdated.value = tobeUpdated.value
            }
        }
    }
}