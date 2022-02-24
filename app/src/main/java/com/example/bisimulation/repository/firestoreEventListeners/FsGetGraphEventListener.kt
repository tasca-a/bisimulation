package com.example.bisimulation.repository.firestoreEventListeners

import androidx.lifecycle.MutableLiveData
import com.example.bisimulation.model.Graph
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

class FsGetGraphEventListener(private val graph: MutableLiveData<Graph>) :
    EventListener<DocumentSnapshot> {

    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null && value.exists()){
            try {
                graph.value = value.toObject(Graph::class.java)
            } catch (e: Exception){
                graph.value = graph.value
            }
        }
    }

}
