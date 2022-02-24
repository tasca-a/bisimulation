package com.example.bisimulation.repository.firestoreEventListeners

import androidx.lifecycle.MutableLiveData
import com.example.bisimulation.model.GameRole
import com.example.bisimulation.model.Move
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FsGetLastMoveEventListener(
    private val lastMove: MutableLiveData<Move>,
    private val moveList: MutableList<Move>,
    private val role: GameRole
) :
    EventListener<QuerySnapshot> {

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null) {
            try {
                // Discard the move if the timestamp has been created and you are the attacker
                // This is to prevent a bug in which the listener updates the value two times
                // in a row, one time when the move is just sent and another time when the move
                // receive the timestamp from the server
                val lastM = value.documents[0].toObject(Move::class.java)
                if (role == GameRole.ATTACKER && lastM?.from == GameRole.ATTACKER) {
                    if (lastM.creationTime != null) return
                }

                lastMove.value = lastM

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
