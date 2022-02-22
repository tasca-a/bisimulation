package com.example.bisimulation.game.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.bisimulation.R
import com.example.bisimulation.databinding.FragmentGameBinding
import com.example.bisimulation.game.GameViewModel
import com.example.bisimulation.game.views.GraphEventListener
import com.example.bisimulation.model.GameRole
import com.example.bisimulation.model.Graph.Vertex

class AttackerFragment : GameFragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel
    private val args: AttackerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        attackerFragmentSetup(inflater, container)
        setLandscapeOrientation()

        // Setup
        viewModel.roomSetup(args.roomId)

        // Observe graph status changes
        viewModel.leftGraph.observe(viewLifecycleOwner) {
            if (it != null)
                binding.leftGraphView.updateGraph(it)
        }

        viewModel.rightGraph.observe(viewLifecycleOwner) {
            if (it != null)
                binding.rightGraphView.updateGraph(it)
        }

        // React to node clicks
        binding.leftGraphView.addGraphEventListener(object : GraphEventListener {
            override fun onVertexClicked(vertex: Vertex) {
                if (viewModel.turnOf.value == GameRole.ATTACKER) {
                    Log.i("AttackerFragment", "Al che sx! :D ${vertex.id}")
                    viewModel.attackerClick("left", vertex)
                }
            }
        })

        binding.rightGraphView.addGraphEventListener(object : GraphEventListener {
            override fun onVertexClicked(vertex: Vertex) {
                if (viewModel.turnOf.value == GameRole.ATTACKER) {
                    Log.i("AttackerFragment", "Al che dx! :D ${vertex.id}")
                    viewModel.attackerClick("right", vertex)
                }
            }
        })

        // Listen for special color
        viewModel.specialColor.observe(viewLifecycleOwner) { color ->
            binding.specialColor.background = color?.toDrawable()
        }

        // Listen for turn
        viewModel.turnOf.observe(viewLifecycleOwner) { turnOf ->
            if (turnOf == GameRole.ATTACKER) {
                binding.turnTextView.text = resources.getString(R.string.yourTurn_textView)
            } else {
                binding.turnTextView.text = resources.getString(R.string.notYourTurn_textView)
            }
        }

        // Listen for move color
        viewModel.lastMove.observe(viewLifecycleOwner){ move ->
            if (move == null) return@observe

            binding.lastMoveColor.background = move.color.toDrawable()

            if (move.graph == "left") {
                viewModel.setLeftEdge(move.vertex)
                binding.leftGraphView.updateGraph(viewModel.leftGraph.value!!)
            }

            if (move.graph == "right") {
                viewModel.setRightEdge(move.vertex)
                binding.rightGraphView.updateGraph(viewModel.rightGraph.value!!)
            }

            // Check if victory has been achieved
//            if (viewModel.checkVictory())
//                binding.turnTextView.text = "Victory!"
//            else
//                binding.turnTextView.text = "Ripperotty.."
        }

        return binding.root
    }

    private fun attackerFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}