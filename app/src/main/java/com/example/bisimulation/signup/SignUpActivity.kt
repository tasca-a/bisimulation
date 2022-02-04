package com.example.bisimulation.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.bisimulation.R
import com.example.bisimulation.databinding.ActivitySignUpBinding
import com.example.bisimulation.main.MainActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignUpBindingSetup()

        // Sign-up handling
        binding.signUpButton.setOnClickListener {
            if (signUpChecks()){
                Toast.makeText(this, R.string.signinUp_Toast, Toast.LENGTH_SHORT).show()
                viewModel.signUp()
            }
        }
        viewModel.signUpStatus.observe(this){ status ->
            val message = resources.getString(status)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            if (status == R.string.signUpSuccessful_Toast){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        setContentView(binding.root)
    }

    private fun signUpChecks(): Boolean {
        var passed = true

        // Clear previous errors
        binding.nameEditText.error = null
        binding.surnameEditText.error = null
        binding.emailSignUpEditText.error = null
        binding.usernameEditText.error = null
        binding.passwordSignUpEditText.error = null
        binding.confirmPasswordEditText.error = null

        // Name check
        if (binding.nameEditText.text.isBlank() || binding.nameEditText.text.contains(" ")) {
            binding.nameEditText.error = resources.getString(R.string.invalidNameError)
            passed = false
        }

        // Surname check
        if (binding.surnameEditText.text.isBlank() || binding.surnameEditText.text.contains(" ")) {
            binding.surnameEditText.error = resources.getString(R.string.invalidSurnameError)
            passed = false
        }

        // Email check
        if (!isEmail(binding.emailSignUpEditText.text)) {
            binding.emailSignUpEditText.error = resources.getString(R.string.invalidEmailError)
            passed = false
        }

        // Username check
        if (binding.usernameEditText.text.isBlank() || binding.usernameEditText.text.contains(" ")) {
            binding.usernameEditText.error = resources.getString(R.string.invalidUsernameError)
            passed = false
        }

        // Password checks
        if (binding.passwordSignUpEditText.text.isBlank()) {
            binding.passwordSignUpEditText.error = resources.getString(R.string.emptyPasswordError)
            passed = false
        }
        if (binding.passwordSignUpEditText.text.contains(" ")) {
            binding.passwordSignUpEditText.error = resources.getString(R.string.invalidPasswordError)
            passed = false
        }
        if (binding.passwordSignUpEditText.text.length < 6) {
            binding.passwordSignUpEditText.error = resources.getString(R.string.lengthPasswordError)
            passed = false
        }
        if (binding.passwordSignUpEditText.text.equals(binding.confirmPasswordEditText.text)){
            binding.confirmPasswordEditText.error = resources.getString(R.string.pwNotMatchError)
            passed = false
        }

        return passed
    }

    private fun isEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun activitySignUpBindingSetup() {
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
}