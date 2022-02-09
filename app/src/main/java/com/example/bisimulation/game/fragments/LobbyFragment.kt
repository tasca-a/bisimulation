package com.example.bisimulation.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.bisimulation.databinding.FragmentLobbyBinding
import com.example.bisimulation.game.GameViewModel

class LobbyFragment : Fragment() {
    private lateinit var binding: FragmentLobbyBinding
    private lateinit var viewModel: GameViewModel
    private val args: LobbyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]

        lobbyFragmentSetup(inflater, container)

        // Process the passed arguments
        viewModel.processArguments(args)

        // Create a lobby as soon as the player one information are ready
        viewModel.player1username.observe(viewLifecycleOwner){ username ->
            if (viewModel.player2uid.value == null){
                viewModel.createRoom(username)
            }
        }

        return binding.root
    }

    private fun lobbyFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        binding.gameViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}