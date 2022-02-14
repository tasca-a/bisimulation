package com.example.bisimulation.repository

import android.util.Log
import com.example.bisimulation.callbacks.OnConnectionSuccess
import com.example.bisimulation.callbacks.OnRoomCreationSuccess
import com.example.bisimulation.model.GameRole
import com.example.bisimulation.model.GameState
import com.example.bisimulation.model.Lobby
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

    fun createRoom(roomId:String, room: Lobby, listener: OnRoomCreationSuccess){
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).set(room).addOnSuccessListener {
            listener.roomCreationSuccess()
        }
    }

    fun setRoomAsZombie(roomId: String){
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(mapOf(
            "roomState" to GameState.ZOMBIE
        ))
    }

    fun setRoomAsPlaying(roomId: String){
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(mapOf(
            "roomState" to GameState.PLAYING
        ))
    }

    fun connectPlayer2(roomId: String, room: Lobby, listener: OnConnectionSuccess){
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(mapOf(
            "player2uid" to room.player2uid,
            "player2username" to room.player2username,
            "roomState" to room.roomState
        )).addOnSuccessListener {
            // Useless?
            listener.connectionSuccess()
        }
    }

    fun setP1Role(roomId: String, role: GameRole) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(mapOf(
            "player1role" to role
        ))
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
        return db.collection("rooms")
            .whereEqualTo("roomState", "LOBBY")
            .orderBy("creationTime", Query.Direction.ASCENDING)
    }

    fun getLobbyReference(roomId: String): DocumentReference {
        val db = Firebase.firestore
        return db.collection("rooms").document(roomId)
    }
}