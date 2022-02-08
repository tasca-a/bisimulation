package com.example.bisimulation.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

class FsGetIntEventListener(private val tobeUpdated: MutableLiveData<Int>, val field: String) : EventListener<DocumentSnapshot> {
    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null && value.exists()){
            try {
                tobeUpdated.value = value.getLong(field)?.toInt()
            } catch (e: java.lang.Exception) {
                tobeUpdated.value = 0
            }
        }
    }
}