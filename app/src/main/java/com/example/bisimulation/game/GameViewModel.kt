package com.example.bisimulation.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.repository.FsGetStringEventListener
import com.example.bisimulation.utils.GameState
import com.example.bisimulation.utils.MatchmakingRoomModel

class GameViewModel : ViewModel(), OnRoomCreationSuccess {
    private var roomId: String = ""

    private val _p1username = MutableLiveData<String>()
    val p1username: LiveData<String> = _p1username

    private val _p2username = MutableLiveData<String>()
    val p2username: LiveData<String> = _p2username

    private val _lobbyStatus = MutableLiveData<String>()
    val lobbyStatus: LiveData<String> = _lobbyStatus

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
}

interface OnRoomCreationSuccess{
    fun roomCreationSuccess()
}