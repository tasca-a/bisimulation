package com.example.bisimulation.repository

import android.util.Log
import com.example.bisimulation.game.OnConnectionSuccess
import com.example.bisimulation.game.OnRoomCreationSuccess
import com.example.bisimulation.utils.MatchmakingRoomModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirestoreRepository {
    private const val TAG = "FirestoreRepository"

    // One-shot queries
    suspend fun getName(userId: String): String? {
        val db = Firebase.firestore
        return try {
            db.collection("users")
                .document(userId)
                .get()
                .await()
                .data?.get("name").toString()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            ""
        }
    }

    suspend fun getSurname(userId: String): String? {
        val db = Firebase.firestore
        return try {
            db.collection("users")
                .document(userId)
                .get()
                .await()
                .data?.get("surname").toString()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            ""
        }
    }

    suspend fun getUsername(userId: String): String? {
        val db = Firebase.firestore
        return try {
            db.collection("users")
                .document(userId)
                .get()
                .await()
                .data?.get("username").toString()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            ""
        }
    }

    fun createRoom(roomId:String, room: MatchmakingRoomModel, listener: OnRoomCreationSuccess){
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).set(room).addOnSuccessListener {
            listener.roomCreationSuccess()
        }
    }

    fun connectPlayer2(roomId: String, room: MatchmakingRoomModel, listener: OnConnectionSuccess){
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(mapOf(
            "player2uid" to room.player2uid,
            "player2username" to room.player2username,
            "roomState" to room.roomState
        )).addOnSuccessListener {
            listener.connectionSuccess()
        }
    }

    // Realtime queries - to be observed
    fun getStatsReference(userId: String): DocumentReference {
        val db = Firebase.firestore
        return db.collection("stats").document(userId)
    }

    fun getRoomsReference(): Query {
        val db = Firebase.firestore

        // Maybe get userUid as parameter and make his room not visible?
        // This to impede the user to enter in a lobby with himself
        return db.collection("rooms").whereEqualTo("roomState", "LOBBY").orderBy("creationTime", Query.Direction.ASCENDING)
    }

    fun getLobbyReference(roomId: String): DocumentReference {
        val db = Firebase.firestore
        return db.collection("rooms").document(roomId)
    }
}