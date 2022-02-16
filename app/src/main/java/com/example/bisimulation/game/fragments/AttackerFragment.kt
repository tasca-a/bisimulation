package com.example.bisimulation.game.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.bisimulation.databinding.FragmentGameBinding
import com.example.bisimulation.game.GameViewModel
import com.example.bisimulation.game.views.GraphEventListener

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

        // Initial graph setup
        binding.leftGraphView.setGraph(viewModel.leftGraph)
        binding.rightGraphView.setGraph(viewModel.rightGraph)

        binding.leftGraphView.addGraphEventListener(object : GraphEventListener{
            override fun onNodeClicked(nodeId: Int) {
                Log.i("AttackerFragment", "Al che sx! :D $nodeId")
            }
        })

        binding.rightGraphView.addGraphEventListener(object : GraphEventListener{
            override fun onNodeClicked(nodeId: Int) {
                Log.i("AttackerFragment", "Al che dx! :D $nodeId")
            }
        })

        return binding.root
    }

    private fun attackerFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}