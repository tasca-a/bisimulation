package com.example.bisimulation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.bisimulation.R
import com.example.bisimulation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivitySetup()

        //Toolbar and Drawer setup
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        setContentView(binding.root)
    }

    // Set up binding and viewModel of the MainActivity
    private fun mainActivitySetup() {
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = this
    }
}