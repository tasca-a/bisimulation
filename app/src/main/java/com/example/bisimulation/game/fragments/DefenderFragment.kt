package com.example.bisimulation.game.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bisimulation.R
import com.example.bisimulation.databinding.FragmentGameBinding
import com.example.bisimulation.game.GameViewModel
import com.example.bisimulation.game.views.GraphEventListener
import com.example.bisimulation.model.GameRole
import com.example.bisimulation.model.GameState
import com.example.bisimulation.model.Graph.Vertex

class DefenderFragment : GameFragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel
    private val args: DefenderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        defenderFragmentSetup(inflater, container)

        // Setup
        viewModel.roomSetup(args.roomId)

        setupListeners()

        return binding.root
    }


    private fun setupListeners() {
        viewModel.leftGraph.removeObservers(viewLifecycleOwner)
        viewModel.rightGraph.removeObservers(viewLifecycleOwner)
        viewModel.specialColor.removeObservers(viewLifecycleOwner)
        viewModel.turnOf.removeObservers(viewLifecycleOwner)
        viewModel.lastMove.removeObservers(viewLifecycleOwner)

        // Observe graph status changes
        viewModel.leftGraph.observe(viewLifecycleOwner) {
            if (it != null)
                binding.leftGraphView.updateGraph(it)
        }

        viewModel.rightGraph.observe(viewLifecycleOwner) {
            if (it != null)
                binding.rightGraphView.updateGraph(it)
        }

        // React to vertex clicks
        binding.leftGraphView.addGraphEventListener(object : GraphEventListener {
            override fun onVertexClicked(vertex: Vertex) {
                if (viewModel.turnOf.value == GameRole.DEFENDER)
                    viewModel.defenderClick("left", vertex)
            }
        })

        binding.rightGraphView.addGraphEventListener(object : GraphEventListener {
            override fun onVertexClicked(vertex: Vertex) {
                if (viewModel.turnOf.value == GameRole.DEFENDER)
                    viewModel.defenderClick("right", vertex)
            }
        })

        // Listen for special color
        viewModel.specialColor.observe(viewLifecycleOwner) { color ->
            binding.specialColor.background = color?.toDrawable()
        }

        // Listen for turn
        viewModel.turnOf.observe(viewLifecycleOwner) { turnOf ->
            if (turnOf == GameRole.DEFENDER) {
                binding.turnTextView.text = resources.getString(R.string.yourTurn_textView)
            } else {
                binding.turnTextView.text = resources.getString(R.string.notYourTurn_textView)
            }
        }

        // Listen for move color
        viewModel.lastMove.observe(viewLifecycleOwner) { move ->
            if (move == null) return@observe

            binding.lastMoveColor.background = move.color.toDrawable()

            if (move.graph == "left") {
                viewModel.setLeftEdge(move.vertex)
                binding.leftGraphView.updateGraph(viewModel.leftGraph.value!!)
                binding.leftGraphView.enableClick = false
                binding.rightGraphView.enableClick = true
            }

            if (move.graph == "right") {
                viewModel.setRightEdge(move.vertex)
                binding.rightGraphView.updateGraph(viewModel.rightGraph.value!!)
                binding.leftGraphView.enableClick = true
                binding.rightGraphView.enableClick = false
            }

            // Check victory!
            if (viewModel.turnOf.value == GameRole.DEFENDER)
                if (viewModel.checkDefenderVictory())
                    viewModel.setVictory()

        }

        // Listen to lobby state and react accordingly
        viewModel.lobbyStatus.observe(viewLifecycleOwner){ state ->
            // If you won, just wait a few seconds and exit.
            // If you lost, display the defeat, wait a few seconds and then exit
            if (state == GameState.DONE){
                if (!viewModel.victory) {
                    Toast.makeText(context, resources.getString(R.string.defeatText), Toast.LENGTH_SHORT).show()
                    binding.turnTextView.text = resources.getString(R.string.defeatText)
                    viewModel.updateStats("losses")
                } else{
                    Toast.makeText(context, resources.getString(R.string.victoryText), Toast.LENGTH_SHORT).show()
                    binding.turnTextView.text = resources.getString(R.string.victoryText)
                    viewModel.updateStats("victories")
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    lifecycleScope.launchWhenResumed {
                        findNavController().popBackStack()
                    }
                }, 5000)
            }
        }
    }

    private fun defenderFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}