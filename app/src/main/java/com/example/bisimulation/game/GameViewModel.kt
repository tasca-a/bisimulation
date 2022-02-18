package com.example.bisimulation.game

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bisimulation.model.GameRole
import com.example.bisimulation.model.Graph
import com.example.bisimulation.model.Graph.Edge
import com.example.bisimulation.model.Graph.Vertex
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.repository.FsGetGameEventListener
import com.example.bisimulation.repository.FsGetGraphEventListener

class GameViewModel() : ViewModel() {

    private var roomId: String = ""
    fun setRoomId(roomId: String){
        this.roomId = roomId

        //Setup all the listeners
        FirestoreRepository.getLobbyReference(roomId).addSnapshotListener(
            FsGetGameEventListener(
                _turnOf,
                _specialColor
            )
        )

        FirestoreRepository.getLobbyReference(roomId).collection("graphs")
            .document("leftGraph").addSnapshotListener(
                FsGetGraphEventListener(_leftGraph)
            )

        FirestoreRepository.getLobbyReference(roomId).collection("graphs")
            .document("rightGraph").addSnapshotListener(
                FsGetGraphEventListener(_rightGraph)
            )
    }

    fun click(graph: String, nodeId: Int) {
        if (graph == "left"){
            _leftGraph.value?.selectEdge(nodeId)
            FirestoreRepository.setGraph(roomId, "leftGraph", _leftGraph.value!!)
        } else {
            _rightGraph.value?.selectEdge(nodeId)
            FirestoreRepository.setGraph(roomId, "rightGraph", _rightGraph.value!!)
        }
    }

    private val _leftGraph = MutableLiveData<Graph>()
    private val _rightGraph = MutableLiveData<Graph>()
    private val _turnOf = MutableLiveData<GameRole>()
    private val _specialColor = MutableLiveData<Int>()
    val leftGraph: LiveData<Graph> = _leftGraph
    val rightGraph: LiveData<Graph> = _rightGraph
    val turnOf: LiveData<GameRole> = _turnOf
    val specialColor: LiveData<Int> = _specialColor
}
