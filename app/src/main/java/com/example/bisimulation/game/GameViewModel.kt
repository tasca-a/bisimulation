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
    fun setRoomId(roomId: String){
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

    // Todo: remove this
    // Perform click on a graph
    fun click(graph: String, nodeId: Int) {
        if (graph == "left"){

            // Game logic

            _leftGraph.value?.selectEdge(nodeId)
            FirestoreRepository.setGraph(roomId, "leftGraph", _leftGraph.value!!)
        } else {

            // Game logic

            _rightGraph.value?.selectEdge(nodeId)
            FirestoreRepository.setGraph(roomId, "rightGraph", _rightGraph.value!!)
        }
    }

    fun attackerClick(graph: String, nodeId: Int){
        if (graph == "left"){
            // Check if it is a valid strong move
            val selectedEdge = leftGraph.value?.edges?.find { it.selected }
            val pathVertex = leftGraph.value?.vertices?.find {
                it.from == selectedEdge && it.to.id == nodeId
            }

            // If the vertex exists, the move is strong
            if (pathVertex != null){
                FirestoreRepository.setMove(roomId, Move(
                    graph = "left",
                    color = pathVertex.color,
                    edge = nodeId
                ))
            }
        }
        if (graph == "right"){
            // Check if it is a valid strong move
            val selectedEdge = rightGraph.value?.edges?.find { it.selected }
            val pathVertex = rightGraph.value?.vertices?.find {
                it.from == selectedEdge && it.to.id == nodeId
            }

            // If the vertex exists, the move is strong
            if (pathVertex != null){
                FirestoreRepository.setMove(roomId, Move(
                    graph = "right",
                    color = pathVertex.color,
                    edge = nodeId
                ))
            }
        }
    }

    fun defenderClick(graph: String, nodeId: Int){
        if (graph == "left"){

        }
        if (graph == "right"){

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
