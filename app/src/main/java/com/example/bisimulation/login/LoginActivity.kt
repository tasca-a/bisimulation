package com.example.bisimulation.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bisimulation.R
import com.example.bisimulation.databinding.ActivityLoginBinding
import com.example.bisimulation.signup.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = activityLoginBindingSetup()

        // Log-in handling
        binding.loginButton.setOnClickListener {
            if (logInChecks()) {
                Toast.makeText(this, R.string.logginIn_Toast, Toast.LENGTH_SHORT).show()
                viewModel.logIn()
            }
        }
        viewModel.logInStatus.observe(this) { logged ->
            val message: String
            if (logged)
                message = resources.getString(R.string.logInSuccessful_Toast)
            else
                message = resources.getString(R.string.logInFailed_Toast)

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // "Remember me" functionality
        val sharedPreferences = this.getSharedPreferences("com.example.bisimulation", Context.MODE_PRIVATE)
        binding.rememberMeCheckBox.isChecked = sharedPreferences.getBoolean("rememberMe", false)
        viewModel.email = sharedPreferences.getString("email", "")!!
        viewModel.password = sharedPreferences.getString("password", "")!!
        binding.rememberMeCheckBox.setOnClickListener { setPreferences(sharedPreferences) }

        // Sign-up handling
        binding.registerClickHereTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }

    private fun setPreferences(sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("rememberMe", binding.rememberMeCheckBox.isChecked)
        if (binding.rememberMeCheckBox.isChecked) {
            editor
                .putString("email", viewModel.email)
                .putString("password", viewModel.password)
        }
        else {
            editor
                .putString("email", "")
                .putString("password", "")
        }
        editor.apply()
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

        // Clear previous errors
        binding.emailEditText.error = null
        binding.passwordEditText.error = null

        // Email check
        if (!isEmail(binding.emailEditText.text)) {
            binding.emailEditText.error = resources.getString(R.string.invalidEmailError)
            passed = false
        }

        // Password checks
        if (binding.passwordEditText.text.isBlank()) {
            binding.passwordEditText.error = resources.getString(R.string.emptyPasswordError)
            passed = false
        }
        if(binding.passwordEditText.text.contains(" ")){
            binding.passwordEditText.error = resources.getString(R.string.invalidPasswordError)
            passed = false
        }
        if (binding.passwordEditText.text.length < 6) {
            binding.passwordEditText.error = resources.getString(R.string.lengthPasswordError)
            passed = false
        }

        return passed
    }
    private fun isEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}