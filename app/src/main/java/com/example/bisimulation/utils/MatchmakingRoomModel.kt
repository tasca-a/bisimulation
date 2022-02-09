package com.example.bisimulation.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class MatchmakingRoomModel(
    val player1username: String = "",
    val player2username: String = "",
    val player1uid: String = "",
    val player2uid: String = "",
    val roomState: GameState = GameState.DONE,

    @ServerTimestamp
    val creationTime: Timestamp? = null
)