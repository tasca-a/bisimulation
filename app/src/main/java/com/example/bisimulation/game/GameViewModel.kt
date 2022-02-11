package com.example.bisimulation.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.repository.FsGetStringEventListener
import com.example.bisimulation.utils.GameState
import com.example.bisimulation.utils.MatchmakingRoomModel
import kotlinx.coroutines.launch

class GameViewModel : ViewModel(), OnRoomCreationSuccess, OnConnectionSuccess {
    private var roomId: String = ""

    private val _p1username = MutableLiveData<String>()
    val p1username: LiveData<String> = _p1username

    private val _p2username = MutableLiveData<String>()
    val p2username: LiveData<String> = _p2username

    private val _lobbyStatus = MutableLiveData<String>()
    val lobbyStatus: LiveData<String> = _lobbyStatus

    // PLAYER 1
    fun createRoom(uid: String, username: String) {
        // Use the player1 uid as the roomId, to prevent multiple room creation
        // from the same user and support a match log for the user in the future
        roomId = uid

        // Set the current user as player1
        _p1username.value = username

        // Set the current lobby status
        _lobbyStatus.value = GameState.LOBBY.toString()

        // Create a new room
        val newRoom = MatchmakingRoomModel(
            player1uid = uid,
            player1username = username,
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
            FsGetStringEventListener(_lobbyStatus, "roomState")
        )
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
            FsGetStringEventListener(_lobbyStatus, "roomState")
        )
    }

    fun connectPlayer2(uid: String, username: String) {
        // Set the player as player2
        _p2username.value = username

        // Create a new room object to contain player2 info
        val player2 = MatchmakingRoomModel(
            player2uid = uid,
            player2username = username,
            roomState = GameState.READY
        )

        FirestoreRepository.connectPlayer2(roomId, player2, this)
    }

    override fun connectionSuccess() {
        Log.e("GameViewModel", "Connection successful! :D")
    }
}

interface OnRoomCreationSuccess{
    fun roomCreationSuccess()
}

interface OnConnectionSuccess{
    fun connectionSuccess()
}