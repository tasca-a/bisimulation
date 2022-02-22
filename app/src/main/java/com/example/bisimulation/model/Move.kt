package com.example.bisimulation.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class Move(){
    var graph: String = ""
    var color: Int = 0
    var vertex: Int = 0

    @ServerTimestamp
    val creationTime: Timestamp? = null

    constructor(graph: String, color: Int, vertex: Int) : this() {
        this.graph = graph
        this.color = color
        this.vertex = vertex
    }
}