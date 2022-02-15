package com.example.bisimulation.game.fragments

import android.content.pm.ActivityInfo
import androidx.fragment.app.Fragment

open class GameFragment : Fragment() {

    fun setLandscapeOrientation(){
        // Lock the fragment in landscape
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onDestroyView() {
        // Lock back the screen orientation to portrait once the game is finished
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onDestroyView()
    }
}