package com.onfonmobile.projectx.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.ui.MainActivity
import com.onfonmobile.projectx.ui.di.SessionManager
import com.onfonmobile.projectx.ui.user.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var usernameTextView: TextView
    private lateinit var roleChip: Chip
    private lateinit var totalContributionTextView: TextView
    private lateinit var remainingAmountTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressPercentageTextView: TextView
    private lateinit var viewModel: ProfileViewModel
    private lateinit var roleText: TextView

    private val YEARLY_TARGET = 66795  // Fixed yearly contribution target

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Apply window insets (to avoid overlaps with system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(R.layout.activity_profile) // Make sure this matches your layout file

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Find Views
        usernameTextView = findViewById(R.id.nameLabel)
        roleText = findViewById(R.id.roleText)

        totalContributionTextView = findViewById(R.id.totalContributionValueTextView)
        remainingAmountTextView = findViewById(R.id.remainingAmountValueTextView)

        progressBar = findViewById(R.id.contributionProgressBar)
        progressPercentageTextView = findViewById(R.id.progressPercentageTextView)

        // Set initial values
        usernameTextView.text = "Username: ${sessionManager.getUsername() ?: "Unknown User"}"
        roleText.text = "Role: ${sessionManager.getUserRole() ?: "Unknown Role"}"
        progressPercentageTextView.text = "0.00%"

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Get user ID from session
        val userId = sessionManager.getUserId()
        Log.d("ProfileActivity", "User ID from session: $userId")

        if (userId != null) {
            Log.d("ProfileActivity", "Fetching contribution for userId: $userId")
            viewModel.getTotalContribution(userId) { total ->
                Log.d("ProfileActivity", "Contribution callback received. Amount: $total")

                // Calculate remaining amount
                val remainingAmount = YEARLY_TARGET - total
                val progressPercentage = (total / YEARLY_TARGET.toFloat()) * 100

                // Update UI with formatted decimal values
                totalContributionTextView.text = "Ksh ${"%.2f".format(total)}"
                remainingAmountTextView.text = "Ksh ${"%.2f".format(remainingAmount)}"
                progressBar.progress = progressPercentage.toInt()
                progressPercentageTextView.text = "%.2f%%".format(progressPercentage)
            }
        } else {
            Log.d("ProfileActivity", "UserId is null, displaying zero contribution")
            totalContributionTextView.text = "Total Contributions: Ksh 0.00"
            remainingAmountTextView.text = "Remaining Amount: Ksh 66,795.00"
            progressBar.progress = 0
            progressPercentageTextView.text = "0.00%"
        }
    }
}
