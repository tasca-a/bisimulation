package com.example.bisimulation.activities.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.bisimulation.R
import com.example.bisimulation.activities.login.LoginActivity
import com.example.bisimulation.game.GameViewModel
import com.example.bisimulation.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivitySetup()

        // Return to login if the user disconnects
        sharedViewModel.auth.addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.sharedViewModel = sharedViewModel
        binding.lifecycleOwner = this
    }

    // Manage disconnections
    override fun onResume() {
        super.onResume()
        if (sharedViewModel.auth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}