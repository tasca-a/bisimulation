package com.example.bisimulation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.bisimulation.R
import com.example.bisimulation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivitySetup()

        setContentView(binding.root)
    }

    // Set up binding and viewModel of the MainActivity
    private fun mainActivitySetup() {
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = this
    }
}