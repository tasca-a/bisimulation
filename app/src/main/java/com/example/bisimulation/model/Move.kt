package com.example.bisimulation.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

class Move(){
    var graph: String = ""
    var color: Int = 0
    var edge: Int = 0

    @ServerTimestamp
    val creationTime: Timestamp? = null

    constructor(graph: String, color: Int, edge: Int) : this() {
        this.graph = graph
        this.color = color
        this.edge = edge
    }
}