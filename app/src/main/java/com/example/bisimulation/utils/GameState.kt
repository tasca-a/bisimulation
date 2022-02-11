package com.example.bisimulation.utils

// Maybe it's possible to remove the strings? Try it
enum class GameState(val message: String) {
    LOBBY("lobby"),
    READY("ready"),
    PLAYING("playing"),
    DONE("done"),
    ZOMBIE("zombie")
}