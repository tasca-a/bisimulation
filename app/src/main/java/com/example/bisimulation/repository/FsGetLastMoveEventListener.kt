package com.example.bisimulation.repository

import androidx.lifecycle.MutableLiveData
import com.example.bisimulation.model.Graph
import com.example.bisimulation.model.Move
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FsGetLastMoveEventListener(
    private val lastMove: MutableLiveData<Move>
) :
    EventListener<QuerySnapshot> {

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null) {
            try {
                lastMove.value = value.documents[0].toObject(Move::class.java)
            } catch (e: Exception) {
                lastMove.value = lastMove.value
            }
        }
    }

}
