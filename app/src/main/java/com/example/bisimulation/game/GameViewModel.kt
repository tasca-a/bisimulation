package com.example.bisimulation.game

import androidx.lifecycle.*
import com.example.bisimulation.game.fragments.LobbyFragmentArgs
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.utils.GameState
import com.example.bisimulation.utils.MatchmakingRoomModel
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _player1username = MutableLiveData<String>()
    private val _player2username = MutableLiveData<String>()
    private val _player1uid = MutableLiveData<String>()
    private val _player2uid = MutableLiveData<String>()
    private val _roomState = MutableLiveData(GameState.LOBBY)
    val player1username: LiveData<String> = _player1username
    val player2username: LiveData<String> = _player2username
    val player1uid: LiveData<String> = _player1uid
    val player2uid: LiveData<String> = _player2uid
    val roomState: LiveData<GameState> = _roomState

    fun processArguments(args: LobbyFragmentArgs) {
        // Process player 1 information
        if (args.player1uid.isNotEmpty()) {
            _player1uid.value = args.player1uid
            viewModelScope.launch{
                _player1username.value = FirestoreRepository.getUsername(args.player1uid)
            }
        }
        // Process player 2 information
        if (args.player2uid.isNotEmpty()) {
            _player2uid.value = args.player2uid
            viewModelScope.launch{
                _player2username.value = FirestoreRepository.getUsername(args.player2uid)
            }
        }
    }

    fun createRoom(username: String) {
        if (player1uid.value != null){
            val newRoom = MatchmakingRoomModel(
                player1uid = player1uid.value!!,
                player1username = username
            )
            FirestoreRepository.createRoom(newRoom)
        }
    }
}