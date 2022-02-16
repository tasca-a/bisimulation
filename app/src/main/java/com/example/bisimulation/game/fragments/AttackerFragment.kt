package com.example.bisimulation.game.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.bisimulation.databinding.FragmentGameBinding
import com.example.bisimulation.game.GameViewModel
import com.example.bisimulation.model.Graph

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

        val leftGraph = Graph().apply {
            val e1 = Edge(2, 3)
            val e2 = Edge(1, 2)
            val e3 = Edge(3, 2)
            val e4 = Edge(1, 1)
            val e5 = Edge(3, 1)

            addVertex(Vertex(e1, e2, Color.RED))
            addVertex(Vertex(e1, e3, Color.RED))
            addVertex(Vertex(e2, e1, Color.GREEN))
            addVertex(Vertex(e3, e1, Color.GREEN))
            addVertex(Vertex(e2, e4, Color.BLUE))
            addVertex(Vertex(e3, e5))
            addVertex(Vertex(e5, e4, Color.BLUE))
        }

        val rightGraph = Graph().apply {
            val e1 = Edge(2, 3)
            val e2 = Edge(1, 2)
            val e3 = Edge(3, 2)
            val e4 = Edge(1, 1)
            val e5 = Edge(3, 1)

            addVertex(Vertex(e1, e2, Color.RED))
            addVertex(Vertex(e1, e3, Color.RED))
            addVertex(Vertex(e3, e1, Color.GREEN))
            addVertex(Vertex(e2, e4, Color.BLUE))
            addVertex(Vertex(e3, e5))
            addVertex(Vertex(e5, e4, Color.BLUE))
            addVertex(Vertex(e5, e1, Color.GREEN))
        }

        binding.leftGraphView.setGraph(leftGraph)
        binding.rightGraphView.setGraph(rightGraph)

        return binding.root
    }

    private fun attackerFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}