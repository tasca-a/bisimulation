package com.example.bisimulation.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bisimulation.R
import com.example.bisimulation.databinding.FragmentLobbyBinding
import com.example.bisimulation.game.GameViewModel
import com.example.bisimulation.game.OnRoomClearedSuccess
import com.example.bisimulation.utils.GameState

class P1LobbyFragment : Fragment() {
    private lateinit var binding: FragmentLobbyBinding
    private lateinit var viewModel: GameViewModel
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
            findNavController().navigate(R.id.playNowFragment)
        } else {
            viewModel.createRoom(args.uid, args.username)
        }

        // When lobby status changes to READY, enable startButton
        // TODO: Refactor to a when statement
        viewModel.lobbyStatus.observe(viewLifecycleOwner){ status ->
            if (status == GameState.READY.toString()){
                binding.startGameButton.isEnabled = true
            }
            // If the lobby is a zombie, return to PlayNow
            if (status == GameState.ZOMBIE.toString()){
                Toast.makeText(context, resources.getString(R.string.lobbyInactiveError), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.playNowFragment)
            }
        }

        // Play the game!
        // Maybe you can embed this listener in the XML?
        binding.startGameButton.setOnClickListener {
            // Set the status to PLAYING
            // TODO: Avvia la partita!
        }

        return binding.root
    }

    private fun p1LobbyFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        binding.gameViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroy() {
        viewModel.setRoomAsZombie()
        super.onDestroy()
    }
}