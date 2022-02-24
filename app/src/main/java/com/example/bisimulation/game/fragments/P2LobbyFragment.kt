package com.example.bisimulation.game.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.bisimulation.game.LobbyViewModel
import com.example.bisimulation.model.GameRole
import com.example.bisimulation.model.GameState

class P2LobbyFragment : Fragment() {
    private lateinit var binding: FragmentLobbyBinding
    private lateinit var viewModel: LobbyViewModel
    private val args: P2LobbyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        p2LobbyFragmentSetup(inflater, container)

        // Get lobby already existing information
        if (args.uid.isEmpty() || args.username.isEmpty() || args.roomId.isEmpty()) {
            // Impossible to create a lobby
            Toast.makeText(
                context,
                resources.getString(R.string.lobbyCreationError),
                Toast.LENGTH_SHORT
            ).show()
            val action = P2LobbyFragmentDirections.actionP2LobbyToPlayNow()
            findNavController().navigate(action)
        } else {
            viewModel.getExistingRoom(args.roomId)
            viewModel.connectPlayer2(args.uid, args.username)
        }

        // Listen to attacker/defender updates
        viewModel.p1role.observe(viewLifecycleOwner) { p1role ->
            binding.attackerSwitch.isChecked = (p1role == GameRole.ATTACKER)
        }

        // When lobby status changes to READY, enable startButton
        viewModel.lobbyStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                GameState.READY -> {
                    binding.startGameButton.text = resources.getString(R.string.lobbyWaitingToStart)

                    // Show the zombie-fy button after 10 seconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.zombiefyButton.visibility = View.VISIBLE
                    }, 10000)
                }

                // Start the game!
                GameState.PLAYING -> {
                    // Select the fragment to take based on the role of the player1
                    val action =
                        if (viewModel.p1role.value == GameRole.ATTACKER)
                            P2LobbyFragmentDirections.actionP2LobbyToDefensor(viewModel.roomId)
                        else
                            P2LobbyFragmentDirections.actionP2LobbyToAttacker(viewModel.roomId)

                    // Change screen orientation before the new gameFragment start
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                    // Start the game!
                    findNavController().navigate(action)
                }

                // If the lobby is a zombie, return to PlayNow
                GameState.ZOMBIE -> {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.lobbyZombieError),
                        Toast.LENGTH_SHORT
                    ).show()
                    val action = P2LobbyFragmentDirections.actionP2LobbyToPlayNow()
                    findNavController().navigate(action)
                }

                // Don't to anything
                else -> {}
            }
        }

        return binding.root
    }

    private fun p2LobbyFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(this)[LobbyViewModel::class.java]
        binding = FragmentLobbyBinding.inflate(inflater, container, false)
        binding.gameViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}