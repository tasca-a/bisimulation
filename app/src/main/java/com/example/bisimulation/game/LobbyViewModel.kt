package com.example.bisimulation.game

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bisimulation.callbacks.OnConnectionSuccess
import com.example.bisimulation.callbacks.OnRoomCreationSuccess
import com.example.bisimulation.model.*
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.repository.firestoreEventListeners.FsGetRoleEventListener
import com.example.bisimulation.repository.firestoreEventListeners.FsGetStatusEventListener
import com.example.bisimulation.repository.firestoreEventListeners.FsGetStringEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class LobbyViewModel : ViewModel(), OnRoomCreationSuccess, OnConnectionSuccess {
    var roomId: String = ""

    private val _p1username = MutableLiveData<String>()
    val p1username: LiveData<String> = _p1username

    private val _p2username = MutableLiveData<String>()
    val p2username: LiveData<String> = _p2username

    private val _lobbyStatus = MutableLiveData<GameState>()
    val lobbyStatus: LiveData<GameState> = _lobbyStatus

    private val _p1role = MutableLiveData<GameRole>()
    val p1role: LiveData<GameRole> = _p1role

    // PLAYER 1
    fun createRoom(uid: String, username: String) {
        // Use the player1 uid as the roomId, to prevent multiple room creation
        // from the same user and support a match log for the user in the future
        roomId = uid

        // Set the current user as player1
        _p1username.value = username

        // Set the default player1 role to attacker
        _p1role.value = GameRole.ATTACKER

        // Set the current lobby status
        _lobbyStatus.value = GameState.LOBBY

        // Create a new room
        val newRoom = Lobby(
            player1uid = uid,
            player1username = username,
            player1role = GameRole.ATTACKER,
            player2username = "...",
            roomState = GameState.LOBBY
        )

        FirestoreRepository.createRoom(roomId, newRoom, this)
    }

    override fun roomCreationSuccess() {
        // Start listening for player2
        FirestoreRepository.getLobbyReference(roomId).addSnapshotListener(
            FsGetStringEventListener(_p2username, "player2username")
        )

        // Start listening for room status
        FirestoreRepository.getLobbyReference(roomId).addSnapshotListener(
            FsGetStatusEventListener(_lobbyStatus)
        )
    }

    fun setP1Role(role: GameRole) {
        _p1role.value = role
        FirestoreRepository.setP1Role(roomId, role)
    }

    fun setRoomAsZombie() {
        FirestoreRepository.setRoomAsZombie(roomId)
    }

    // PLAYER 2
    fun getExistingRoom(roomId: String) {
        this.roomId = roomId

        // Get all information of an already existing room
        viewModelScope.launch {
            // roomId is the player1 uid
            _p1username.value = FirestoreRepository.getUsername(roomId)
        }

        // Start listening for room status
        FirestoreRepository.getLobbyReference(roomId).addSnapshotListener(
            FsGetStatusEventListener(_lobbyStatus),
        )

        FirestoreRepository.getLobbyReference(roomId).addSnapshotListener(
            FsGetRoleEventListener(_p1role)
        )
    }

    fun connectPlayer2(uid: String, username: String) {
        // Set the player as player2
        _p2username.value = username

        // Create a new room object to contain player2 info
        val player2 = Lobby(
            player2uid = uid,
            player2username = username,
            roomState = GameState.READY
        )

        FirestoreRepository.connectPlayer2(roomId, player2, this)
    }

    // Useless?
    override fun connectionSuccess() {
        Log.e("GameViewModel", "Connection successful! :D")
    }

    fun gameSetUp() {
        // Graph setup
//        val gl = graphSetup()//graphSetupL()
//        val gr = graphSetup()//graphSetupR()

        val gl = graphSetupJson("left"){ graphSetupL() }
        val gr = graphSetupJson("right"){ graphSetupR() }

        // It is always the attacker turn at the beginning
        val turnOf = GameRole.ATTACKER

        // Create a list of all the colors present in the two generated graphs
        val colorList = mutableListOf<Int>()
        gl.edges.forEach { edge ->
            if (edge.color !in colorList)
                colorList.add(edge.color)
        }
        gr.edges.forEach { edge ->
            if (edge.color !in colorList)
                colorList.add(edge.color)
        }

        // Select a random special color from the list of colors
        val sc = colorList.random()

        // Update to Firestore
        FirestoreRepository.setGraph(roomId, "leftGraph", gl)
        FirestoreRepository.setGraph(roomId, "rightGraph", gr)
        FirestoreRepository.setInitialConfig(
            roomId,
            turnOf,
            sc
        )
    }

    private fun graphSetupJson(direction: String, fallback: () -> Graph): Graph{
        var out: Graph
        if (jsonString.isEmpty()) {
            out = fallback.invoke()
        }
        else{
            try {
                out = Graph()
                val collectionType = object : TypeToken<List<GraphJson>>(){}.type
                val graph = Gson().fromJson<List<GraphJson>>(jsonString, collectionType)
                if (direction == "left"){
                    graph[0].edges.forEach {
                        out.addEdge(it)
                    }
                }
                if (direction == "right"){
                    graph[1].edges.forEach {
                        out.addEdge(it)
                    }
                }
            } catch (e: Exception){
                Log.e("LobbyViewModel", e.message.toString())
                out = fallback.invoke()
            }
        }
        return out
    }

    private var jsonString = ""
    fun readJsonFromAssets(context: Context) {
        try {
            val inputStream = context.assets.open("graphs.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            jsonString = String(buffer, Charset.forName("UTF-8"))
        } catch (e: Exception){
            Log.e("JsonParsing", e.message.toString())
        }
    }

    private fun graphSetup(): Graph {
        // Vertex parameters
        val MAX_VERTEX = 6
        val MIN_VERTEX = 4

        val nOfVertices = Random.nextInt(MIN_VERTEX, MAX_VERTEX + 1)

        // Max vertices on a side
        val sideSize = sqrt(nOfVertices.toFloat()).roundToInt()

        // Create the list of all vertices
        val vertexList = mutableListOf<Graph.Vertex>()

        var column = 0
        var row = 0
        for (i in 1..nOfVertices) {
            if (column < sideSize){
                vertexList.add(
                    Graph.Vertex(i, column + 1, row + 1)
                )
                column++
            } else {
                row++
                column = 0
                vertexList.add(
                    Graph.Vertex(i, column + 1, row + 1)
                )
                column++
            }
        }

        // For each vertex, create an edge with another random vertex
        val graph = Graph()
        for (vertex1 in vertexList) {
            val vertex2 = vertexList.filter { it != vertex1 }.random()
            val color = colorList.random()

            graph.addEdge(
                Graph.Edge(
                    vertex1, vertex2, color
                )
            )
        }

        return graph
    }

    private fun graphSetupL(): Graph {
        val graph = Graph()

        val e1 = Graph.Vertex(1, 2, 3)
        val e2 = Graph.Vertex(2, 1, 2)
        val e3 = Graph.Vertex(3, 3, 2)
        val e4 = Graph.Vertex(4, 1, 1)
        val e5 = Graph.Vertex(5, 3, 1)

        graph.addEdge(Graph.Edge(e1, e2, Color.RED))
        graph.addEdge(Graph.Edge(e1, e3, Color.RED))
        graph.addEdge(Graph.Edge(e2, e1, Color.GREEN))
        graph.addEdge(Graph.Edge(e3, e1, Color.GREEN))
        graph.addEdge(Graph.Edge(e2, e4, Color.BLUE))
        graph.addEdge(Graph.Edge(e3, e5, Color.BLACK))
        graph.addEdge(Graph.Edge(e5, e4, Color.BLUE))

        return graph
    }

    private fun graphSetupR(): Graph {
        val graph = Graph()

        val e1 = Graph.Vertex(1, 2, 3)
        val e2 = Graph.Vertex(2, 1, 2)
        val e3 = Graph.Vertex(3, 3, 2)
        val e4 = Graph.Vertex(4, 1, 1)
        val e5 = Graph.Vertex(5, 3, 1)

        graph.addEdge(Graph.Edge(e1, e2, Color.RED))
        graph.addEdge(Graph.Edge(e1, e3, Color.RED))
        graph.addEdge(Graph.Edge(e3, e1, Color.GREEN))
        graph.addEdge(Graph.Edge(e2, e4, Color.BLUE))
        graph.addEdge(Graph.Edge(e3, e5, Color.BLACK))
        graph.addEdge(Graph.Edge(e5, e4, Color.BLUE))
        graph.addEdge(Graph.Edge(e5, e1, Color.GREEN))

        return graph
    }

    companion object {
        val colorList = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.BLACK)
    }
}