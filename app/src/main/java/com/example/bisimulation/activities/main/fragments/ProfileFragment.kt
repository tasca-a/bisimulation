package com.example.bisimulation.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.databinding.FragmentProfileBinding
import com.example.bisimulation.activities.main.SharedViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileFragmentSetup(inflater, container)
        return binding.root
    }

    private fun profileFragmentSetup(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}