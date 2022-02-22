package com.example.bisimulation.game

import android.util.Log
import androidx.core.graphics.toColor
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

    // Manages edge clicks for the attacker
    fun attackerClick(graph: String, clickedVertex: Graph.Vertex) {
        // Currently selected vertex
        val selectedVertex =
            if (graph == "left")
                leftGraph.value?.vertices?.find { it.selected }
            else
                rightGraph.value?.vertices?.find { it.selected }

        // Direct path from the currently selected vertex to the clicked one
        val pathEdge =
            if (graph == "left")
                leftGraph.value?.edges?.find {
                    it.from == selectedVertex && it.to == clickedVertex
                }
            else
                rightGraph.value?.edges?.find {
                    it.from == selectedVertex && it.to == clickedVertex
                }

        // If the edge exists, the move is strong
        if (pathEdge != null) {
            // Send the move to the server
            FirestoreRepository.sendMove(
                roomId, GameRole.ATTACKER, Move(
                    graph = graph,
                    color = pathEdge.color,
                    vertex = clickedVertex.id
                )
            )
        }
    }

    // Manages edge clicks for the defender
    fun defenderClick(graph: String, clickedVertex: Graph.Vertex) {
        // Currently selected vertex
        val selectedVertex =
            if (graph == "left")
                leftGraph.value?.vertices?.find { it.selected }
            else
                rightGraph.value?.vertices?.find { it.selected }

        // Current graph we are using
        val g =
            if (graph == "left")
                leftGraph.value!!
            else
                rightGraph.value!!

        // Find all paths from the selected vertex to the clicked one
        val paths = g.findAllPaths(selectedVertex!!, clickedVertex)

        // Compute all possible moves
        val moveList = mutableListOf<Move>()
        for (path in paths) {
            val moveColors = mutableListOf<Int>()
            path.forEachIndexed { index, vertex ->
                if (index == path.size - 1) return@forEachIndexed

                val edge = g.edges.find {
                    it.from.id == vertex && it.to.id == path[index + 1]
                }

                if (edge != null)
                    moveColors.add(edge.color)
            }

            // Filter all moves based on the special color
            val filteredMoveColors = moveColors.filter { it != specialColor.value }

            // If all the colors in a move are the special color, the move is special
            val isSpecial = filteredMoveColors.isEmpty()
            if (isSpecial) {
                Log.i("GameViewModel", "Special move! :D")
                moveList.add(Move(graph, specialColor.value!!, clickedVertex.id))
            }

            // If all the colors in a move are special color except one, the move is colored
            val isColor = filteredMoveColors.size == 1
            if (isColor){
                Log.i("GameViewModel", "Move of color: ${filteredMoveColors[0].toString()}")
                moveList.add(Move(graph, filteredMoveColors[0], clickedVertex.id))
            }

            // Send the first available move it finds
            if (moveList.isNotEmpty()){
                FirestoreRepository.sendMove(roomId, GameRole.DEFENDER,
                    moveList[0]
                )
            }
        }
    }

    // colors -> 0 = white, 1 = gray, 2 = black
    inner class BfsV(val id: Int, var color: Int = 0, var d: Int = 0, var p: BfsV? = null)

    fun setLeftEdge(edge: Int) {
        _leftGraph.value?.selectVertex(edge)
    }

    fun setRightEdge(edge: Int) {
        _rightGraph.value?.selectVertex(edge)
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