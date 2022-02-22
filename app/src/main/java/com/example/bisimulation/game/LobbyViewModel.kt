package com.example.bisimulation.game

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bisimulation.callbacks.OnConnectionSuccess
import com.example.bisimulation.callbacks.OnRoomCreationSuccess
import com.example.bisimulation.model.GameRole
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.repository.FsGetStatusEventListener
import com.example.bisimulation.repository.FsGetStringEventListener
import com.example.bisimulation.model.GameState
import com.example.bisimulation.model.Graph
import com.example.bisimulation.model.Lobby
import com.example.bisimulation.repository.FsGetRoleEventListener
import kotlinx.coroutines.launch
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

    fun setRoomAsZombie(){
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
        val gl = graphSetupL()
        val gr = graphSetupR()

        // Make a random initial config
        gl.selectVertex(Random.nextInt(1, 3))
        gr.selectVertex(Random.nextInt(1, 3))

        // It is always the attacker turn at the beginning
        val turnOf = GameRole.ATTACKER

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