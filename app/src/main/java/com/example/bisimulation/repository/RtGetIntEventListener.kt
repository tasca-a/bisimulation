package com.example.bisimulation.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class RtGetIntEventListener(private val tobeUpdated: MutableLiveData<Int>) : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        tobeUpdated.value = snapshot.getValue<Int>()
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e("DB ERROR: ", error.toString())
    }
}