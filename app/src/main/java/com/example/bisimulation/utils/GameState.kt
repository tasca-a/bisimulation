package com.example.bisimulation.utils

enum class GameState(val message: String) {
    LOBBY("lobby"),
    READY("ready"),
    PLAYING("playing"),
    DONE("done")
}