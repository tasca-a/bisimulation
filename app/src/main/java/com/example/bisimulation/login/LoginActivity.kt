package com.example.bisimulation.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bisimulation.R
import com.example.bisimulation.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = activityLoginBindingSetup()

        binding.loginButton.setOnClickListener{
            if (logInChecks()){
                Toast.makeText(this , R.string.logginIn_Toast, Toast.LENGTH_SHORT).show()
                viewModel.logIn()
            }
        }
        viewModel.logInStatus.observe(this) { message ->
            Toast.makeText(this , message, Toast.LENGTH_SHORT).show()
        }

        setContentView(binding.root)
    }

    private fun activityLoginBindingSetup(): ActivityLoginBinding {
        // Set the ViewModel to the Activity
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Setup binding object to inflate the activity
        val binding = ActivityLoginBinding.inflate(layoutInflater)

        // Bind the viewModel in the layout to the viewModel class
        binding.viewModel = viewModel

        // Makes LiveData update the UI correctly
        binding.lifecycleOwner = this
        return binding
    }

    private fun logInChecks(): Boolean {
        var passed = true

        if (!isEmail(binding.emailEditText.text)){
            binding.emailEditText.error = "Please insert a valid email address."
            passed = false
        }
        if (binding.passwordEditText.text.isBlank()){
            binding.passwordEditText.error = "Please insert a password."
            passed = false
        }

        return passed
    }
    private fun isEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}