package com.onfonmobile.projectx.ui.user

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.databinding.ActivityAdmin2Binding
import com.onfonmobile.projectx.ui.Adapters.GroupMembersAdapter
//import com.onfonmobile.projectx.ui.dialogs.ContributionDialog
//import com.onfonmobile.projectx.ui.adapters.GroupMembersAdapter
import com.onfonmobile.projectx.ui.login.ContributionDialog

class Admin : AppCompatActivity() {
    private lateinit var binding: ActivityAdmin2Binding
    private lateinit var adapter: GroupMembersAdapter
    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdmin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupDashboardActions()
        loadUsers()
    }

    private fun setupViewModel() {
        val db = AppDatabase.getDatabase(this)
        viewModel = ViewModelProvider(this, AdminViewModelFactory(db)).get(AdminViewModel::class.java)
    }

    private fun setupRecyclerView() {
        adapter = GroupMembersAdapter(
            userList = mutableListOf(),
            onUserClicked = { user ->
                // Show user details or edit dialog
                showUserDetailsDialog(user)
            },
            onTotalContributionFetched = { userId, callback ->
                viewModel.getTotalContribution(userId) { totalContribution ->
                    callback(totalContribution)
                }
            }
        )

        binding.apply {
            // Setup RecyclerView within membersCard
            groupMembersRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@Admin)
                adapter = this@Admin.adapter
                setHasFixedSize(true)
            }
        }
    }

    private fun setupDashboardActions() {
        binding.apply {
            // Savings Updates
            updateSavingsButton.setOnClickListener {
                showContributionDialog()
            }

            // User Management
            viewEditUsersButton.setOnClickListener {
                showUserManagementDialog()
            }

            // Statistics
            calculateStatsButton.setOnClickListener {
                navigateToStatistics()
            }

            // Reminders
            sendRemindersButton.setOnClickListener {
                showRemindersDialog()
            }
        }
    }

    private fun loadUsers() {
        viewModel.loadUsers { users ->
            adapter.setUsers(users)
        }
    }

    private fun showContributionDialog() {
        ContributionDialog(this) { username, amount, date ->
            viewModel.saveContribution(
                username = username,
                amount = amount,
                date = date,
                onSuccess = {
                    showSuccessMessage("Contribution saved successfully")
                    loadUsers() // Refresh the list
                },
                onError = { error ->
                    showErrorMessage(error)
                }
            )
        }.show()
    }

    private fun showUserManagementDialog() {
        // TODO: Implement user management dialog
        // This could be a bottom sheet or new activity
        showUnderDevelopmentMessage("User Management")
    }

    private fun navigateToStatistics() {
        // TODO: Navigate to statistics screen
        // Intent to StatisticsActivity
        showUnderDevelopmentMessage("Statistics")
    }

    private fun showRemindersDialog() {
        // TODO: Implement reminders functionality
        showUnderDevelopmentMessage("Reminders")
    }

    private fun showUserDetailsDialog(user: User) {
        // TODO: Show user details in a bottom sheet or dialog
        showUnderDevelopmentMessage("User Details")
    }

    // Helper functions for showing messages
    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }

    private fun showUnderDevelopmentMessage(feature: String) {
        Toast.makeText(this, "$feature feature coming soon!", Toast.LENGTH_SHORT).show()
    }
}