package com.example.bisimulation.game.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.fragment.app.Fragment

open class GameFragment : Fragment() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onDestroyView() {
        // Lock back the screen orientation to portrait once the game is finished
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onDestroyView()
    }
}