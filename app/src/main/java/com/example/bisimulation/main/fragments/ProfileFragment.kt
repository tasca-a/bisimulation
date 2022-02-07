package com.example.bisimulation.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.databinding.FragmentProfileBinding
import com.example.bisimulation.main.SharedViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileFragmentSetup(inflater, container)

        //Load user info
        viewModel.name.observe(viewLifecycleOwner){ binding.nameTextView.text = viewModel.name.value }
        viewModel.surname.observe(viewLifecycleOwner){ binding.surnameTextView.text = viewModel.surname.value }
        binding.emailAddressTextView.text = viewModel.email

        //TODO: numero di vittorie e sconfitte

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