package com.example.bisimulation.repository.firestoreEventListeners

import androidx.lifecycle.MutableLiveData
import com.example.bisimulation.model.Move
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FsGetLastMoveEventListener(
    private val lastMove: MutableLiveData<Move>,
    private val moveList: MutableList<Move>
) :
    EventListener<QuerySnapshot> {

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null) {
            try {
                lastMove.value = value.documents[0].toObject(Move::class.java)

                moveList.clear()
                value.documents.forEachIndexed { index, documentSnapshot ->
                    if (index != 0)
                        moveList.add(documentSnapshot.toObject(Move::class.java)!!)
                }
            } catch (e: Exception) {
                lastMove.value = lastMove.value
            }
        }
    }

}
