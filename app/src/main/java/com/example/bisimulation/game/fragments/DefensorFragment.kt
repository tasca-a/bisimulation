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

class DefensorFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel
    private val args: DefensorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        p2GameFragmentSetup(inflater, container)
        return binding.root
    }

    private fun p2GameFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}