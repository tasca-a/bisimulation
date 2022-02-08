package com.example.bisimulation.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.databinding.FragmentSettingsBinding
import com.example.bisimulation.activities.main.SharedViewModel

class SettingsFragment : Fragment(){
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsFragmentSetup(inflater, container)

        return binding.root
    }

    private fun settingsFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}