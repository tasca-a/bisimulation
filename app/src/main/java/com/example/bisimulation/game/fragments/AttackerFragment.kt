package com.example.bisimulation.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.bisimulation.databinding.FragmentGameBinding
import com.example.bisimulation.game.GameViewModel
import com.example.bisimulation.game.LobbyViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AttackerFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel
    private val args: AttackerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        p1GameFragmentSetup(inflater, container)
        return binding.root
    }

    private fun p1GameFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}