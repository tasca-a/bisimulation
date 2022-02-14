package com.example.bisimulation.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bisimulation.R
import com.example.bisimulation.databinding.FragmentLobbyBinding
import com.example.bisimulation.game.LobbyViewModel
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.model.GameState

class P1LobbyFragment : Fragment() {
    private lateinit var binding: FragmentLobbyBinding
    private lateinit var viewModel: LobbyViewModel
    private val args: P1LobbyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        p1LobbyFragmentSetup(inflater, container)

        // Create the lobby
        if (args.uid.isEmpty() || args.username.isEmpty()){
            // Impossible to create a lobby
            Toast.makeText(context, resources.getString(R.string.lobbyCreationError), Toast.LENGTH_SHORT).show()
            val action = P1LobbyFragmentDirections.actionP1lobbyToPlayNow()
            findNavController().navigate(action)
        } else {
            viewModel.createRoom(args.uid, args.username)

            // Little animation effect
            val loadingAnimation = AnimationUtils.loadAnimation(context, R.anim.blink)
            binding.player2TextView.startAnimation(loadingAnimation)
        }

        // When lobby status changes to READY, enable startButton
        viewModel.lobbyStatus.observe(viewLifecycleOwner){ status ->
            when(status){
                // When the game is ready, show the startGameButton
                GameState.READY -> {
                    binding.startGameButton.isEnabled = true
                    binding.player2TextView.clearAnimation()
                }

                // If the lobby is a zombie, return to PlayNow
                GameState.ZOMBIE -> {
                    Toast.makeText(context, resources.getString(R.string.lobbyInactiveError), Toast.LENGTH_SHORT).show()
                    val action = P1LobbyFragmentDirections.actionP1lobbyToPlayNow()
                    findNavController().navigate(action)
                }

                // Don't do anything
                else -> {}
            }
        }

        // Play the game!
        // Maybe you can embed this listener in the XML?
        binding.startGameButton.setOnClickListener {
            // Set the status to PLAYING
            FirestoreRepository.setRoomAsPlaying(viewModel.roomId)

            // Start the game!
            val action = P1LobbyFragmentDirections.actionP1lobbyToP1Game(viewModel.roomId)
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun p1LobbyFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(requireActivity())[LobbyViewModel::class.java]
        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        binding.gameViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroy() {
        viewModel.setRoomAsZombie()
        super.onDestroy()
    }
}