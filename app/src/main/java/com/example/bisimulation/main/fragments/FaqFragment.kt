package com.example.bisimulation.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.databinding.FragmentFaqBinding
import com.example.bisimulation.main.SharedViewModel

class FaqFragment : Fragment(){
    private lateinit var binding: FragmentFaqBinding

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        faqFragmentSetup(inflater, container)

        return binding.root
    }

    private fun faqFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentFaqBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}