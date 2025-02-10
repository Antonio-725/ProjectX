package com.onfonmobile.projectx.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.ui.Adapters.MonthlyContributionAdapter
import com.onfonmobile.projectx.ui.activities.LoginActivity
import com.onfonmobile.projectx.ui.login.ProfileActivity
import com.onfonmobile.projectx.ui.user.Admin
import com.onfonmobile.projectx.ui.user.AdminViewModel
import com.onfonmobile.projectx.ui.user.AdminViewModelFactory
import com.onfonmobile.projectx.ui.di.SessionManager
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var progressBarContainer: View
    private lateinit var totalContributionsTextView: TextView
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var progressPercentageTextView: TextView
    private lateinit var targetAmountTextView: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MonthlyContributionAdapter

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
        progressIndicator = findViewById(R.id.progressIndicator)
        progressPercentageTextView = findViewById(R.id.progressPercentage)
        targetAmountTextView = findViewById(R.id.targetAmount)
        recyclerView = findViewById(R.id.monthlyContributions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up toolbar & session manager
        setSupportActionBar(toolbar)
        sessionManager = SessionManager(this)

        // Initialize ViewModel
        val database = AppDatabase.getDatabase(this)
        val factory = AdminViewModelFactory(database)
        adminViewModel = ViewModelProvider(this, factory)[AdminViewModel::class.java]

        // Fetch and display data
        refreshData()

        // Set up navigation drawer
        setupNavigation()

        // Handle button click
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
        hideProgressBar()
    }

    private fun setupNavigation() {
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        updateNavigationHeader()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> openActivity(ProfileActivity::class.java)
                R.id.nav_admin_page -> openActivity(Admin::class.java)
                R.id.nav_settings -> logout()
                else -> false
            }
        }
    }

    private fun openActivity(activityClass: Class<*>): Boolean {
        showProgressBar()
        startActivity(Intent(this, activityClass))
        return true
    }

    private fun logout(): Boolean {
        sessionManager.logout()
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
        finish()
        return true
    }

    private fun initiateUssdCode() {
        val ussdCode = "*334#"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(ussdCode)}")))
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
            R.id.action_search, R.id.action_notifications -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateNavigationHeader() {
        navigationView.getHeaderView(0)?.findViewById<TextView>(R.id.userName)?.text =
            sessionManager.getUsername() ?: "Unknown User"
    }

    private fun fetchTotalContributions() {
        adminViewModel.getTotalContributionsForAllUsers { contributions ->
            val totalAmount = contributions.values.sum()

            // Format numbers with commas
            val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalAmount)
            val formattedTarget = NumberFormat.getNumberInstance(Locale.US).format(targetAmount)

            // Calculate progress percentage
            val progressPercentage =
                ((totalAmount.toFloat() / targetAmount) * 100).coerceIn(0f, 100f).toInt()
            val remainingPercentage = 100 - progressPercentage

            // Calculate remaining amount
            val formattedRemainingAmount =
                NumberFormat.getNumberInstance(Locale.US).format((targetAmount - totalAmount).coerceAtLeast(
                    0.0
                ))

            // Update UI
            runOnUiThread {
                totalContributionsTextView.text = "Ksh $formattedTotal"
                targetAmountTextView.text = "Ksh $formattedTarget"
                progressIndicator.setProgress(progressPercentage, true)
                progressPercentageTextView.text =
                    "$remainingPercentage% remaining (Ksh $formattedRemainingAmount)"
            }
        }
    }

    private fun fetchMonthlyContributions() {
        adminViewModel.getMonthlyContributionSummary { summaryList ->
            runOnUiThread {
                adapter = MonthlyContributionAdapter(summaryList)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun refreshData() {
        fetchTotalContributions()
        fetchMonthlyContributions()
    }
}
