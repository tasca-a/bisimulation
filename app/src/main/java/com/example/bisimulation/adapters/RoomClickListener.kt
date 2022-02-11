package com.example.bisimulation.adapters

import androidx.navigation.NavDirections

interface RoomClickListener {
    fun navigate(action: NavDirections)
}