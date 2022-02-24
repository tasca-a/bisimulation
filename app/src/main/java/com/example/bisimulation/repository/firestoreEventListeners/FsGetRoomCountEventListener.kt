package com.example.bisimulation.repository.firestoreEventListeners

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FsGetRoomCountEventListener(private val roomCount: MutableLiveData<Int>) :
    EventListener<QuerySnapshot> {

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        var count = 0
        if (value != null) {
            for (v in value) {
                count++
            }
        }
        roomCount.value = count
    }
}
