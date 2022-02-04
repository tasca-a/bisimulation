package com.example.bisimulation.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.databinding.FragmentBugReportBinding
import com.example.bisimulation.main.SharedViewModel

class BugReportFragment : Fragment() {
    private lateinit var binding: FragmentBugReportBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bugReportFragmentSetup(inflater, container)

        return binding.root
    }

    private fun bugReportFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentBugReportBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}