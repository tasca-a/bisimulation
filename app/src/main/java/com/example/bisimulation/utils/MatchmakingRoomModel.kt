package com.example.bisimulation.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class MatchmakingRoomModel(
    var player1username: String = "",
    var player2username: String = "",
    var player1uid: String = "",
    var player2uid: String = "",
    var roomState: GameState = GameState.DONE,

    @ServerTimestamp
    val creationTime: Timestamp? = null
)