package com.example.bisimulation.repository.firestoreEventListeners

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

class FsGetStringEventListener(private val tobeUpdated: MutableLiveData<String>, val field: String) :
    EventListener<DocumentSnapshot> {

    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null && value.exists()){
            try {
                tobeUpdated.value = value.getString(field)
            } catch (e: java.lang.Exception) {
                tobeUpdated.value = ""
            }
        }
    }
}