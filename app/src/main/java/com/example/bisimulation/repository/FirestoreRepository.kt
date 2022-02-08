package com.example.bisimulation.repository

import android.util.Log
import com.google.firebase.firestore.DocumentReference
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

    // Realtime queries - to be observed
    fun getStatsReference(userId: String): DocumentReference {
        val db = Firebase.firestore
        return db.collection("stats").document(userId)
    }
}