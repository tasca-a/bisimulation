package com.example.bisimulation.repository.firestoreEventListeners

import androidx.lifecycle.MutableLiveData
import com.example.bisimulation.model.GameRole
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException

class FsGetGameEventListener(
    private val _turnOf: MutableLiveData<GameRole>,
    private val _specialColor: MutableLiveData<Int>
) : EventListener<DocumentSnapshot> {

    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        if (value != null && value.exists()){
            try {
                val turn = value.getString("turnOf")
                _turnOf.value = GameRole.valueOf(turn!!)
                _specialColor.value = value.getLong("specialColor")?.toInt()
            } catch (e: Exception){
                // In case of error, maintain the current status
                _turnOf.value = _turnOf.value
                _specialColor.value = _specialColor.value
            }
        }
    }
}
