package com.onfonmobile.projectx.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.ui.activities.LoginActivity
import com.onfonmobile.projectx.ui.di.SessionManager
import com.onfonmobile.projectx.ui.login.Admin
import com.onfonmobile.projectx.ui.login.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.topAppBar)
        progressBarContainer = findViewById(R.id.ussdProgressBarContainer) // Reference the ProgressBar
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.topAppBar)
        progressBarContainer = findViewById(R.id.ussdProgressBarContainer)

        // Set up the toolbar
        setSupportActionBar(toolbar)

        // Set up the navigation drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateNavigationHeader()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    showProgressBar()
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    hideProgressBar()
                    true
                }
                R.id.nav_admin_page -> {
                    showProgressBar()
                    val intent = Intent(this, Admin::class.java)
                    startActivity(intent)
                    hideProgressBar()
                    true
                }
                R.id.nav_settings -> { // Handle logout
                    val sessionManager = SessionManager(this)
                    sessionManager.logout() // Clear user session

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Prevent going back
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }


        // Handle "Add Contribution" button click
        val addContributionButton: MaterialButton = findViewById(R.id.addContributionButton)
        addContributionButton.setOnClickListener {
            showProgressBar() // Show progress bar while initiating USSD
            initiateUssdCode()
            hideProgressBar() // Hide it after completion
        }

        // Window insets listener
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun initiateUssdCode() {
        // Define the USSD code
        val ussdCode = "*334#"

        // Create an intent to launch the dialer
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${Uri.encode(ussdCode)}")

        // Start the dialer activity
        startActivity(intent)
    }

    private fun showProgressBar() {
        progressBarContainer.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBarContainer.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu resource
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                // Handle search action
                true
            }
            R.id.action_notifications -> {
                // Handle notifications action
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun updateNavigationHeader() {
        val headerView = navigationView.getHeaderView(0) // Get header layout
        val userNameTextView = headerView.findViewById<TextView>(R.id.userName)

        val username = getLoggedInUsername() // Fetch username
        userNameTextView.text = username // Set the username in the header
    }
    private fun getLoggedInUsername(): String {
        val sessionManager = SessionManager(this)
        return sessionManager.getUsername() ?: "Unknown User"
    }

}
