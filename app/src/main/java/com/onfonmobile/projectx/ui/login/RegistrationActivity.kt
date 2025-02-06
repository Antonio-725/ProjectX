
package com.onfonmobile.projectx.ui.register

import RegisterViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.onfonmobile.projectx.databinding.ActivityRegistrationBinding
import com.onfonmobile.projectx.ui.MainActivity
import com.onfonmobile.projectx.ui.activities.LoginActivity
import com.onfonmobile.projectx.ui.viewmodel.UserViewModelFactory

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("RegistrationActivity", "Activity created")

        // Initialize the ViewModel with the RegisterViewModelFactory
        val factory = UserViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        Log.d("RegistrationActivity", "ViewModel initialized")

        setupListeners()
    }

    private fun setupListeners() {
        binding.registerButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val role = if (binding.userRadioButton.isChecked) "user" else "admin"

            Log.d("RegistrationActivity", "Register button clicked")

            // Check if username and password are blank
            if (username.isBlank() || password.isBlank()) {
                Log.d("RegistrationActivity", "Username or password is blank")
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("RegistrationActivity", "Attempting to register user: $username with role: $role")

            // Call the ViewModel to register the user
            viewModel.registerUser(username, password, role,
                onSuccess = {
                    Log.d("RegistrationActivity", "User registration successful")
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()

                    // Navigate to the MainActivity after successful registration
                    Log.d("RegistrationActivity", "Navigating to MainActivity")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()  // Finish the current activity so user can't navigate back to registration
                },
                onError = { error ->
                    Log.e("RegistrationActivity", "User registration failed: $error")
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                })
        }
    }
}