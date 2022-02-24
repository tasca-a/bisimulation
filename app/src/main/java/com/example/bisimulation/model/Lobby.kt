package com.example.bisimulation.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

// Lobby model
// Useless parcelize?

@Parcelize
class Lobby(
    var player1username: String = "",
    var player2username: String = "",
    var player1uid: String = "",
    var player2uid: String = "",
    var player1role: GameRole = GameRole.ATTACKER,
    var roomState: GameState = GameState.DONE,

    @ServerTimestamp
    val creationTime: Timestamp? = null
) : Parcelable