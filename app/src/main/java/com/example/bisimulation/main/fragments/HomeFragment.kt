package com.example.bisimulation.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.databinding.FragmentHomeBinding
import com.example.bisimulation.main.SharedViewModel

class HomeFragment : Fragment(){
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentSetup(inflater, container)

        viewModel.name.observe(viewLifecycleOwner){
            binding.nameTextView.text = it
        }
        viewModel.surname.observe(viewLifecycleOwner){
            binding.surnameTextView.text = it
        }
        viewModel.username.observe(viewLifecycleOwner){
            binding.usernameTextView.text = it
        }

        return binding.root
    }

    private fun homeFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}