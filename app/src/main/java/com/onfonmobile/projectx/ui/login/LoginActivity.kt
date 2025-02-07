package com.onfonmobile.projectx.ui.activities

import RegisterViewModel
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.databinding.ActivityLoginBinding
import com.onfonmobile.projectx.ui.viewmodel.UserViewModelFactory
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.ui.MainActivity
import com.onfonmobile.projectx.ui.di.SessionManager
import com.onfonmobile.projectx.ui.register.RegistrationActivity
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SessionManager
        sessionManager = SessionManager(this)
        val username = sessionManager.getUsername()
        Log.d("LoginActivity", "Logged-in username: $username")

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Initialize the ViewModel
        val factory = UserViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        // Hide the loading GIF initially
        binding.loadingGif.visibility = View.GONE

        // Set login button listener
        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Hide input fields and button, show loading GIF
                binding.usernameInput.visibility = View.GONE
                binding.passwordInput.visibility = View.GONE
                binding.loginButton.visibility = View.GONE
                binding.loadingGif.visibility = View.VISIBLE
                Glide.with(this).load(R.drawable.progress).into(binding.loadingGif)

                // Authenticate user
                authenticateUser(username, password)
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerText.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
private fun authenticateUser(username: String, password: String) {
    val userDao = AppDatabase.getDatabase(applicationContext).userDao()

    lifecycleScope.launch {
        try {
            Log.d("LoginActivity", "Authenticating user: $username")
            val user = userDao.getUserByUsername(username)

            if (user != null && BCrypt.checkpw(password, user.password)) {
                Log.d("LoginActivity", "Login successful")

                // Get the user's role from the database
                val userRole = user.role // Assuming 'role' is a column in the User table

                // Create session with role
                sessionManager.createSession(username, user.id, userRole)

                // Delay transition to MainActivity for at least 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }, 2000) // 2-second delay for GIF animation

            } else {
                Log.d("LoginActivity", "Invalid credentials")
                Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()

                // Show inputs and button again, hide loading GIF
                binding.usernameInput.visibility = View.VISIBLE
                binding.passwordInput.visibility = View.VISIBLE
                binding.loginButton.visibility = View.VISIBLE
                binding.loadingGif.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Authentication failed: ${e.message}")
            Toast.makeText(this@LoginActivity, "Authentication error", Toast.LENGTH_SHORT).show()

            // Show inputs and button again, hide loading GIF
            binding.usernameInput.visibility = View.VISIBLE
            binding.passwordInput.visibility = View.VISIBLE
            binding.loginButton.visibility = View.VISIBLE
            binding.loadingGif.visibility = View.GONE
        }
    }
}

}
