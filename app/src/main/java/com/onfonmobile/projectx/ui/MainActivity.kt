package com.onfonmobile.projectx.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.google.android.material.snackbar.Snackbar
import com.onfonmobile.projectx.Firestore.Helpers.Worker.NotificationScheduler
import com.onfonmobile.projectx.Firestore.Helpers.Worker.SyncScheduler
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.ui.Adapters.MonthlyContributionAdapter
import com.onfonmobile.projectx.ui.activities.LoginActivity
import com.onfonmobile.projectx.ui.login.ProfileActivity
import com.onfonmobile.projectx.ui.user.Admin
import com.onfonmobile.projectx.ui.user.AdminViewModel
import com.onfonmobile.projectx.ui.user.AdminViewModelFactory
import com.onfonmobile.projectx.ui.di.SessionManager
import com.onfonmobile.projectx.ui.di.SharedViewModel
import com.onfonmobile.projectx.ui.login.HelpandSupport
import com.onfonmobile.projectx.ui.login.Notifications
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
    private lateinit var sharedViewModel: SharedViewModel

    private val targetAmount = 104400
    private var notificationCount = 0
    // Hardcoded target amount

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

        // Observe user and contribution changes to trigger sync
        database.userDao().getAllUsersLiveData().observe(this) { users ->
            if (users.isNotEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    SyncScheduler.triggerImmediateSync(this)
                }, 3000) // Delayed sync for testing
            }
        }

        database.contributionDao().getAllContributionsLiveData().observe(this) { contributions ->
            if (contributions.isNotEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    SyncScheduler.triggerImmediateSync(this)
                }, 3000) // Delayed sync for testing
            }
        }

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

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Observe notification count
        sharedViewModel.notificationCount.observe(this) { count ->
            updateNotificationCounter(count)
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
                R.id.nav_help->openActivity(HelpandSupport::class.java)
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

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
//        return true
//    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        val notificationItem = menu.findItem(R.id.action_notifications)
        notificationItem.actionView?.setOnClickListener {
            onOptionsItemSelected(notificationItem)
        }
        updateNotificationCounter(notificationCount)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                refreshData() // Refresh all data when clicked
                true
            }
            R.id.action_notifications -> {
               // val intent = Intent(this, Notifications::class.java)
                updateNotificationCounter(0)
                NotificationScheduler.scheduleNotification(this)
              //  startActivity(intent)
                true

            }
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
                NumberFormat.getNumberInstance(Locale.US).format(
                    (targetAmount - totalAmount).coerceAtLeast(
                        0.0
                    )
                )

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
    private fun updateNotificationCounter(count: Int) {
        notificationCount = count
        val menuItem = toolbar.menu.findItem(R.id.action_notifications)
        val badge = menuItem.actionView?.findViewById<TextView>(R.id.notification_badge)
        if (count > 0) {
            badge?.text = count.toString()
            badge?.visibility = View.VISIBLE
        } else {
            badge?.visibility = View.GONE
        }
    }
    fun onNewNotificationReceived() {
        notificationCount++
        updateNotificationCounter(notificationCount)
    }


    private fun refreshData() {
        // Show progress bar
        showProgressBar()

        // Show a Snackbar with a stylish UI when refreshing starts
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "Refreshing data...",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setBackgroundTint(
            ContextCompat.getColor(
                this,
                R.color.facebook_blue
            )
        ) // Custom background color
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white)) // Text color
        snackbar.show()

        // Fetch total contributions
        adminViewModel.getTotalContributionsForAllUsers { contributions ->
            val totalAmount = contributions.values.sum()
            val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalAmount)
            val formattedTarget = NumberFormat.getNumberInstance(Locale.US).format(targetAmount)
            val progressPercentage =
                ((totalAmount.toFloat() / targetAmount) * 100).coerceIn(0f, 100f).toInt()
            val remainingPercentage = 100 - progressPercentage
            val formattedRemainingAmount = NumberFormat.getNumberInstance(Locale.US)
                .format((targetAmount - totalAmount).coerceAtLeast(0.0))

            runOnUiThread {
                totalContributionsTextView.text = "Ksh $formattedTotal"
                targetAmountTextView.text = "Ksh $formattedTarget"
                progressIndicator.setProgress(progressPercentage, true)
                progressPercentageTextView.text =
                    "$remainingPercentage% remaining (Ksh $formattedRemainingAmount)"

                // Fetch Monthly Contributions
                adminViewModel.getMonthlyContributionSummary { summaryList ->
                    runOnUiThread {
                        adapter = MonthlyContributionAdapter(summaryList)
                        recyclerView.adapter = adapter

                        // Hide progress bar
                        hideProgressBar()

                        // Dismiss the first Snackbar
                        snackbar.dismiss()

                        // Show a success Snackbar with an action button
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Data refreshed successfully!",
                            Snackbar.LENGTH_LONG
                        )
                            .setBackgroundTint(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.accent_color
                                )
                            )
                            .setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                            .setAction("OK") { /* Do nothing, just dismiss */ }
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.white
                                )
                            )
                            .show()
                    }
                }
            }
        }
    }
}

