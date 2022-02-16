package com.example.bisimulation.game

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bisimulation.model.Graph
import com.example.bisimulation.model.Graph.Edge
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    val leftGraph = Graph().apply {
        val e1 = Edge(1,2, 3, true)
        val e2 = Edge(2,1, 2)
        val e3 = Edge(3,3, 2)
        val e4 = Edge(4,1, 1)
        val e5 = Edge(5,3, 1)

        addVertex(Vertex(e1, e2, Color.RED))
        addVertex(Vertex(e1, e3, Color.RED))
        addVertex(Vertex(e2, e1, Color.GREEN))
        addVertex(Vertex(e3, e1, Color.GREEN))
        addVertex(Vertex(e2, e4, Color.BLUE))
        addVertex(Vertex(e3, e5))
        addVertex(Vertex(e5, e4, Color.BLUE))
    }

    val rightGraph = Graph().apply {
        val e1 = Edge(1,2, 3)
        val e2 = Edge(2,1, 2)
        val e3 = Edge(3,3, 2, true)
        val e4 = Edge(4,1, 1)
        val e5 = Edge(5,3, 1)

        addVertex(Vertex(e1, e2, Color.RED))
        addVertex(Vertex(e1, e3, Color.RED))
        addVertex(Vertex(e3, e1, Color.GREEN))
        addVertex(Vertex(e2, e4, Color.BLUE))
        addVertex(Vertex(e3, e5))
        addVertex(Vertex(e5, e4, Color.BLUE))
        addVertex(Vertex(e5, e1, Color.GREEN))
    }

    private val _leftGraphChanged = MutableLiveData<Boolean>()
    val leftGraphChanged: LiveData<Boolean> = _leftGraphChanged
    fun selectEdge(id: Int){
        leftGraph.selectEdge(id)
        _leftGraphChanged.value = true
    }
}