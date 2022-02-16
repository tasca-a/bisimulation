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
import kotlin.random.Random

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

        binding.leftGraphView.setGraph(viewModel.leftGraph)
        binding.rightGraphView.setGraph(viewModel.rightGraph)

        viewModel.leftGraphChanged.observe(viewLifecycleOwner){
            binding.leftGraphView.setGraph(viewModel.leftGraph)
        }

        binding.button.setOnClickListener {
            viewModel.selectEdge(Random.nextInt(1, 6))
        }

        return binding.root
    }

    private fun attackerFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}