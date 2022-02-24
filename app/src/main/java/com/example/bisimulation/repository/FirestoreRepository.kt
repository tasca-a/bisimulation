package com.example.bisimulation.repository

import android.util.Log
import com.example.bisimulation.callbacks.OnConnectionSuccess
import com.example.bisimulation.callbacks.OnRoomCreationSuccess
import com.example.bisimulation.model.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object FirestoreRepository {
    private const val TAG = "FirestoreRepository"

    fun clearCache() {
        Firebase.firestore.clearPersistence()
    }

    // One-shot get queries
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

    // One-shot set queries
    fun setRoomAsDone(roomId: String) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(
            mapOf(
                "roomState" to GameState.DONE
            )
        )
    }

    fun setRoomAsZombie(roomId: String) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(
            mapOf(
                "roomState" to GameState.ZOMBIE
            )
        )
    }

    fun setRoomAsPlaying(roomId: String) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(
            mapOf(
                "roomState" to GameState.PLAYING
            )
        )
    }

    fun setP1Role(roomId: String, role: GameRole) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(
            mapOf(
                "player1role" to role
            )
        )
    }

    fun setInitialConfig(roomId: String, turnOf: GameRole, specialColor: Int) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(
            mapOf(
                "turnOf" to turnOf,
                "specialColor" to specialColor
            )
        )
    }

    fun setGraph(roomId: String, type: String, graph: Graph) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId)
            .collection("graphs").document(type).set(
                graph
            )
    }

    // One-shot lobby queries
    fun createRoom(roomId: String, room: Lobby, listener: OnRoomCreationSuccess) {
        val db = Firebase.firestore
        // Useless listener?
        db.collection("rooms").document(roomId).set(room).addOnSuccessListener {
            listener.roomCreationSuccess()
        }
        db.collection("rooms").document(roomId)
            .collection("moves").get().addOnCompleteListener {
                for (document in it.result.documents) {
                    document.reference.delete()
                }
            }
    }

    fun connectPlayer2(roomId: String, room: Lobby, listener: OnConnectionSuccess) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId).update(
            mapOf(
                "player2uid" to room.player2uid,
                "player2username" to room.player2username,
                "roomState" to room.roomState
            )
        ).addOnSuccessListener {
            // Useless?
            listener.connectionSuccess()
        }
    }

    // One-shot game queries
    fun sendMove(roomId: String, from: GameRole, move: Move) {
        val db = Firebase.firestore
        db.collection("rooms").document(roomId)
            .collection("moves").document().set(
                move
            )

        // Change turn
        when (from) {
            GameRole.ATTACKER -> db.collection("rooms").document(roomId).update(
                mapOf(
                    "turnOf" to GameRole.DEFENDER
                )
            )
            GameRole.DEFENDER -> db.collection("rooms").document(roomId).update(
                mapOf(
                    "turnOf" to GameRole.ATTACKER
                )
            )
            else -> {}
        }
    }

    fun addVictoryStat(userId: String) {
        val db = Firebase.firestore
        db.collection("stats").document(userId)
            .update("victories", FieldValue.increment(1))
    }

    fun addDefeatStat(userId: String) {
        val db = Firebase.firestore
        db.collection("stats").document(userId)
            .update("losses", FieldValue.increment(1))
    }

    // Realtime queries to be observed
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

    // Realtime queries to be observed while in game
    fun getLobbyReference(roomId: String): DocumentReference {
        val db = Firebase.firestore
        return db.collection("rooms").document(roomId)
    }

    fun getMovesReference(roomId: String): Query {
        val db = Firebase.firestore
        return db.collection("rooms").document(roomId).collection("moves")
            .orderBy("creationTime", Query.Direction.DESCENDING)
    }
}