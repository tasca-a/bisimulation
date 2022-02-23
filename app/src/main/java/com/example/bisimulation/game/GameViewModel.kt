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

class GameViewModel : ViewModel() {

    private var roomId: String = ""

    // Set room ID and activate necessary listeners
    fun roomSetup(roomId: String) {
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
            FsGetLastMoveEventListener(_lastMove, moveList)
        )
    }

    // Manages edge clicks for the attacker
    fun attackerClick(graph: String, clickedVertex: Graph.Vertex) {
        // Variable to store the move config
        val config: Pair<Int, Int>

        // Current graph we are using
        val g: Graph

        if (graph == "left") {
            g = leftGraph.value!!
            config = Pair(
                clickedVertex.id,
                rightGraph.value?.getSelectedVertex()?.id ?: -1
            )
        } else {
            g = rightGraph.value!!
            config = Pair(
                leftGraph.value?.getSelectedVertex()?.id ?: -1,
                clickedVertex.id
            )
        }

        // Currently selected vertex
        val selectedVertex = g.vertices.find { it.selected }

        // Direct path from the currently selected vertex to the clicked one
        val pathEdge = g.edges.find {
            it.from == selectedVertex && it.to == clickedVertex
        }

        // If the edge exists, the move is strong
        if (pathEdge != null) {
            // Send the move to the server
            FirestoreRepository.sendMove(
                roomId, GameRole.ATTACKER, Move(
                    graph = graph,
                    color = pathEdge.color,
                    vertex = clickedVertex.id,
                    from = GameRole.ATTACKER,
                    config = config
                )
            )
        }
    }

    // Manages edge clicks for the defender
    fun defenderClick(graph: String, clickedVertex: Graph.Vertex) {
        // Variable to store the move config
        val config: Pair<Int, Int>

        // Current graph we are using
        val g: Graph

        if (graph == "left") {
            g = leftGraph.value!!
            config = Pair(
                clickedVertex.id,
                rightGraph.value?.getSelectedVertex()?.id ?: -1
            )
        } else {
            g = rightGraph.value!!
            config = Pair(
                leftGraph.value?.getSelectedVertex()?.id ?: -1,
                clickedVertex.id
            )
        }

        // Currently selected vertex
        val selectedVertex = g.vertices.find { it.selected }

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
            if (isSpecial)
                moveList.add(
                    Move(
                        graph,
                        specialColor.value!!,
                        clickedVertex.id,
                        GameRole.DEFENDER,
                        config
                    )
                )

            // If all the colors in a move are special color except one, the move is colored
            val isColor = filteredMoveColors.size == 1
            // The move has to be of the same color as the attacker move
            if (isColor && filteredMoveColors[0] == lastMove.value?.color)
                moveList.add(
                    Move(
                        graph,
                        filteredMoveColors[0],
                        clickedVertex.id,
                        GameRole.DEFENDER,
                        config
                    )
                )

            // Send the first available move it finds
            if (moveList.isNotEmpty()) {
                FirestoreRepository.sendMove(
                    roomId, GameRole.DEFENDER,
                    moveList[0]
                )
            }
        }
    }

    // Check if defender can or cannot respond to an attacker's move with a
    // weak move of the same color
    fun checkAttackerVictory(graph: String): Boolean {
        // Current graph we are checking (inverted because we are the defender)
        val g =
            if (graph == "left")
                rightGraph.value!!
            else
                leftGraph.value!!

        // Currently selected vertex
        val selectedVertex = g.vertices.find { it.selected } ?: return false

        // Find all paths from the selected vertex to any other vertex
        val allPaths = mutableListOf<List<IntArray>>()
        for (v in g.vertices) {
            allPaths.add(g.findAllPaths(selectedVertex, v))
        }

        // Compute all possible moves
        val moveList = mutableListOf<Move>()
        for (move in allPaths) {
            for (path in move) {
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
                if (isSpecial)
                    moveList.add(Move(graph, specialColor.value!!, 1, GameRole.NONE, Pair(0, 0)))

                // If all the colors in a move are special color except one, the move is colored
                val isColor = filteredMoveColors.size == 1
                if (isColor)
                    moveList.add(Move(graph, filteredMoveColors[0], 1, GameRole.NONE, Pair(0, 0)))
            }
        }

        // If there are no more moves of the same color of the attacker last move, Attacker wins
        val sameColorMoves = moveList.filter { it.color == lastMove.value?.color ?: false }
        return sameColorMoves.isEmpty()
    }

    fun checkDefenderVictory(): Boolean {
        // Currently selected vertex on each graph
        val leftSelectedVertex = leftGraph.value?.vertices?.find { it.selected }
        val rightSelectedVertex = rightGraph.value?.vertices?.find { it.selected }

        // Direct path from the currently selected vertex to every other vertex on each graph
        val leftPathEdges = leftGraph.value?.edges?.find {
            it.from == leftSelectedVertex
        }
        val rightPathEdges = rightGraph.value?.edges?.find {
            it.from == rightSelectedVertex
        }

        // If no move on each graph is found, the defender won
        val noAttackerMove = leftPathEdges == null && rightPathEdges == null

        var alreadyVisitedConfig = false
        moveList.forEach { move ->
            if (
                move.leftConfig == leftGraph.value?.getSelectedVertex()?.id &&
                move.rightConfig == rightGraph.value?.getSelectedVertex()?.id
            )
                alreadyVisitedConfig = true
        }

        return noAttackerMove || alreadyVisitedConfig
    }

    fun setLeftEdge(edge: Int) {
        _leftGraph.value?.selectVertex(edge)
    }

    fun setRightEdge(edge: Int) {
        _rightGraph.value?.selectVertex(edge)
    }

    private val moveList = mutableListOf<Move>()
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