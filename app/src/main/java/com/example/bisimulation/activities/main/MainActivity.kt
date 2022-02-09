package com.example.bisimulation.activities.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.bisimulation.R
import com.example.bisimulation.databinding.ActivityMainBinding
import com.example.bisimulation.repository.RealtimeRepository

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivitySetup()

        // Call the ViewModel to manage user presence online
        // It is necessary to make this call in the Activity to assure that the function is
        // called everytime the activity is created. There must be a better way to separate
        // this but now I can't find any
        viewModel.username.observe(this){ username ->
            viewModel.manageOnlinePresence(username)
        }

        // Toolbar and Drawer setup
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Load user info in the drawer
        val navigationView = binding.navView
        val header = navigationView.getHeaderView(0)
        viewModel.username.observe(this){
            header.findViewById<TextView>(R.id.username_textView).text = it
        }
        viewModel.name.observe(this){
            header.findViewById<TextView>(R.id.name_textView).text = it
        }
        viewModel.surname.observe(this){
            header.findViewById<TextView>(R.id.surname_textView).text = it
        }

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