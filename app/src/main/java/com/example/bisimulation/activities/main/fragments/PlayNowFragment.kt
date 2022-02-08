package com.example.bisimulation.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.databinding.FragmentPlayNowBinding
import com.example.bisimulation.activities.main.SharedViewModel

class PlayNowFragment : Fragment(){
    private lateinit var binding:  FragmentPlayNowBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        playNowFragmentSetup(inflater, container)

        //val query

        return binding.root
    }

    private fun playNowFragmentSetup(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = FragmentPlayNowBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}