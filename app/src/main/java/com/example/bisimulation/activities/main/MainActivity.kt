package com.example.bisimulation.activities.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.bisimulation.R
import com.example.bisimulation.game.GameViewModel
import com.example.bisimulation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivitySetup()

        //TODO: osserva l'auth, se rileva una disconnessione ritorna alla login.

        // Call the ViewModel to manage user presence online
        // It is necessary to make this call in the Activity to assure that the function is
        // called everytime the activity is created. There must be a better way to separate
        // this but for now I can't find any
        sharedViewModel.username.observe(this){ username ->
            sharedViewModel.manageOnlinePresence(username)
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
        sharedViewModel.username.observe(this){
            header.findViewById<TextView>(R.id.username_textView).text = it
        }
        sharedViewModel.name.observe(this){
            header.findViewById<TextView>(R.id.name_textView).text = it
        }
        sharedViewModel.surname.observe(this){
            header.findViewById<TextView>(R.id.surname_textView).text = it
        }

        setContentView(binding.root)
    }

    // Set up binding and viewModel of the MainActivity
    private fun mainActivitySetup() {
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        //gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.sharedViewModel = sharedViewModel
        binding.lifecycleOwner = this
    }
}