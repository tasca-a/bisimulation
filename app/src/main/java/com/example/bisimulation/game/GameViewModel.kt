package com.example.bisimulation.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bisimulation.model.GameRole
import com.example.bisimulation.model.Graph
import com.example.bisimulation.model.Move
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.repository.FsGetGameEventListener
import com.example.bisimulation.repository.FsGetGraphEventListener
import com.example.bisimulation.repository.FsGetLastMoveEventListener

class GameViewModel() : ViewModel() {

    private var roomId: String = ""

    // Set room ID and activate necessary listeners
    fun setRoomId(roomId: String) {
        this.roomId = roomId

        //Setup all the listeners
        FirestoreRepository.getLobbyReference(roomId).addSnapshotListener(
            FsGetGameEventListener(
                _turnOf,
                _specialColor
            )
        )

        // Necessary to load the graphs when they are ready
        FirestoreRepository.getLobbyReference(roomId).collection("graphs")
            .document("leftGraph").addSnapshotListener(
                FsGetGraphEventListener(_leftGraph)
            )

        FirestoreRepository.getLobbyReference(roomId).collection("graphs")
            .document("rightGraph").addSnapshotListener(
                FsGetGraphEventListener(_rightGraph)
            )

        // Listen to moves
        FirestoreRepository.getMovesReference(roomId).addSnapshotListener(
            FsGetLastMoveEventListener(_lastMove)
        )
    }

    fun attackerClick(graph: String, nodeId: Int) {

        val selectedEdge =
            if (graph == "left")
                leftGraph.value?.edges?.find { it.selected }
            else
                rightGraph.value?.edges?.find { it.selected }

        val pathVertex =
            if (graph == "left")
                leftGraph.value?.vertices?.find {
                    it.from == selectedEdge && it.to.id == nodeId
                }
            else
                rightGraph.value?.vertices?.find {
                    it.from == selectedEdge && it.to.id == nodeId
                }

        // If the vertex exists, the move is strong
        if (pathVertex != null) {
            FirestoreRepository.setMove(
                roomId, GameRole.ATTACKER, Move(
                    graph = graph,
                    color = pathVertex.color,
                    edge = nodeId
                )
            )
        }
    }

    fun defenderClick(graph: String, nodeId: Int) {
        if (graph == "left") {

        }
        if (graph == "right") {

        }
    }

    fun setLeftEdge(edge: Int) {
        _leftGraph.value?.selectEdge(edge)
        //FirestoreRepository.setGraph(roomId, "leftGraph", _leftGraph.value!!)
    }

    fun setRightEdge(edge: Int) {
        _rightGraph.value?.selectEdge(edge)
        //FirestoreRepository.setGraph(roomId, "rightGraph", _rightGraph.value!!)
    }

    private val _leftGraph = MutableLiveData<Graph>()
    private val _rightGraph = MutableLiveData<Graph>()
    private val _turnOf = MutableLiveData<GameRole>()
    private val _specialColor = MutableLiveData<Int>()
    private val _lastMove = MutableLiveData<Move>()
    val leftGraph: LiveData<Graph> = _leftGraph
    val rightGraph: LiveData<Graph> = _rightGraph
    val turnOf: LiveData<GameRole> = _turnOf
    val specialColor: LiveData<Int> = _specialColor
    val lastMove: LiveData<Move> = _lastMove
}
