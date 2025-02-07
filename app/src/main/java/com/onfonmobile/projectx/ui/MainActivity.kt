package com.onfonmobile.projectx.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.ui.activities.LoginActivity
import com.onfonmobile.projectx.ui.di.SessionManager
import com.onfonmobile.projectx.ui.login.ProfileActivity
import com.onfonmobile.projectx.ui.user.Admin
import com.onfonmobile.projectx.ui.user.AdminViewModel
import com.onfonmobile.projectx.ui.user.AdminViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var progressBarContainer: FrameLayout
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var totalContributionsTextView: TextView
    private lateinit var sessionManager: SessionManager

    private val targetAmount = 400770 // Hardcoded target amount


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.topAppBar)
        progressBarContainer = findViewById(R.id.ussdProgressBarContainer)
         totalContributionsTextView = findViewById(R.id.groupWalletBalance)

        // Set up the toolbar
        setSupportActionBar(toolbar)

        // Initialize session manager
        sessionManager = SessionManager(this)


        val database = AppDatabase.getDatabase(this) // ✅ Get database instance
        val factory = AdminViewModelFactory(database) // ✅ Pass it to the factory
        adminViewModel =
            ViewModelProvider(this, factory)[AdminViewModel::class.java] // ✅ Use the factory

        // Fetch and display total contributions
        fetchTotalContributions()

        // Set up the navigation drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateNavigationHeader()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    showProgressBar()
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                R.id.nav_admin_page -> {
                    showProgressBar()
                    startActivity(Intent(this, Admin::class.java))
                    true
                }

                R.id.nav_settings -> {
                    sessionManager.logout()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }

        // Handle "Add Contribution" button click
        findViewById<MaterialButton>(R.id.addContributionButton).setOnClickListener {
            showProgressBar()
            initiateUssdCode()
        }

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    override fun onResume() {
        super.onResume()
        hideProgressBar() // ✅ Hide progress bar when returning
    }

    private fun initiateUssdCode() {
        val ussdCode = "*334#"
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(ussdCode)}"))
        startActivity(intent)
    }

    private fun showProgressBar() {
        progressBarContainer.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBarContainer.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            R.id.action_notifications -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateNavigationHeader() {
        val headerView = navigationView.getHeaderView(0) ?: return
        val userNameTextView = headerView.findViewById<TextView>(R.id.userName)
        userNameTextView.text = sessionManager.getUsername() ?: "Unknown User"
    }

    private fun fetchTotalContributions() {
        adminViewModel.getTotalContributionsForAllUsers { contributions ->
            val totalAmount = contributions.values.sum()

            // Format numbers with commas
            val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalAmount)
            val formattedTarget = NumberFormat.getNumberInstance(Locale.US).format(targetAmount)

            // Calculate progress percentage
            val progressPercentage =
                ((totalAmount.toFloat() / targetAmount) * 100).coerceIn(0f, 100f)
            val remainingPercentage = 100 - progressPercentage.toInt()

            // Calculate the remaining amount
            val remainingAmount = (targetAmount - totalAmount).coerceAtLeast(0.0)
            val formattedRemainingAmount =
                NumberFormat.getNumberInstance(Locale.US).format(remainingAmount)

            runOnUiThread {
                // Update total contributions & target amount
                totalContributionsTextView.text = "Ksh $formattedTotal"
                findViewById<TextView>(R.id.targetAmount).text = "Ksh $formattedTarget"

                // Update progress bar
                findViewById<LinearProgressIndicator>(R.id.progressIndicator).setProgress(
                    progressPercentage.toInt(),
                    true
                )

                // Update percentage & remaining amount text
                findViewById<TextView>(R.id.progressPercentage).text =
                    "$remainingPercentage% remaining (Ksh $formattedRemainingAmount)"
            }
        }
    }


}